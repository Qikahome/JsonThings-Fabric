package dev.gigaherz.jsonthings;

import com.mojang.logging.LogUtils;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.client.BlockColorHandler;
import dev.gigaherz.jsonthings.things.client.ItemColorHandler;
import dev.gigaherz.jsonthings.things.parsers.*;
import dev.gigaherz.jsonthings.things.scripting.ScriptParser;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.util.function.Function;

/**
 * JsonThings Fabric 入口（main + client 双入口），对应上游官方 gigaherz JsonThings 1.21.1 {@code JsonThings}。
 *
 * <p>NeoForge 事件 → Fabric 入口方法的映射：
 * <ul>
 *   <li>构造器（parser 注册 + {@code ScriptParser.enable}）→ {@link #onInitialize()}；</li>
 *   <li>{@code FMLConstructModEvent}（{@code ThingRegistries.initRegistries} + 加载 thingpacks）+
 *       {@code NewRegistryEvent}（{@code waitForLoading}）→ onInitialize 内同步完成；</li>
 *   <li>parser 的 {@code RegisterEvent} 批量注册 → thingpack 解析完成后由 {@link #registerThingValues()}
 *       按依赖顺序显式注册（见各 parser 的 registerValues()）；</li>
 *   <li>{@code AddPackFindersEvent}：SERVER_DATA / CLIENT_RESOURCES（thingpack 作为世界数据包 / 客户端资源包）
 *       → Fabric 无对应的 vanilla 包库注入通道（{@code PackRepository} 无 addPackFinder，1.20.1 成品同样
 *       未做此通道），省略 —— thingpack 的 data/assets 目录暂不进入 vanilla 管线；</li>
 *   <li>{@code ClientHandlers}（颜色 init / 渲染层 / 颜色注册）→ {@link #onInitializeClient()} +
 *       {@code CLIENT_STARTED}（等价 enqueueWork 到主线程）。</li>
 * </ul>
 *
 * <p>与 NeoForge 版差异说明：
 * <ul>
 *   <li>Neo {@code NamedRenderTypeManager.get(layer).block()}（数据包自定义命名渲染层）→ 仅支持 vanilla 渲染层名
 *       （solid/cutout_mipped/cutout/translucent/tripwire），见 {@link #renderTypeByLayer(ResourceLocation)}；</li>
 *   <li>Neo {@code IConfigScreenFactory}（thingpack 配置屏挂 mod 设置）→ Fabric 无对应扩展点，暂缺（可后续接 ModMenu）；</li>
 *   <li>Neo {@code ResourcePackLoader.populatePackRepository} → 由 ThingResourceManager 构造期持有 thingpacks
 *       目录源（FolderRepositorySource）替代。</li>
 * </ul>
 */
public class JsonThings implements ModInitializer, ClientModInitializer
{
    public static final String MODID = "jsonthings";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static BlockParser blockParser;
    public static ItemParser itemParser;
    public static FluidParser fluidParser;
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
        // Neo：construct 事件里先初始化 jsonthings 自建注册表（vanilla MappedRegistry，无平台依赖）。
        ThingRegistries.initRegistries();

        var manager = ThingResourceManager.instance();

        // rhino-lib 随模组内嵌，无条件启用脚本解析（上游已去掉 rhino mod 检测）。
        ScriptParser.enable(manager);

        // parser 注册顺序与上游构造器一致（决定 thing 目录的解析顺序）。
        blockParser = manager.registerParser(new BlockParser());
        itemParser = manager.registerParser(new ItemParser());
        fluidParser = manager.registerParser(new FluidParser());
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

        // mod jar 内嵌 things/（自带 pack.mcmeta 的 mod）注册为 thingpack 源。
        manager.addPackFinder(ModResourcesFinder.buildPackFinder());

        // 同步加载并解析 thingpacks（beginLoading → waitForLoading）。
        var loaderFuture = manager.beginLoading();
        manager.waitForLoading(loaderFuture);

        // 各 parser 的 finishLoadingInternal 注册自建注册表条目（如 TierParser → ThingRegistries.TIERS），
        // 必须在 registerValues（builder.get() 解析对这些条目的引用）之前完成。
        manager.finishLoading();

        // Neo 各 parser 以 RegisterEvent 处理器注册 builders；Fabric 无注册事件，
        // 按依赖顺序显式注册（sound → fluid_type → fluid → block → armor → item → creative_tab）。
        registerThingValues();
    }

    private static void registerThingValues()
    {
        // SoundType 构建时同步查 BuiltInRegistries.SOUND_EVENT（SoundTypeBuilder.resolve），故 SoundEvent 最先。
        soundEventParser.registerValues();
        // fluid_type 引用（FluidBuilder string 分支）按 id 延迟查 PortingLibFluids.FLUID_TYPES，fluid 需在其后。
        fluidTypeParser.registerValues();
        fluidParser.registerValues();
        // block 内嵌 block-item 也进 itemParser，block 先注册。
        blockParser.registerValues();
        // armor 类 item 构建时即时查 ARMOR_MATERIAL（FlexItemType getHolderOrCrash），armor material 先于 item。
        armorMaterialParser.registerValues();
        // item（含 bucket / block-item）随后；tier/food 等引用为延迟 Supplier 不受顺序影响。
        itemParser.registerValues();
        // 组匹配依赖 item/block 全部注册完成（explicit=false 遍历 itemParser）。
        creativeModeTabParser.registerValues();
    }

    @Override
    public void onInitializeClient()
    {
        // Neo ClientHandlers.constructMod 的 enqueueWork 会把客户端注册推迟到主线程、Minecraft 就绪后执行，
        // Fabric 对应 CLIENT_STARTED。
        ClientLifecycleEvents.CLIENT_STARTED.register(JsonThings::afterClientStart);
    }

    private static void afterClientStart(Minecraft client)
    {
        try
        {
            // Neo ClientHandlers.constructMod：初始化颜色 handler 注册表。
            BlockColorHandler.init();
            ItemColorHandler.init();

            // Neo clientSetup：方块 + 流体（含全部 sibling 状态）渲染层。
            registerRenderLayers();

            // Neo RegisterColorHandlersEvent.Block / .Item → ColorProviderRegistry。
            registerBlockColors();
            registerItemColors(client.getBlockColors());
        }
        catch (Throwable e)
        {
            LOGGER.error("Error during JsonThings client setup", e);
        }
    }

    private static void registerRenderLayers()
    {
        final ResourceLocation solid = ResourceLocation.withDefaultNamespace("solid");
        blockParser.getBuilders().forEach(thing -> {
            if (thing.isInErrorState()) return;
            ResourceLocation layer = thing.getDefaultRenderLayer();
            if (!layer.equals(solid))
            {
                // BlockBuilder 泛型 IFlexBlock（含 self() 返回 Block）。
                BlockRenderLayerMap.INSTANCE.putBlock(thing.get().self(), renderTypeByLayer(layer));
            }
        });
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
                BlockColor bc = BlockColorHandler.get(handlerName);
                ColorProviderRegistry.BLOCK.register(
                        (state, world, pos, tintIndex) -> bc.getColor(state, world, pos, tintIndex),
                        thing.get().self());
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
                // ItemBuilder 泛型为 Item（无 self()），直接 get()。
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> ic.getColor(stack, tintIndex), thing.get());
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
}
