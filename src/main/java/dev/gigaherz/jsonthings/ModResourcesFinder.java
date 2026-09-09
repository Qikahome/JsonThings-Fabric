/*
 * JsonThings (Fabric) — 从各 mod 的 jar 中寻找 things/ 目录并包装为资源包源。
 * 对应上游 Forge 版 ModResourcesFinder；Forge 的 IModFile/PathPackResources/
 * ModLoaderWarning 机制在 Fabric 下由 ModContainer.findPath + 日志替代。
 */
package dev.gigaherz.jsonthings;

import com.mojang.logging.LogUtils;
import dev.gigaherz.jsonthings.util.CustomPackType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import org.slf4j.Logger;

import java.util.function.Consumer;

class ModResourcesFinder
{
    public static final Logger LOGGER = LogUtils.getLogger();

    /** 生成一个 RepositorySource：扫描所有加载的 mod，将根目录含 things/ 的 jar 注册为 thingpack。 */
    static RepositorySource buildPackFinder()
    {
        return (Consumer<Pack> packList) -> {
            for (ModContainer container : FabricLoader.getInstance().getAllMods())
            {
                String modId = container.getMetadata().getId();
                if (modId.equals("minecraft") || modId.equals("fabric"))
                    continue;

                container.findPath("things").ifPresent(path -> {
                    // 与 Forge 对齐：以整个 mod 为 pack 根（mcmeta 取 jar 根），things/ 仅作为包内目录，
                    // 因此不要求 things/ 目录内自带 pack.mcmeta。
                    final java.util.List<java.nio.file.Path> roots = container.getRootPaths();
                    if (roots.isEmpty())
                        return;
                    final String name = "mod:" + modId;
                    PackLocationInfo location = new PackLocationInfo(
                            name, Component.literal(name), PackSource.DEFAULT, java.util.Optional.empty());
                    Pack.ResourcesSupplier resources = new Pack.ResourcesSupplier()
                    {
                        @Override
                        public net.minecraft.server.packs.PackResources openPrimary(PackLocationInfo info)
                        {
                            return new PathPackResources(info, roots.get(0));
                        }

                        @Override
                        public net.minecraft.server.packs.PackResources openFull(PackLocationInfo info, Pack.Metadata metadata)
                        {
                            return openPrimary(info);
                        }
                    };
                    final Pack pack = Pack.readMetaAndCreate(
                            location,
                            resources,
                            CustomPackType.THINGS,
                            new PackSelectionConfig(false, Pack.Position.BOTTOM, false));
                    if (pack == null)
                    {
                        LOGGER.warn("Mod '{}' has a things/ folder but no valid pack.mcmeta; it will not be loaded as a thingpack.", modId);
                        return;
                    }
                    LOGGER.debug("Registering thingpack {} from mod {}", name, modId);
                    packList.accept(pack);
                });
            }
        };
    }
}
