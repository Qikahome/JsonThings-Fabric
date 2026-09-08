package dev.gigaherz.jsonthings.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Fabric 版延迟注册表引用：替代 Forge RegistryObject。
 * 每次 {@link #get()} 都从目标注册表按 id 解析（注册全部完成后即稳定），
 * 因此适用于"解析阶段互相引用、注册阶段结束后才消费"的 thingpack 流程。
 * Forge 的 RegistryObject 在注册期由事件填充；Fabric 无对应注册事件，
 * 此处以"注册表直接查找"实现等价语义。
 */
public final class RegistryObject<T> implements Supplier<T>
{
    private final ResourceLocation id;
    private final Registry<T> registry;

    private RegistryObject(ResourceLocation id, Registry<T> registry)
    {
        this.id = Objects.requireNonNull(id);
        this.registry = Objects.requireNonNull(registry);
    }

    public static <T> RegistryObject<T> create(ResourceLocation id, Registry<T> registry)
    {
        return new RegistryObject<>(id, registry);
    }

    public ResourceLocation getId()
    {
        return id;
    }

    public boolean isPresent()
    {
        return registry.containsKey(id);
    }

    @Override
    public T get()
    {
        return registry.get(id);
    }

    public T orElse(T other)
    {
        return isPresent() ? get() : other;
    }

    public T orElseGet(Supplier<? extends T> other)
    {
        return isPresent() ? get() : other.get();
    }
}
