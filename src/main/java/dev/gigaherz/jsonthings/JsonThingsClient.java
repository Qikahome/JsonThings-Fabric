package dev.gigaherz.jsonthings;

import dev.gigaherz.jsonthings.client.LoadingIssuesScreen;
import dev.gigaherz.jsonthings.things.client.BlockColorHandler;
import dev.gigaherz.jsonthings.things.client.ItemColorHandler;
import dev.gigaherz.jsonthings.util.LoadingIssues;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

/**
 * JsonThings Fabric 客户端入口（对应 Forge 版的 ClientHandlers/FMLClientSetupEvent）。
 *
 * <p>必须与 {@link JsonThings} 拆开：{@code main} 入口点在专用服务端也要加载，而本类引用了
 * {@code net.minecraft.client.*}，混在同一个类里会导致服务端启动时报
 * {@code Cannot load class net.minecraft.client.gui.screens.Screen in environment type SERVER}。
 */
public class JsonThingsClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        // 流体渲染 handler 由 FabricatedForgeFluid 的 FluidRenderHandlerRegistrar 自动注册
        // （遍历流体注册表，对所有 FabricatedFluidType 调用 initializeClient）。

        BlockColorHandler.init();
        ItemColorHandler.init();

        ClientLifecycleEvents.CLIENT_STARTED.register(JsonThingsClient::afterClientStart);
        // 提示屏必须在标题屏就绪之后再设置（CLIENT_STARTED 与首个 tick 都早于初始屏生效，会被覆盖），
        // 故在标题屏初始化完成后替换之；玩家确认（关闭提示屏）后不再替换。
        ScreenEvents.AFTER_INIT.register(JsonThingsClient::replaceTitleScreenWithLoadingIssues);
    }

    /**
     * 对应 Forge 侧 {@code ModLoadingWarning} 汇总出的加载错误屏：thingpack 解析期间有问题时，
     * 用自建提示屏替换标题屏（Fabric Loader 无等价 API，见 {@link LoadingIssues}）。
     */
    private static void replaceTitleScreenWithLoadingIssues(Minecraft client, Screen screen, int width, int height)
    {
        if (!(screen instanceof TitleScreen) || LoadingIssues.isEmpty() || LoadingIssues.isAcknowledged())
            return;

        JsonThings.LOGGER.warn("[Json Things] {} thingpack loading issue(s) found; showing the loading issues screen.",
                LoadingIssues.getIssues().size());
        client.setScreen(new LoadingIssuesScreen());
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
            JsonThings.LOGGER.error("Error during JsonThings client setup", e);
        }
    }

    private static void registerBlockRenderLayers()
    {
        final ResourceLocation solid = new ResourceLocation("solid");
        JsonThings.blockParser.getBuilders().forEach(thing -> {
            if (thing.isInErrorState()) return;
            ResourceLocation layer = thing.getDefaultRenderLayer();
            if (!layer.equals(solid))
            {
                BlockRenderLayerMap.INSTANCE.putBlock(thing.get().self(), renderTypeByLayer(layer));
            }
        });

        // 流体（含全部 sibling 变体）渲染层：对应上游 Forge JsonThings clientSetup 的 fluidParser 段。
        // 渲染层按 Fluid 逐个注册，同一 thing 生成的静止/流动等所有条目都需覆盖。
        JsonThings.fluidParser.getBuilders().forEach(thing -> {
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
        JsonThings.blockParser.getBuilders().forEach(thing -> {
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
        JsonThings.itemParser.getBuilders().forEach(thing -> {
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
}
