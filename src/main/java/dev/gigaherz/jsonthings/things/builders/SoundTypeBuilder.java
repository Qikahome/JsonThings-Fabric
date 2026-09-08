package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;

public class SoundTypeBuilder extends BaseBuilder<SoundType, SoundTypeBuilder>
{
    public static SoundTypeBuilder begin(ThingParser<SoundTypeBuilder> ownerParser, ResourceLocation registryName)
    {
        return new SoundTypeBuilder(ownerParser, registryName);
    }

    private float volume = 1.0f;
    private float pitch = 1.0f;
    private ResourceLocation breakSound;
    private ResourceLocation stepSound;
    private ResourceLocation placeSound;
    private ResourceLocation hitSound;
    private ResourceLocation fallSound;

    private SoundTypeBuilder(ThingParser<SoundTypeBuilder> ownerParser, ResourceLocation registryName)
    {
        super(ownerParser, registryName);
    }

    public void setVolume(float volume)
    {
        this.volume = volume;
    }

    public void setPitch(float pitch)
    {
        this.pitch = pitch;
    }

    public void setBreakSound(ResourceLocation resourceLocation)
    {
        breakSound = resourceLocation;
    }

    public void setStepSound(ResourceLocation resourceLocation)
    {
        stepSound = resourceLocation;
    }

    public void setHitSound(ResourceLocation resourceLocation)
    {
        hitSound = resourceLocation;
    }

    public void setFallSound(ResourceLocation resourceLocation)
    {
        fallSound = resourceLocation;
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Sound Type";
    }

    @Override
    protected SoundType buildInternal()
    {
        // Forge 版用 ForgeSoundType(Supplier) 延迟解析；Fabric 的 vanilla SoundType 要求实体 SoundEvent，
        // 依赖注册顺序（SoundEvent 先于 SoundType 注册）直接解析。
        return new SoundType(volume, pitch,
                resolve(breakSound, SoundEvents.STONE_BREAK),
                resolve(stepSound, SoundEvents.STONE_STEP),
                resolve(placeSound != null ? placeSound : breakSound, SoundEvents.STONE_PLACE),
                resolve(hitSound, SoundEvents.STONE_HIT),
                resolve(fallSound, SoundEvents.STONE_FALL));
    }

    private SoundEvent resolve(ResourceLocation id, SoundEvent fallback)
    {
        SoundEvent event = BuiltInRegistries.SOUND_EVENT.get(id);
        return event != null ? event : fallback;
    }
}
