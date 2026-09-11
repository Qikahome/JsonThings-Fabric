package dev.gigaherz.jsonthings;

import com.mojang.logging.LogUtils;
import dev.gigaherz.jsonthings.api.ThingPlugin;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.parsers.*;
import dev.gigaherz.jsonthings.things.scripting.ScriptParser;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.item.FluidBucketWrapper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * JsonThings Fabric 主入口（对应上游 Forge 版 {@code JsonThings} 的 common 部分）。
 *
 * <p>Fabric 无 mod 事件总线，各阶段的映射：
 * <ul>
 *   <li>构造器 + FMLConstructModEvent → {@link #onInitialize()}：创建 parser、注册 things pack 源、同步加载 thingpacks；</li>
 *   <li>NewRegistryEvent(finishLoading) → onInitialize 内按序调用 parser 的 finishLoading/registerValues；</li>
 *   <li>AddPackFindersEvent(SERVER_DATA) → Fabric 无 world datapack 注入通道（数据包内容暂不注入世界）；</li>
 *   <li>ClientHandlers(FMLClientSetupEvent/颜色/渲染层) → {@link JsonThingsClient}（必须分开，
 *       否则专用服务端加载 main 入口时会因引用客户端类而失败）。</li>
 * </ul>
 */
public class JsonThings implements ModInitializer
{
    public static final String MODID = "jsonthings";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static BlockParser blockParser;
    public static ItemParser itemParser;
    public static FluidParser fluidParser;
    public static EnchantmentParser enchantmentParser;
    public static FoodParser foodParser;
    public static ShapeParser shapeParser;
    public static TierParser tierParser;
    public static FluidTypeParser fluidTypeParser;
    public static ArmorMaterialParser armorMaterialParser;
    public static CreativeModeTabParser creativeModeTabParser;
    public static MobEffectInstanceParser mobEffectInstanceParser;
    public static BlockSetTypeParser blockSetTypeParser;
    public static SoundEventParser soundEventParser;
    public static SoundTypeParser soundTypeParser;

    /** 声明了 {@code "jsonthings"} 入口点的 mod 扩展，见 {@link ThingPlugin}。 */
    private static List<LoadedPlugin> thingPlugins = List.of();

    private record LoadedPlugin(String modId, ThingPlugin plugin)
    {
    }

    @Override
    public void onInitialize()
    {
        ThingRegistries.initRegistries();

        var manager = ThingResourceManager.instance();

        if (rhinoPresent())
        {
            ScriptParser.enable(manager);
        }

        blockParser = manager.registerParser(new BlockParser());
        itemParser = manager.registerParser(new ItemParser());
        fluidParser = manager.registerParser(new FluidParser());
        enchantmentParser = manager.registerParser(new EnchantmentParser());
        foodParser = manager.registerParser(new FoodParser());
        shapeParser = manager.registerParser(new ShapeParser());
        tierParser = manager.registerParser(new TierParser());
        fluidTypeParser = manager.registerParser(new FluidTypeParser());
        armorMaterialParser = manager.registerParser(new ArmorMaterialParser());
        creativeModeTabParser = manager.registerParser(new CreativeModeTabParser());
        mobEffectInstanceParser = manager.registerParser(new MobEffectInstanceParser());
        blockSetTypeParser = manager.registerParser(new BlockSetTypeParser());
        soundEventParser = manager.registerParser(new SoundEventParser());
        soundTypeParser = manager.registerParser(new SoundTypeParser());

        // 各 mod 内嵌的 things/ 目录也作为 thingpack 源。
        manager.addPackFinder(ModResourcesFinder.buildPackFinder());

        // 扩展点（解析前）：Fabric 各 mod 的 main 入口点是并行调用的，"先注册 parser/thing 类型、
        // 再解析 thingpack"这个顺序无法保证，故由这里主动回调声明了 "jsonthings" 入口点的 mod。
        thingPlugins = loadThingPlugins(manager);

        // 同步加载并解析 thingpacks（对应 Forge 的 enqueueWork + NewRegistryEvent waitForLoading）。
        var loaderFuture = manager.beginLoading();
        manager.waitForLoading(loaderFuture);

        // 先在自定义/vanilla 注册表登记"类型级"条目（sound_type/item_tier/armor_material/blockset 等），
        // 再按依赖顺序把各 parser 构建的 Thing 注册进 vanilla 注册表。
        manager.finishLoading();

        soundEventParser.registerValues();
        fluidTypeParser.registerValues();
        enchantmentParser.registerValues();
        // 流体先注册（液体方块/桶在构造时按注册表解析流体），再注册方块，最后注册物品。
        fluidParser.registerValues();
        blockParser.registerValues();
        itemParser.registerValues();
        creativeModeTabParser.registerValues();

        registerBucketStorages();

        // 扩展点（解析后）：thing 已全部注册进游戏注册表，见 ThingPlugin#afterThingLoading。
        thingPlugins.forEach(loaded -> {
            try
            {
                loaded.plugin().afterThingLoading(manager);
            }
            catch (Throwable e)
            {
                LOGGER.error("Error in JsonThings plugin post-load from mod '{}'", loaded.modId(), e);
            }
        });
    }

    /**
     * 获取 {@code "jsonthings"} 入口点的扩展实现，并按序回调 {@link ThingPlugin#registerThingTypes}。
     * 单个插件的失败不应影响其它插件与随后的 thingpack 解析，故逐个捕获。
     */
    private static List<LoadedPlugin> loadThingPlugins(ThingResourceManager manager)
    {
        var loaded = new ArrayList<LoadedPlugin>();
        for (var container : FabricLoader.getInstance().getEntrypointContainers(ThingPlugin.ENTRYPOINT_KEY, ThingPlugin.class))
        {
            String modId = container.getProvider().getMetadata().getId();
            try
            {
                ThingPlugin plugin = container.getEntrypoint();
                plugin.registerThingTypes(manager);
                loaded.add(new LoadedPlugin(modId, plugin));
            }
            catch (Throwable e)
            {
                LOGGER.error("Error initializing JsonThings plugin from mod '{}'; its thing types and parsers will be unavailable.",
                        modId, e);
            }
        }
        return loaded;
    }

    /**
     * 把 thingpack 生成的流体桶暴露为 Fabric transfer 流体容器（{@code FluidStorage.ITEM}），
     * 这样 PL 的 fluid_container 桶模型能读到桶内流体以显示填充纹理，管道/泵也能抽取。
     */
    private static void registerBucketStorages()
    {
        if (fluidParser == null)
            return;
        fluidParser.getBuilders().forEach(fb -> {
            var bucketBuilder = fb.getBucketBuilder();
            if (bucketBuilder == null)
                return;
            Item bucket = bucketBuilder.get().self();
            FluidStorage.combinedItemApiProvider(bucket).register(context -> new FluidBucketWrapper(context));
        });
    }

    private static boolean rhinoPresent()
    {
        // 对应 Forge 的 ModList#isLoaded("rhino")：rhino 以独立 mod 安装（其初始化负责安装 ContextFactory/ClassShutter）。
        // Fabric 上优先看 loader（dev/生产 mods 目录均适用），退回类探测兼容仅放 classpath 的情形。
        if (FabricLoader.getInstance().isModLoaded("rhino"))
            return true;
        try
        {
            Class.forName("dev.latvian.mods.rhino.Rhino", false, JsonThings.class.getClassLoader());
            return true;
        }
        catch (ClassNotFoundException e)
        {
            return false;
        }
    }
}
