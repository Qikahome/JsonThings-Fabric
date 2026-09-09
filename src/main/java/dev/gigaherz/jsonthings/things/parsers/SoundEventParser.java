package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.things.builders.SoundEventBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class SoundEventParser extends ThingParser<SoundEventBuilder>
{
    public static final Logger LOGGER = LogManager.getLogger();

    public SoundEventParser()
    {
        super(GSON, "sound_event");
    }

    public void registerValues()
    {
        LOGGER.info("Started registering SoundEvent things, errors about unexpected registry domains are harmless...");
        processAndConsumeErrors(getThingType(), getBuilders(),
                thing -> Registry.register(BuiltInRegistries.SOUND_EVENT, thing.getRegistryName(), thing.get()),
                BaseBuilder::getRegistryName);
        LOGGER.info("Done processing thingpack SoundEvents.");
    }


    @Override
    public SoundEventBuilder processThing(ResourceLocation key, JsonObject data, Consumer<SoundEventBuilder> builderModification)
    {
        final SoundEventBuilder builder = SoundEventBuilder.begin(this, key);

        JParse.begin(data)
                .ifKey("range", val -> val.floatValue().handle(builder::setRange))
        ;

        builderModification.accept(builder);

        return builder;
    }
}
