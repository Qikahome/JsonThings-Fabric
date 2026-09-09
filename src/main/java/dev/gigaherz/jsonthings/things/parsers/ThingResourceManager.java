package dev.gigaherz.jsonthings.things.parsers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import dev.gigaherz.jsonthings.RunnableQueue;
import dev.gigaherz.jsonthings.util.CustomPackType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ThingResourceManager
{
    private static class InstanceHolder
    {
        private static final ThingResourceManager instance = new ThingResourceManager();
    }

    public static final Logger LOGGER = LogUtils.getLogger();

    public static ThingResourceManager instance()
    {
        return InstanceHolder.instance;
    }

    private static final Set<String> disabledPacks = Sets.newHashSet();

    private RunnableQueue mainThreadExecutor;

    private final ReloadableResourceManager resourceManager;
    private final FolderRepositorySource folderPackFinder;
    // Fabric 无 AddPackFindersEvent：thingpack 源先收集，beginLoading 时再构建 PackRepository。
    private final List<RepositorySource> packSources = Lists.newArrayList();
    private PackRepository packList;

    private final List<ThingParser<?>> thingParsers = Lists.newArrayList();
    private final Map<String, ThingParser<?>> parsersMap = Maps.newHashMap();

    private ThingResourceManager()
    {
        resourceManager = new ReloadableResourceManager(CustomPackType.THINGS);
        // 1.21.1 vanilla FolderRepositorySource 需要 SymlinkValidator 参数；沿用上游 Neo 读取
        // <gameDir>/allowed_symlinks.txt 的解析方式（Fabric 用游戏目录替代 FMLPaths.GAMEDIR）。
        var validator = LevelStorageSource.parseValidator(
                FabricLoader.getInstance().getGameDir().resolve("allowed_symlinks.txt"));
        folderPackFinder = new FolderRepositorySource(getThingPacksLocation(), CustomPackType.THINGS, PackSource.DEFAULT, validator);
        packSources.add(folderPackFinder);
    }

    public synchronized <TParser extends ThingParser<?>> TParser registerParser(TParser parser)
    {
        if (parsersMap.containsKey(parser.getThingType()))
            throw new IllegalStateException("There is already a parser registered for type " + parser.getThingType());
        thingParsers.add(parser);
        resourceManager.registerReloadListener(parser);
        parsersMap.put(parser.getThingType(), parser);
        return parser;
    }

    public Path getThingPacksLocation()
    {
        Path thingpacks = FabricLoader.getInstance().getGameDir().resolve("thingpacks");
        if (!Files.exists(thingpacks) && !thingpacks.toFile().mkdirs())
            throw new ThingParseException("Could not create thingspacks directory! Please create the directory yourself, or make sure the name is not taken by a file and you have permission to create directories.");
        return thingpacks;
    }

    /**
     * Call during mod construction **without enqueueWork**!
     */
    public synchronized void addPackFinder(RepositorySource finder)
    {
        packSources.add(finder);
    }

    /**
     * Call during mod construction **without enqueueWork**!
     */
    public synchronized void addResourceReloadListener(PreparableReloadListener listener)
    {
        resourceManager.registerReloadListener(listener);
    }

    private static final CompletableFuture<Unit> RESOURCE_RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);

    public CompletableFuture<ThingResourceManager> beginLoading()
    {
        packList = new PackRepository(packSources.toArray(new RepositorySource[0]));
        packList.reload();

        loadConfig();

        mainThreadExecutor = new RunnableQueue();

        return resourceManager
                .createReload(Util.backgroundExecutor(), mainThreadExecutor, RESOURCE_RELOAD_INITIAL_TASK, packList.openAllSelected())
                .done()
                .whenComplete((unit, throwable) -> {
                    if (throwable != null)
                    {
                        resourceManager.close();
                    }
                })
                .thenRun(mainThreadExecutor::finish)
                .thenApply((unit) -> this);
    }

    public void waitForLoading(CompletableFuture<ThingResourceManager> loaderFuture)
    {
        try
        {
            //DSLHelpers.debugDumpBindings();

            while (!loaderFuture.isDone())
            {
                if (!mainThreadExecutor.runQueue())
                    mainThreadExecutor.waitForTasks();
            }

            mainThreadExecutor.runQueue();

            // 仅等待异步资源加载结束；finishLoading 由调用方（JsonThings.onInitialize）显式调用一次，
            // 避免 builder 二次注册（Fabric RegistrySync 会因同一对象重复注册而报错）。
            loaderFuture.get();
        }
        catch (InterruptedException e)
        {
            LOGGER.error("Thingpack loader future interrupted!");
        }
        catch (ExecutionException e)
        {
            Throwable pCause = e.getCause();
            throw new ReportedException(CrashReport.forThrowable(pCause, "Error loading thingpacks"));
        }
    }

    public void finishLoading()
    {
        thingParsers.forEach(ThingParser::finishLoading);
    }

    public PackRepository getRepository()
    {
        return packList;
    }

    public void onConfigScreenSave()
    {
        disabledPacks.clear();
        disabledPacks.addAll(packList.getAvailableIds());
        disabledPacks.removeAll(packList.getSelectedIds());
        saveConfig();
    }

    public void saveConfig()
    {
        JsonArray disabled = new JsonArray();
        disabledPacks.forEach(disabled::add);
        JsonArray order = new JsonArray();
        packList.getSelectedIds().forEach(order::add);
        JsonObject obj = new JsonObject();
        obj.add("disabled", disabled);
        obj.add("order", order);
        String json = (new Gson()).toJson(obj);
        try (FileOutputStream stream = new FileOutputStream(getConfigFile());
             Writer w = new OutputStreamWriter(stream, StandardCharsets.UTF_8))
        {
            w.write(json);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void loadConfig()
    {
        File configFile = getConfigFile();
        List<String> orderList = new ArrayList<>();
        if (configFile.exists())
        {
            try (FileInputStream stream = new FileInputStream(configFile);
                 Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8))
            {
                JsonObject obj = (new Gson()).fromJson(reader, JsonObject.class);
                JsonArray disabled = obj.get("disabled").getAsJsonArray();
                disabledPacks.clear();
                disabled.forEach(element -> disabledPacks.add(element.getAsString()));

                JsonArray order = obj.get("order").getAsJsonArray();
                order.forEach(element -> orderList.add(element.getAsString()));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        for (String s : packList.getAvailableIds())
        {
            if (!orderList.contains(s) && !disabledPacks.contains(s))
                orderList.add(s);
        }

        packList.setSelected(orderList);
    }

    private File getConfigFile()
    {
        return FabricLoader.getInstance().getConfigDir().resolve("jsonthings-thingpacks.json").toFile();
    }
}
