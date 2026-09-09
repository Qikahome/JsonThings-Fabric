package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.JsonThings;
import dev.gigaherz.jsonthings.things.StackContext;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.things.builders.CreativeModeTabBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import dev.gigaherz.jsonthings.util.parse.value.Any;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class CreativeModeTabParser extends ThingParser<CreativeModeTabBuilder>
{
    public static final Logger LOGGER = LogManager.getLogger();

    public CreativeModeTabParser()
    {
        super(GSON, "creative_mode_tab");
    }

    public void registerValues()
    {
        LOGGER.info("Started registering Item things, errors about unexpected registry domains are harmless...");
        processAndConsumeErrors(getThingType(), getBuilders(), thing -> {
            var tab = thing.get();
            var icon = tab.icon();
            var name = tab.name();
            var tabKey = ResourceKey.create(Registries.CREATIVE_MODE_TAB, thing.getRegistryName());
            var builder = new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 0).icon(() -> icon.toStack(null))
                    .title(Component.translatable(name))
                    .displayItems((parameters, output) -> {
                        for (var variantProvider : thing.getVariantProviders())
                        {
                            variantProvider.provideVariants(tabKey, output, parameters, null, true);
                        }
                        // 组匹配：声明 group == 本 tab 的 item builder（含方块派生的 block item）补进本 tab，
                        // 等价上游 Neo 的 addToTabs(BuildCreativeModeTabContentsEvent)。
                        for (var itemBuilder : JsonThings.itemParser.getBuilders())
                        {
                            itemBuilder.provideVariants(tabKey, output, parameters, null, false);
                        }
                    });
            // Neo 的 Builder.withTabsBefore/After 是平台注入方法；vanilla/Fabric 的 CreativeModeTab.Builder
            // 无相对排序 API（1.20.1 成品同），thingpack 自定义 tab 追加到列表末尾，before/after 忽略。
            if (thing.getRightSide())
                builder = builder.alignedRight();
            Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, thing.getRegistryName(), builder.build());
        }, BaseBuilder::getRegistryName);

        registerItemsToExternalTabs();

        LOGGER.info("Done processing thingpack CreativeModeTabs.");
    }

    private static ResourceKey<CreativeModeTab>[] toTabKeys(ResourceLocation[] ids)
    {
        ResourceKey<CreativeModeTab>[] keys = new ResourceKey[ids.length];
        for (int i = 0; i < ids.length; i++)
        {
            keys[i] = ResourceKey.create(Registries.CREATIVE_MODE_TAB, ids[i]);
        }
        return keys;
    }

    /**
     * item 声明的 group / creative_menu_stacks 若指向 vanilla 或其它 mod 的 tab（非本 mod 自定义 tab），
     * 用 Fabric 的 {@link ItemGroupEvents} 挂载到对应 tab 的内容构建流程。
     */
    private void registerItemsToExternalTabs()
    {
        Set<ResourceKey<CreativeModeTab>> customTabs = new HashSet<>();
        getBuilders().forEach(b -> customTabs.add(ResourceKey.create(Registries.CREATIVE_MODE_TAB, b.getRegistryName())));

        Set<ResourceKey<CreativeModeTab>> referencedTabs = new HashSet<>();
        JsonThings.itemParser.getBuilders().forEach(itemBuilder -> {
            var group = itemBuilder.getGroup();
            if (group != null)
                referencedTabs.add(group);
            referencedTabs.addAll(itemBuilder.getCreativeMenuTabs());
        });

        for (ResourceKey<CreativeModeTab> tabKey : referencedTabs)
        {
            if (customTabs.contains(tabKey))
                continue; // 本 mod 的 tab 已在 displayItems 里做组匹配，走 Fabric 事件会重复
            ItemGroupEvents.modifyEntriesEvent(tabKey).register(entries -> {
                var parameters = entries.getContext();
                JsonThings.itemParser.getBuilders().forEach(itemBuilder ->
                        itemBuilder.provideVariants(tabKey, entries, parameters, null, false));
            });
        }
    }

    @Override
    protected void finishLoadingInternal()
    {
        getBuilders().forEach(CreativeModeTabBuilder::get);
    }

    @Override
    protected CreativeModeTabBuilder processThing(ResourceLocation key, JsonObject data, Consumer<CreativeModeTabBuilder> builderModification)
    {
        final CreativeModeTabBuilder builder = CreativeModeTabBuilder.begin(this, key);

        JParse.begin(data)
                .key("icon", val -> val
                        .ifString(str -> str.map(ResourceLocation::parse).map(StackContext::new).handle(builder::setIcon))
                        .ifObj(str -> str.map((JsonObject name) -> parseStackContext(name, true, true)).handle(builder::setIcon))
                        .typeError()
                )
                .ifKey("translation_key", val -> val.string().handle(builder::setTranslationKey))
                .ifKey("right_side", val -> val.bool().handle(builder::setRightSide))
                .ifKey("items", val -> val.array().forEach((index, entry) -> entry
                        .ifString(str -> str.map(ResourceLocation::parse).handle(builder::addItem))
                        .ifObj(obj -> obj.map((JsonObject name) -> parseStackContext(name, true, true)).handle(builder::addItem))
                        .typeError()
                ))
                .ifKey("before", val -> val.array().flatten(e -> e.string().map(ResourceLocation::parse).value(), ResourceLocation[]::new).handle(builder::setBefore))
                .ifKey("after", val -> val.array().flatten(e -> e.string().map(ResourceLocation::parse).value(), ResourceLocation[]::new).handle(builder::setAfter));

        builderModification.accept(builder);

        return builder;
    }
}
