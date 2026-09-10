package dev.gigaherz.jsonthings.util;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * thingpack 加载期间发现的非致命问题（解析失败的条目等）的收集器。
 *
 * <p>对应 NeoForge 版的 {@code ModLoader.addLoadingIssue(new ModLoadingIssue(...))}。
 * Fabric Loader 没有等价的"加载问题"收集/展示 API（{@code net.fabricmc.loader.api} 下不存在相关类型），
 * 因此由 JsonThings 自行收集，并在客户端启动后由一个自建屏提示玩家。
 *
 * @see dev.gigaherz.jsonthings.client.LoadingIssuesScreen
 */
public final class LoadingIssues
{
    /** 与 NeoForge {@code ModLoadingIssue.Severity} 对齐。 */
    public enum Severity
    {
        WARNING,
        ERROR
    }

    public record Issue(Severity severity, Component message)
    {
    }

    // thingpack 解析在资源加载线程池上进行、读取在主线程，故加锁。
    private static final List<Issue> ISSUES = Collections.synchronizedList(new ArrayList<>());

    /** 玩家是否已确认过该提示（关闭过提示屏）。 */
    private static volatile boolean acknowledged;

    private LoadingIssues()
    {
    }

    /**
     * @param translationKey 提示文案的翻译键
     * @param args           翻译键的参数（其中的异常原因取自异常原文，不参与翻译）
     */
    public static void add(Severity severity, String translationKey, Object... args)
    {
        ISSUES.add(new Issue(severity, Component.translatable(translationKey, args)));
    }

    public static boolean isAcknowledged()
    {
        return acknowledged;
    }

    /** 玩家已关闭过提示屏。 */
    public static void acknowledge()
    {
        acknowledged = true;
    }

    public static boolean isEmpty()
    {
        return ISSUES.isEmpty();
    }

    public static List<Issue> getIssues()
    {
        synchronized (ISSUES)
        {
            return List.copyOf(ISSUES);
        }
    }
}
