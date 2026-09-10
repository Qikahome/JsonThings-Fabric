package dev.gigaherz.jsonthings.client;

import dev.gigaherz.jsonthings.util.LoadingIssues;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * thingpack 加载问题提示屏，对应 Forge 1.20.1 版由 {@code ModLoadingWarning} 汇总出的加载错误屏。
 *
 * <p>Fabric Loader 没有等价的加载问题收集/展示 API，故由 JsonThings 自行收集（见 {@link LoadingIssues}）
 * 并在客户端启动后展示：标题 + 可滚动的问题列表 + 「打开模组文件夹 / 返回标题屏幕 / 打开日志文件 / 退出游戏」。
 */
public class LoadingIssuesScreen extends Screen
{
    private static final int LINE_HEIGHT = 9;
    private static final int ENTRY_GAP = 4;
    private static final int LIST_PADDING = 4;
    private static final int BACKGROUND_COLOR = 0xC0200000;
    private static final int ERROR_COLOR = 0xFF8080;
    private static final int WARNING_COLOR = 0xFFD070;
    private static final int TEXT_COLOR = 0xE0E0E0;

    private final List<LoadingIssues.Issue> issues = LoadingIssues.getIssues();
    private final List<MultiLineLabel> labels = new ArrayList<>();

    private double scroll;
    private int contentHeight;
    private int listLeft;
    private int listTop;
    private int listRight;
    private int listBottom;

    public LoadingIssuesScreen()
    {
        super(Component.translatable("text.jsonthings.loading_issues.title"));
    }

    @Override
    public void removed()
    {
        // 玩家关闭该屏（按钮或 Esc）后即视为已确认，不再重复弹出。
        LoadingIssues.acknowledge();
    }

    @Override
    protected void init()
    {
        listLeft = Math.max(20, this.width / 2 - 240);
        listRight = Math.min(this.width - 20, this.width / 2 + 240);
        listTop = 48;
        listBottom = this.height - 48;

        labels.clear();
        contentHeight = 0;
        for (var issue : issues)
        {
            var label = MultiLineLabel.create(this.font, issue.message(), listRight - listLeft - LIST_PADDING * 2);
            labels.add(label);
            contentHeight += label.getLineCount() * LINE_HEIGHT + ENTRY_GAP;
        }
        scroll = 0;

        int buttonWidth = Math.min(150, (this.width - 20) / 4 - 4);
        int buttonHeight = 20;
        int x = (this.width - (buttonWidth * 4 + 3 * 4)) / 2;
        int y = this.height - 32;

        addRenderableWidget(Button.builder(
                        Component.translatable("text.jsonthings.loading_issues.open_mods"),
                        b -> Util.getPlatform().openFile(new File(this.minecraft.gameDirectory, "mods")))
                .bounds(x, y, buttonWidth, buttonHeight).build());
        x += buttonWidth + 4;
        addRenderableWidget(Button.builder(
                        Component.translatable("gui.toTitle"),
                        b -> this.minecraft.setScreen(new TitleScreen()))
                .bounds(x, y, buttonWidth, buttonHeight).build());
        x += buttonWidth + 4;
        addRenderableWidget(Button.builder(
                        Component.translatable("text.jsonthings.loading_issues.open_log"),
                        b -> Util.getPlatform().openFile(new File(this.minecraft.gameDirectory, "logs/latest.log")))
                .bounds(x, y, buttonWidth, buttonHeight).build());
        x += buttonWidth + 4;
        addRenderableWidget(Button.builder(
                        Component.translatable("menu.quit"),
                        b -> this.minecraft.stop())
                .bounds(x, y, buttonWidth, buttonHeight).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        // Screen#render 内部会先画遮罩背景，再画子控件；文字在控件之后画（列表区与按钮不重叠）。
        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.drawCenteredString(this.font, this.title, this.width / 2, 16, 0xFFFFFF);
        graphics.drawCenteredString(this.font,
                Component.translatable("text.jsonthings.loading_issues.summary", this.issues.size()),
                this.width / 2, 30, TEXT_COLOR);

        graphics.fill(listLeft, listTop, listRight, listBottom, BACKGROUND_COLOR);
        graphics.enableScissor(listLeft, listTop, listRight, listBottom);

        int viewHeight = listBottom - listTop;
        int y = listTop + LIST_PADDING - (int) this.scroll;
        for (int i = 0; i < this.labels.size(); i++)
        {
            var label = this.labels.get(i);
            int height = label.getLineCount() * LINE_HEIGHT;
            // 仅渲染可见的条目（列表可能很长）。
            if (y + height >= listTop && y <= listBottom)
            {
                label.renderLeftAligned(graphics, listLeft + LIST_PADDING, y, LINE_HEIGHT, colorOf(this.issues.get(i)));
            }
            y += height + ENTRY_GAP;
        }

        graphics.disableScissor();

        int maxScroll = maxScroll(viewHeight);
        if (maxScroll > 0)
        {
            int barHeight = Math.max(16, viewHeight * viewHeight / this.contentHeight);
            int barY = listTop + (int) ((viewHeight - barHeight) * (this.scroll / maxScroll));
            graphics.fill(listRight - 4, listTop, listRight, listBottom, 0x40FFFFFF);
            graphics.fill(listRight - 4, barY, listRight, barY + barHeight, 0xC0FFFFFF);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY)
    {
        int maxScroll = maxScroll(listBottom - listTop);
        if (maxScroll > 0)
        {
            this.scroll = Mth.clamp(this.scroll - scrollY * 16, 0, maxScroll);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    private int maxScroll(int viewHeight)
    {
        return Math.max(0, this.contentHeight - viewHeight + LIST_PADDING * 2);
    }

    private static int colorOf(LoadingIssues.Issue issue)
    {
        return switch (issue.severity())
        {
            case ERROR -> ERROR_COLOR;
            case WARNING -> WARNING_COLOR;
        };
    }
}
