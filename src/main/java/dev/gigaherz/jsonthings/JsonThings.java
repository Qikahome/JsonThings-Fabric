package dev.gigaherz.jsonthings;

import com.mojang.logging.LogUtils;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.client.BlockColorHandler;
import dev.gigaherz.jsonthings.things.client.ItemColorHandler;
import dev.gigaherz.jsonthings.things.parsers.*;
import dev.gigaherz.jsonthings.things.scripting.ScriptParser;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.item.FluidBucketWrapper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;

import java.util.function.Function;

/**
 * JsonThings Fabric 入口（main + client 双入口）。
 *
 * <p>对应上游 Forge 版 {@code JsonThings} 类。Fabric 无 mod 事件总线，各阶段的映射：
 * <ul>
 *   <li>构造器 + FMLConstructModEvent → {@link #onInitialize()}：创建 parser、注册 things pack 源、同步加载 thingpacks；</li>
 *   <li>NewRegistryEvent(finishLoading) → onInitialize 内按序调用 parser 的 finishLoading/registerValues；</li>
 *   <li>AddPackFindersEvent(SERVER_DATA) → Fabric 无 world datapack 注入通道（数据包内容暂不注入世界）；</li>
 *   <li>ClientHandlers(FMLClientSetupEvent/颜色/渲染层) → {@link #onInitializeClient()} + CLIENT_STARTED。</li>
 * </ul>
 */
public class JsonThings implements ModInitializer, ClientModInitializer
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

    @Override
    public void onInitializeClient()
    {
        // 流体渲染 handler 由 FabricatedForgeFluid 的 FluidRenderHandlerRegistrar 自动注册
        // （遍历流体注册表，对所有 FabricatedFluidType 调用 initializeClient）。

        BlockColorHandler.init();
        ItemColorHandler.init();

        ClientLifecycleEvents.CLIENT_STARTED.register(JsonThings::afterClientStart);
    }

    private static void afterClientStart(Minecraft client)
    {
        try
        {
            registerBlockRenderLayers();
            registerBlockColors();
            registerItemColors(client.getBlockColors());
        }
        catch (Throwable e)
        {
            LOGGER.error("Error during JsonThings client setup", e);
        }
    }

    private static void registerBlockRenderLayers()
    {
        final ResourceLocation solid = new ResourceLocation("solid");
        blockParser.getBuilders().forEach(thing -> {
            if (thing.isInErrorState()) return;
            ResourceLocation layer = thing.getDefaultRenderLayer();
            if (!layer.equals(solid))
            {
                BlockRenderLayerMap.INSTANCE.putBlock(thing.get().self(), renderTypeByLayer(layer));
            }
        });

        // 流体（含全部 sibling 变体）渲染层：对应上游 Forge JsonThings clientSetup 的 fluidParser 段。
        // 渲染层按 Fluid 逐个注册，同一 thing 生成的静止/流动等所有条目都需覆盖。
        fluidParser.getBuilders().forEach(thing -> {
            if (thing.isInErrorState()) return;
            ResourceLocation layer = thing.getDefaultRenderLayer();
            if (!layer.equals(solid))
            {
                for (var fluid : thing.getAllSiblings())
                {
                    BlockRenderLayerMap.INSTANCE.putFluid(fluid, renderTypeByLayer(layer));
                }
            }
        });
    }

    private static void registerBlockColors()
    {
        blockParser.getBuilders().forEach(thing -> {
            String handlerName = thing.getColorHandler();
            if (handlerName != null)
            {
                var bc = BlockColorHandler.get(handlerName);
                ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> bc.getColor(state, world, pos, tintIndex), thing.get().self());
            }
        });
    }

    private static void registerItemColors(BlockColors blockColors)
    {
        itemParser.getBuilders().forEach(thing -> {
            if (thing.isInErrorState()) return;
            String handlerName = thing.getColorHandler();
            if (handlerName != null)
            {
                Function<BlockColors, ItemColor> handler = ItemColorHandler.get(handlerName);
                ItemColor ic = handler.apply(blockColors);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> ic.getColor(stack, tintIndex), thing.get().self());
            }
        });
    }

    private static RenderType renderTypeByLayer(ResourceLocation layer)
    {
        return switch (layer.getPath())
        {
            case "cutout_mipped" -> RenderType.cutoutMipped();
            case "cutout" -> RenderType.cutout();
            case "translucent" -> RenderType.translucent();
            case "tripwire" -> RenderType.tripwire();
            default -> RenderType.solid();
        };
    }

    private static boolean rhinoPresent()
    {
        // 对应 Forge 的 ModList#isLoaded("rhino")：rhino 以独立 mod 安装（其初始化负责安装 ContextFactory/ClassShutter）。
        // Fabric 上优先看 loader（dev/生产 mods 目录均适用），退回类探测兼容仅放 classpath 的情形。
        if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("rhino"))
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
