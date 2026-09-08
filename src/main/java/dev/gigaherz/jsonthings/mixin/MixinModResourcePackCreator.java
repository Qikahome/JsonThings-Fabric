package dev.gigaherz.jsonthings.mixin;

import dev.gigaherz.jsonthings.packs.ThingpackRepositorySource;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

/**
 * 参考 Open-Loader（Darkhax-Minecraft/Open-Loader，1.20.1 分支）：
 * Fabric 的 {@link ModResourcePackCreator} 同时是客户端资源包与服务端数据包仓库的注入点，
 * 在它构造时按 PackType 准备一个 {@link ThingpackRepositorySource}，
 * 在 loadPacks 之后把 thingpacks 目录下的包追加进去（required，自动生效）。
 */
@Mixin(ModResourcePackCreator.class)
public class MixinModResourcePackCreator
{
    @Shadow
    @Final
    private PackType type;

    @Unique
    private RepositorySource jsonthings$thingpackSource;

    @Inject(method = "<init>(Lnet/minecraft/server/packs/PackType;)V", at = @At("RETURN"))
    private void jsonthings$onInit(PackType type, CallbackInfo ci)
    {
        this.jsonthings$thingpackSource = new ThingpackRepositorySource(this.type);
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
