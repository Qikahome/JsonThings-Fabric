package dev.gigaherz.jsonthings.mixin;

import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Fabric resource-loader 假定 PackType 只有 SERVER_DATA / CLIENT_RESOURCES 两种：
 * {@link ResourceManagerHelperImpl#getWrapperLookup(List)} 对 CLIENT_RESOURCES 直接返回 null，
 * 对其余任何 PackType 都强制要求 listeners 中存在 RecipeManager，否则抛
 * {@code IllegalStateException("No RecipeManager found in listeners!")}。
 *
 * <p>JsonThings 使用自建的第三个 PackType（{@code CustomPackType.THINGS}，枚举注入）
 * 持有独立的私有 {@code ReloadableResourceManager}，其 listeners 只有 thingpack parser、
 * 不含 RecipeManager → 触发上述异常。
 * 本 mixin 对"非 vanilla 两值"的 PackType 在方法头部提前返回 null（与 CLIENT_RESOURCES
 * 分支语义一致）；该 helper 实例的 listenerFactories 为空，wrapperLookup 不会被消费，
 * 私有 thingpack reload 得以按 vanilla 原样运行。
 */
@Mixin(ResourceManagerHelperImpl.class)
public class ResourceManagerHelperImplMixin
{
    @Shadow
    @Final
    private PackType type;

    @Inject(method = "getWrapperLookup", at = @At("HEAD"), cancellable = true)
    private void jsonthings$allowCustomPackTypes(List<PreparableReloadListener> listeners, CallbackInfoReturnable<HolderLookup.Provider> cir)
    {
        if (type == dev.gigaherz.jsonthings.util.CustomPackType.THINGS)
        {
            cir.setReturnValue(null);
        }
    }
}
