package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.JsonThings;
import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import dev.gigaherz.jsonthings.util.Utils;
import qikahome.fabricatedforgefluid.client.FabricatedFluidRenderHandler;
import qikahome.fabricatedforgefluid.fluids.FabricatedFluidType;
import io.github.fabricators_of_create.porting_lib.fluids.FluidType;
import io.github.fabricators_of_create.porting_lib.fluids.sound.SoundActions;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

public class FluidTypeBuilder extends BaseBuilder<FluidType, FluidTypeBuilder> {
    public static FluidTypeBuilder begin(ThingParser<FluidTypeBuilder> ownerParser, ResourceLocation registryName) {
        return new FluidTypeBuilder(ownerParser, registryName);
    }

    private ResourceLocation stillTexture;
    private ResourceLocation flowingTexture;
    private ResourceLocation sideTexture;
    private Rarity rarity = Rarity.COMMON;
    private Integer color;
    private Integer density;
    private Integer lightLevel;
    private Integer temperature;
    private Integer viscosity;
    private String translationKey;
    private Boolean isGaseous;
    private ResourceLocation fillSound;
    private ResourceLocation emptySound;
    private ResourceLocation vaporizeSound;
    private Double motionScale;
    private Float fallDistanceModifier;
    private Boolean canPushEntity;
    private Boolean canSwim;
    private Boolean canDrown;
    private Boolean canExtinguish;
    private Boolean canHydrate;
    private Boolean canConvertToSource;
    private Boolean supportsBoating;
    private ResourceLocation tag;

    private FluidTypeBuilder(ThingParser<FluidTypeBuilder> ownerParser, ResourceLocation registryName) {
        super(ownerParser, registryName);
    }

    @Override
    protected String getThingTypeDisplayName() {
        return "Fluid";
    }

    public void setStillTexture(ResourceLocation stillTexture) {
        this.stillTexture = stillTexture;
    }

    @Nullable
    private ResourceLocation getStillTexture() {
        return getValue(stillTexture, FluidTypeBuilder::getStillTexture);
    }

    public void setFlowingTexture(ResourceLocation flowingTexture) {
        this.flowingTexture = flowingTexture;
    }

    @Nullable
    private ResourceLocation getFlowingTexture() {
        return getValue(flowingTexture, FluidTypeBuilder::getFlowingTexture);
    }

    public void setSideTexture(ResourceLocation overlay) {
        this.sideTexture = overlay;
    }

    @Nullable
    private ResourceLocation getSideTexture() {
        return getValue(sideTexture, FluidTypeBuilder::getSideTexture);
    }

    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }

    @Nullable
    private Rarity getRarity() {
        return getValue(rarity, FluidTypeBuilder::getRarity);
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    @Nullable
    private Integer getColor() {
        return getValue(color, FluidTypeBuilder::getColor);
    }

    public void setDensity(Integer density) {
        this.density = density;
    }

    @Nullable
    private Integer getDensity() {
        return getValue(density, FluidTypeBuilder::getDensity);
    }

    public void setLightLevel(Integer lightLevel) {
        this.lightLevel = lightLevel;
    }

    @Nullable
    private Integer getLightLevel() {
        return getValue(lightLevel, FluidTypeBuilder::getLightLevel);
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    @Nullable
    private Integer getTemperature() {
        return getValue(temperature, FluidTypeBuilder::getTemperature);
    }

    public void setViscosity(Integer viscosity) {
        this.viscosity = viscosity;
    }

    @Nullable
    private Integer getViscosity() {
        return getValue(viscosity, FluidTypeBuilder::getViscosity);
    }

    public void setTranslationKey(String translationKey) {
        this.translationKey = translationKey;
    }

    @Nullable
    private String getTranslationKey() {
        return getValue(translationKey, FluidTypeBuilder::getTranslationKey);
    }

    public void setGaseous(Boolean gaseous) {
        isGaseous = gaseous;
    }

    @Nullable
    private Boolean getIsGaseous() {
        return getValue(isGaseous, FluidTypeBuilder::getIsGaseous);
    }

    public void setFillSound(ResourceLocation fillSound) {
        this.fillSound = fillSound;
    }

    @Nullable
    private ResourceLocation getFillSound() {
        return getValue(fillSound, FluidTypeBuilder::getFillSound);
    }

    public void setEmptySound(ResourceLocation emptySound) {
        this.emptySound = emptySound;
    }

    @Nullable
    private ResourceLocation getEmptySound() {
        return getValue(emptySound, FluidTypeBuilder::getEmptySound);
    }

    public void setVaporizeSound(ResourceLocation vaporizeSound) {
        this.vaporizeSound = vaporizeSound;
    }

    @Nullable
    private ResourceLocation getVaporizeSound() {
        return getValue(vaporizeSound, FluidTypeBuilder::getVaporizeSound);
    }

    public void setMotionScale(double motionScale) {
        this.motionScale = motionScale;
    }

    @Nullable
    public Double getMotionScale() {
        return getValue(motionScale, FluidTypeBuilder::getMotionScale);
    }

    public void setFallDistanceModifier(float fallDistanceModifier) {
        this.fallDistanceModifier = fallDistanceModifier;
    }

    @Nullable
    public Float getFallDistanceModifier() {
        return getValue(fallDistanceModifier, FluidTypeBuilder::getFallDistanceModifier);
    }

    public void setCanPushEntity(boolean canPushEntity) {
        this.canPushEntity = canPushEntity;
    }

    @Nullable
    public Boolean getCanPushEntity() {
        return getValue(canPushEntity, FluidTypeBuilder::getCanPushEntity);
    }

    public void setCanSwim(boolean canSwim) {
        this.canSwim = canSwim;
    }

    @Nullable
    public Boolean getCanSwim() {
        return getValue(canSwim, FluidTypeBuilder::getCanSwim);
    }

    public void setCanDrown(boolean canDrown) {
        this.canDrown = canDrown;
    }

    @Nullable
    public Boolean getCanDrown() {
        return getValue(canDrown, FluidTypeBuilder::getCanDrown);
    }

    public void setCanExtinguish(boolean canExtinguish) {
        this.canExtinguish = canExtinguish;
    }

    @Nullable
    public Boolean getCanExtinguish() {
        return getValue(canExtinguish, FluidTypeBuilder::getCanExtinguish);
    }

    public void setCanHydrate(boolean canHydrate) {
        this.canHydrate = canHydrate;
    }

    @Nullable
    public Boolean getCanHydrate() {
        return getValue(canHydrate, FluidTypeBuilder::getCanHydrate);
    }

    public void setCanConvertToSource(boolean canConvertToSource) {
        this.canConvertToSource = canConvertToSource;
    }

    @Nullable
    public Boolean getCanConvertToSource() {
        return getValue(canConvertToSource, FluidTypeBuilder::getCanConvertToSource);
    }

    public void setSupportsBoating(boolean supportsBoating) {
        this.supportsBoating = supportsBoating;
    }

    @Nullable
    public Boolean getSupportsBoating() {
        return getValue(supportsBoating, FluidTypeBuilder::getSupportsBoating);
    }

    public void setTag(ResourceLocation tag) {
        this.tag = tag;
    }

    /**
     * 实体交互桥接用的 fluid tag。默认取本 fluid_type 的注册名（{@code <ns>:<path>}），
     * 数据包需让对应流体实际挂上该 tag（{@code data/<ns>/tags/fluids/<path>.json}），
     * 否则实体交互（推动/游泳/溺水等）不会命中。
     */
    public ResourceLocation getTag() {
        return getValue(tag, FluidTypeBuilder::getTag);
    }

    /**
     * 解析期收集的客户端视觉数据：Fabric 的 FluidRenderHandler 需要 still/flowing 贴图、overlay 与染色（PL
     * FluidType 自身不带这些）。
     */
    public FluidVisualData getVisualData() {
        return new FluidVisualData(getStillTexture(), getFlowingTexture(), getSideTexture(), getColor());
    }

    public record FluidVisualData(@Nullable ResourceLocation still, @Nullable ResourceLocation flowing,
            @Nullable ResourceLocation overlay, @Nullable Integer color) {
    }

    @Override
    protected FluidType buildInternal() {
        FluidType.Properties props = FluidType.Properties.create();

        if (getLightLevel() != null)
            props.lightLevel(getLightLevel());
        if (getDensity() != null)
            props.density(getDensity());
        if (getTemperature() != null)
            props.temperature(getTemperature());
        if (getViscosity() != null)
            props.viscosity(getViscosity());
        if (getRarity() != null)
            props.rarity(getRarity());
        var fillSound = getFillSound() != null ? Utils.getOrCrash(BuiltInRegistries.SOUND_EVENT, getFillSound()) : null;
        if (fillSound != null)
            props.sound(SoundActions.BUCKET_FILL, fillSound);
        var emptySound = getEmptySound() != null ? Utils.getOrCrash(BuiltInRegistries.SOUND_EVENT, getEmptySound())
                : null;
        if (emptySound != null)
            props.sound(SoundActions.BUCKET_EMPTY, emptySound);
        var vaporizeSound = getVaporizeSound() != null
                ? Utils.getOrCrash(BuiltInRegistries.SOUND_EVENT, getVaporizeSound())
                : null;
        if (vaporizeSound != null)
            props.sound(SoundActions.FLUID_VAPORIZE, emptySound);
        if (getTranslationKey() != null)
            props.descriptionId(getTranslationKey());
        // if (getIsGaseous() != null && getIsGaseous()) props.gaseous();
        if (getMotionScale() != null)
            props.motionScale(getMotionScale());
        if (getFallDistanceModifier() != null)
            props.fallDistanceModifier(getFallDistanceModifier());
        if (getCanPushEntity() != null)
            props.canPushEntity(getCanPushEntity());
        if (getCanSwim() != null)
            props.canSwim(getCanSwim());
        if (getCanDrown() != null)
            props.canDrown(getCanDrown());
        if (getCanExtinguish() != null)
            props.canExtinguish(getCanExtinguish());
        if (getCanConvertToSource() != null)
            props.canConvertToSource(getCanConvertToSource());
        if (getCanHydrate() != null)
            props.canHydrate(getCanHydrate());
        if (getSupportsBoating() != null)
            props.supportsBoating(getSupportsBoating());
        // if (getPathType() != null) props.pathType(@org.jetbrains.annotations.Nullable
        // BlockPathTypes pathType)
        // if (getAdjacentPathType() != null)
        // props.adjacentPathType(@org.jetbrains.annotations.Nullable BlockPathTypes
        // adjacentPathType)

        final int color = getColor() != null ? getColor() : 0xFFFFFFFF;
        final ResourceLocation stillTexture = getStillTexture();
        final ResourceLocation flowingTexture = getFlowingTexture();
        final ResourceLocation sideTexture = getSideTexture();

        if (stillTexture == null)
            throw new IllegalStateException("FluidType requires a still_texture value");
        if (flowingTexture == null)
            throw new IllegalStateException("FluidType requires a flowing_texture value");

        // 实体交互桥接 tag：默认 fluid_type 注册名，可被数据包 "tag" 字段覆盖。
        ResourceLocation tagId = getTag();
        if (tagId == null)
            tagId = getRegistryName();
        TagKey<Fluid> fluidTag = TagKey.create(Registries.FLUID, tagId);
        return new ExtendedFluidType(props, fluidTag) {
            @Override
            public void initializeClient(Consumer<FluidRenderHandler> consumer) {
                consumer.accept(new FabricatedFluidRenderHandler(stillTexture, flowingTexture, sideTexture, color));
            }
        };
    }

    public static class ExtendedFluidType extends FabricatedFluidType {

        public ExtendedFluidType(Properties properties, TagKey<Fluid> tag) {
            super(properties, tag);
        }

        public void initializeClient(Consumer<FluidRenderHandler> consumer) {
        }
    }
}
