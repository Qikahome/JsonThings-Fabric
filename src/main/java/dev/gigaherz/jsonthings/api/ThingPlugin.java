package dev.gigaherz.jsonthings.api;

import dev.gigaherz.jsonthings.things.parsers.ThingResourceManager;

/**
 * JsonThings 的 mod 扩展点。
 *
 * <p>NeoForge 上 mod 在构造器里直接调用 {@code ThingResourceManager.registerParser}、
 * {@code ThingRegistries} 与各 Flex 类型的 register 方法，因为"mod 构造"与"thingpack 加载"是加载器
 * 划分的两个阶段，顺序有保证。Fabric 没有这个分界：各 mod 的 {@code main} 入口点是**并行**调用的，
 * JsonThings 在自己的入口点里就完成了注册与解析，后注册的 mod 必然错过解析。
 *
 * <p>因此需要扩展的 mod 改为声明 {@code "jsonthings"} 入口点，由 JsonThings 在固定时机回调，
 * 得到与 Forge 等价的顺序保证：
 * <pre>{@code
 * "entrypoints": {
 *   "main": ["your.mod.YourMod"],
 *   "jsonthings": ["your.mod.YourThingPlugin"]
 * }
 * }</pre>
 *
 * <p>注意：该入口点不会被 Fabric Loader 自动调用，而是由 JsonThings 在初始化过程中主动获取
 * （非标准阶段的入口点 Fabric 不做分发，这也正是它能控制时机的原理）。
 */
public interface ThingPlugin
{
    /** 本扩展点使用的 entrypoint 键。 */
    String ENTRYPOINT_KEY = "jsonthings";

    /**
     * 在 {@code ThingRegistries.initRegistries()} 与内建 parser 注册之后、thingpack 解析
     * （{@code ThingResourceManager.beginLoading()}）之前调用。
     *
     * <p>在这里注册自定义 parser、自定义方块/物品类型（Flex 类型）以及自建注册表
     * （{@code ThingRegistries}）条目 —— 也就是对应 Forge 在 mod 构造器里做的那部分。
     */
    default void registerThingTypes(ThingResourceManager manager)
    {
    }

    /**
     * 在全部 thingpack 解析完成、且各 parser 已把 thing 注册进游戏注册表之后调用。
     *
     * <p>在这里做依赖解析结果的工作，例如注册游戏规则、内置数据包。
     */
    default void afterThingLoading(ThingResourceManager manager)
    {
    }
}
