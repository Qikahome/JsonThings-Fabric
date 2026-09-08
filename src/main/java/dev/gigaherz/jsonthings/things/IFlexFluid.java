package dev.gigaherz.jsonthings.things;

import dev.gigaherz.jsonthings.things.events.IEventRunner;
import io.github.fabricators_of_create.porting_lib.fluids.extensions.FluidExtension;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Supplier;

public interface IFlexFluid extends IEventRunner, FluidExtension
{
    default Fluid self()
    {
        return (Fluid) this;
    }

    default boolean registerTwin()
    {
        return true;
    }

    void setBucketItem(Supplier<Item> bucketItem);
}
