package dev.gigaherz.jsonthings.mixin;

import dev.gigaherz.jsonthings.packs.ThingpackRepositorySource;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

/**
 * 参考 Open-Loader（Darkhax-Minecraft/Open-Loader，1.20.1 分支）：
 * Fabric 的 {@link ModResourcePackCreator} 同时是客户端资源包与服务端数据包仓库的注入点。
 *
 * <p>1.20.1 版该类按 PackType 持有 type 字段；1.21.1 重构为客户端静态单例
 * {@code CLIENT_RESOURCE_PACK_PROVIDER} + 按需构造的数据包实例，不再有 type 字段，
 * 故用两个构造器（{@code (PackType)} / {@code (PackType, boolean)}）的入参直接构造
 * {@link ThingpackRepositorySource}，并在 loadPacks 之后追加（required，自动生效）。
 */
@Mixin(ModResourcePackCreator.class)
public class MixinModResourcePackCreator
{
    @Unique
    private RepositorySource jsonthings$thingpackSource;

    @Inject(method = "<init>(Lnet/minecraft/server/packs/PackType;)V", at = @At("RETURN"))
    private void jsonthings$onInit(PackType type, CallbackInfo ci)
    {
        this.jsonthings$thingpackSource = new ThingpackRepositorySource(type);
    }

    @Inject(method = "<init>(Lnet/minecraft/server/packs/PackType;Z)V", at = @At("RETURN"))
    private void jsonthings$onInit2(PackType type, boolean unused, CallbackInfo ci)
    {
        this.jsonthings$thingpackSource = new ThingpackRepositorySource(type);
    }

    @Inject(method = "loadPacks", at = @At("RETURN"))
    private void jsonthings$appendThingpacks(Consumer<Pack> consumer, CallbackInfo ci)
    {
        if (this.jsonthings$thingpackSource != null)
        {
            this.jsonthings$thingpackSource.loadPacks(consumer);
        }
    }
}
