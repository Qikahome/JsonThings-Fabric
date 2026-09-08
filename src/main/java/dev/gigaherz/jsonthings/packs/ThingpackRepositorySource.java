package dev.gigaherz.jsonthings.packs;

import com.mojang.logging.LogUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * 把 gameDir/thingpacks 下的包（与 ThingResourceManager 解析 things/ 用的是同一批文件夹）
 * 作为 required 包注入 Minecraft 自带的资源系统。
 *
 * <p>对应上游 Forge 的 AddPackFindersEvent(CLIENT_RESOURCES / SERVER_DATA) 注入。
 * Fabric 1.20.1 没有该事件，参考 Open-Loader 的做法：mixin Fabric 的
 * {@code ModResourcePackCreator}（客户端资源与服务端数据包仓库共同的注入点），
 * 在其 loadPacks 之后追加本源产出的包。required=true 保证无需玩家手动勾选即生效。
 *
 * <p>这样 thingpack 根目录里的 assets/（模型、贴图、语言）与 data/（配方、标签、战利品表）
 * 能真正被游戏使用，而不只被 JsonThings 的 things/ 解析器读取。
 */
public class ThingpackRepositorySource implements RepositorySource
{
    public static final Logger LOGGER = LogUtils.getLogger();

    private final PackType packType;

    public ThingpackRepositorySource(PackType packType)
    {
        this.packType = packType;
    }

    @Override
    public void loadPacks(Consumer<Pack> packConsumer)
    {
        Path root = FabricLoader.getInstance().getGameDir().resolve("thingpacks");
        if (!Files.isDirectory(root))
            return;

        try (Stream<Path> children = Files.list(root))
        {
            children.filter(Files::isDirectory).forEach(dir -> {
                String folderName = dir.getFileName().toString();
                final String packId = "thingpack:" + folderName;
                Pack pack = Pack.readMetaAndCreate(
                        packId,
                        Component.literal(packId),
                        true,
                        id -> new PathPackResources(id, dir, false),
                        packType,
                        Pack.Position.BOTTOM,
                        PackSource.DEFAULT);
                if (pack == null)
                {
                    LOGGER.warn("Thingpack folder '{}' has no valid pack.mcmeta; skipped as {}.", dir, packType);
                    return;
                }
                packConsumer.accept(pack);
                LOGGER.debug("Injected thingpack '{}' as {}.", packId, packType);
            });
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to scan thingpacks folder {}", root, e);
        }
    }
}
