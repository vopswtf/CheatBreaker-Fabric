package cc.vops.cheatbreaker.client.module.type;


import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.event.type.GuiDrawEvent;
import cc.vops.cheatbreaker.client.event.type.RenderPreviewEvent;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.module.ModuleRule;
import cc.vops.cheatbreaker.client.ui.module.CBModulesGui;
import cc.vops.cheatbreaker.client.ui.module.GuiAnchor;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.BlankFormat;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.world.scores.*;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.util.Comparator;
import java.util.Objects;

public class ScoreboardModule extends AbstractModule {
    public static ModuleRule rule = ModuleRule.SCOREBOARD;
    public Setting removeNumbers;
    private Setting titleBackgroundColor;
    private Setting backgroundColor;

    public ScoreboardModule() {
        super("Scoreboard");
        this.setDefaultAnchor(GuiAnchor.RIGHT_MIDDLE);
        this.removeNumbers = new Setting(this, "Remove Scoreboard numbers").setValue(true);
        this.titleBackgroundColor = new Setting(this, "Title Background Color").setDefaultValue(1711276032).setMinMax(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.backgroundColor = new Setting(this, "Background Color").setDefaultValue(1711276032).setMinMax(Integer.MIN_VALUE, Integer.MAX_VALUE);

        this.addEvent(GuiDrawEvent.class, this::renderReal);
        this.addEvent(RenderPreviewEvent.class, this::renderPreview);
        this.setDefaultState(true);
    }

    private void renderPreview(RenderPreviewEvent event) {
        if (!this.isRenderHud()) return;
        if (!this.isEnabled()) return;
        if (this.getSidebarObjective() != null) return;
        GuiGraphicsExtractor gfx = event.getGraphics();

        gfx.pose().pushMatrix();
        gfx.pose().scale(CheatBreaker.getScaleFactor(), CheatBreaker.getScaleFactor());

        this.scaleAndTranslate(gfx);

        Scoreboard scoreboard = new Scoreboard();
        scoreboard.addObjective(
                "CheatBreaker",
                ObjectiveCriteria.DUMMY,
                Component.literal(ChatFormatting.RED + "" + ChatFormatting.BOLD + "Cheat" + ChatFormatting.RESET + ChatFormatting.WHITE + "Breaker"),
                ObjectiveCriteria.RenderType.INTEGER,
                false,
                isRemoveNumbers() ? BlankFormat.INSTANCE : StyledFormat.SIDEBAR_DEFAULT
        );
        scoreboard.setDisplayObjective(DisplaySlot.SIDEBAR, scoreboard.getObjective("CheatBreaker"));

        scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly("Steve"), scoreboard.getObjective("CheatBreaker")).set(1);
        scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly("Alex"), scoreboard.getObjective("CheatBreaker"));

        this.drawObjective(gfx, Objects.requireNonNull(scoreboard.getObjective("CheatBreaker")), minecraft.font);

        gfx.pose().popMatrix();
    }

    private static final Comparator<PlayerScoreEntry> SCORE_DISPLAY_ORDER = Comparator.comparing(PlayerScoreEntry::value)
            .reversed()
            .thenComparing(PlayerScoreEntry::owner, String.CASE_INSENSITIVE_ORDER);

    private void drawObjective(GuiGraphicsExtractor gfx, Objective objective, net.minecraft.client.gui.Font font) {
        Scoreboard scoreboard = objective.getScoreboard();
        NumberFormat numberformat = objective.numberFormatOrDefault(StyledFormat.SIDEBAR_DEFAULT);

        if (isRemoveNumbers()) {
            numberformat = BlankFormat.INSTANCE;
        }

        NumberFormat finalNumberformat = numberformat;
        DisplayEntry[] agui$1displayentry = scoreboard.listPlayerScores(objective)
                .stream()
                .filter(p_308174_ -> !p_308174_.isHidden())
                .sorted(SCORE_DISPLAY_ORDER)
                .limit(15L)
                .map(p_308178_ -> {
                    PlayerTeam playerteam = scoreboard.getPlayersTeam(p_308178_.owner());
                    Component component1 = p_308178_.ownerName();
                    Component component2 = PlayerTeam.formatNameForTeam(playerteam, component1);
                    Component component3 = p_308178_.formatValue(finalNumberformat);
                    int k3 = font.width(component3);
                    return new DisplayEntry(component2, component3, k3);
                })
                .toArray(DisplayEntry[]::new);

        Component component = objective.getDisplayName();
        int i = font.width(component);
        int j = i;
        int k = font.width(": ");

        for (DisplayEntry gui$1displayentry : agui$1displayentry) {
            j = Math.max(j, font.width(gui$1displayentry.name) + (gui$1displayentry.scoreWidth > 0 ? k + gui$1displayentry.scoreWidth : 0));
        }

        int l2 = agui$1displayentry.length;
        int i3 = l2 * 9;

        int background = this.backgroundColor.getColorValue();
        int titleBackground = this.titleBackgroundColor.getColorValue();

        int totalHeight = l2 * 9;
        int totalWidth = j + 2;

        int originX = 3;
        int originY = 2;

        int backgroundTop = originY;
        int backgroundBottom = originY + totalHeight + 9;
        int backgroundLeft = originX;
        int backgroundRight = originX + totalWidth;

        RenderUtil.drawRect(gfx, backgroundLeft - 2, backgroundTop - 1, backgroundRight + 2, backgroundTop + 9, titleBackground);
        RenderUtil.drawRect(gfx, backgroundLeft - 2, backgroundTop + 9, backgroundRight + 2, backgroundBottom, background);

        gfx.text(font, component, backgroundLeft + (totalWidth - i) / 2, backgroundTop, -1, false);

        for (int idx = 0; idx < l2; idx++) {
            DisplayEntry entry = agui$1displayentry[idx];
            int lineY = backgroundTop + 9 + idx * 9;
            gfx.text(font, entry.name, backgroundLeft, lineY, -1, false);
            gfx.text(font, entry.score, backgroundRight - entry.scoreWidth - 1, lineY, -1, false);
        }

        this.setDimensions(totalWidth + 6, totalHeight + 12);
    }

    private Objective getSidebarObjective() {
        if (this.minecraft.player == null || this.minecraft.level == null) return null;
        Scoreboard scoreboard = this.minecraft.level.getScoreboard();
        Objective objective = null;
        PlayerTeam playerteam = scoreboard.getPlayersTeam(this.minecraft.player.getScoreboardName());
        if (playerteam != null) {
            DisplaySlot displayslot = DisplaySlot.teamColorToSlot(playerteam.getColor());
            if (displayslot != null) {
                objective = scoreboard.getDisplayObjective(displayslot);
            }
        }

        return objective != null ? objective : scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
    }

    private void renderReal(GuiDrawEvent guiDrawEvent) {
        if (!this.isRenderHud()) return;
        Objective objective = this.getSidebarObjective();
        if (objective == null) return;

        GuiGraphicsExtractor gfx = guiDrawEvent.getGraphics();

        gfx.pose().pushMatrix();
        gfx.pose().scale(CheatBreaker.getScaleFactor(), CheatBreaker.getScaleFactor());

        this.scaleAndTranslate(gfx);
        this.drawObjective(gfx, objective, minecraft.font);

        gfx.pose().popMatrix();
    }

    private boolean isRemoveNumbers() {
        return rule == ModuleRule.SCOREBOARD ? (Boolean) this.removeNumbers.getValue() : false;
    }

    record DisplayEntry(Component name, Component score, int scoreWidth) {}
}
