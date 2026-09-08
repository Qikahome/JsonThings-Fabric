package dev.gigaherz.jsonthings.util;

import net.minecraft.server.packs.PackType;

/**
 * 持有注入的 THINGS pack 类型（由 mixin ResourcePackTypeExtender 在 PackType 类初始化时填充）。
 * <p>
 * TODO(设计债务)：通过扩展原版枚举 PackType 实现自定义资源层，仅为了复用 MultiPackResourceManager 等原版资源管线；
 * 代价是对假设 "PackType 只有两个值" 的 mod 不透明。保留以兼容上游 thingpacks 根级 things/ 目录格式。
 */
public class CustomPackType
{
    public static PackType THINGS;

    static
    { // make sure the field has been initialized.
        //noinspection ResultOfMethodCallIgnored
        PackType.values();
    }
}
