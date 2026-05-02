package cc.vops.cheatbreaker.client.module.type;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.event.type.GuiDrawEvent;
import cc.vops.cheatbreaker.client.event.type.RenderPreviewEvent;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.ui.module.GuiAnchor;
import cc.vops.cheatbreaker.mixin.module.bossbar.BossBarAccessor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;

import java.util.UUID;

public class BossBarModule extends AbstractModule {

    public BossBarModule() {
        super("Boss bar");
        this.setDefaultAnchor(GuiAnchor.MIDDLE_TOP);
        this.addEvent(GuiDrawEvent.class, this::renderReal);
        this.addEvent(RenderPreviewEvent.class, this::renderPreview);
        this.setPreviewLabel("Boss Bar", 1.0f);
        this.setDefaultState(true);
    }

    public void renderPreview(RenderPreviewEvent event) {
        if (!((BossBarAccessor) minecraft.gui.getBossOverlay()).getEvents().isEmpty()) return;
        GuiGraphicsExtractor gfx = event.getGraphics();
//        GL11.glPushMatrix();
        gfx.pose().pushMatrix();

        gfx.pose().scale(CheatBreaker.getScaleFactor(), CheatBreaker.getScaleFactor());
        this.scaleAndTranslate(gfx);

        gfx.nextStratum();

        int i = gfx.guiWidth();
        int j = 12;

        LerpingBossEvent lerpingbossevent = new LerpingBossEvent(
                UUID.randomUUID(),
                Component.literal("Wither"),
                1f,
                BossEvent.BossBarColor.PURPLE,
                BossEvent.BossBarOverlay.PROGRESS,
                false, false, false
        );
        int k = i / 2 - 91;
        this.drawBar(gfx, 0, j, lerpingbossevent);
        Component component = lerpingbossevent.getName();
        int l = this.minecraft.font.width(component);
        int i1 = 182 / 2 - l / 2;
        int j1 = j - 9;
        gfx.text(this.minecraft.font, component, i1, j1, -1);
        j += 10 + 9;
        this.setDimensions(182, 20);

        gfx.pose().popMatrix();
    }

    public void renderReal(GuiDrawEvent event) {
        if (((BossBarAccessor) minecraft.gui.getBossOverlay()).getEvents().isEmpty()) return;
        GuiGraphicsExtractor gfx = event.getGraphics();
        gfx.pose().pushMatrix();

        gfx.pose().scale(CheatBreaker.getScaleFactor(), CheatBreaker.getScaleFactor());
        this.scaleAndTranslate(gfx);

        gfx.nextStratum();

        int i = gfx.guiWidth();
        int j = 12;

        for (LerpingBossEvent lerpingbossevent : ((BossBarAccessor) minecraft.gui.getBossOverlay()).getEvents().values()) {
            this.drawBar(gfx, 0, j, lerpingbossevent);
            Component component = lerpingbossevent.getName();
            int l = this.minecraft.font.width(component);
            int i1 = 182 / 2 - l / 2;
            int j1 = j - 9;
            gfx.text(this.minecraft.font, component, i1, j1, -1);
            j += 10 + 9;
            if (j >= gfx.guiHeight() / 3) {
                break;
            }
        }

        this.setDimensions(182, j - 11);
        gfx.pose().popMatrix();
    }

    private void drawBar(GuiGraphicsExtractor p_283672_, int p_283570_, int p_283306_, BossEvent p_283156_) {
        this.drawBar(p_283672_, p_283570_, p_283306_, p_283156_, 182, BAR_BACKGROUND_SPRITES, OVERLAY_BACKGROUND_SPRITES);
        int i = Mth.lerpDiscrete(p_283156_.getProgress(), 0, 182);
        if (i > 0) {
            this.drawBar(p_283672_, p_283570_, p_283306_, p_283156_, i, BAR_PROGRESS_SPRITES, OVERLAY_PROGRESS_SPRITES);
        }
    }

    private void drawBar(
            GuiGraphicsExtractor p_281657_, int p_283675_, int p_282498_, BossEvent p_281288_, int p_283619_, Identifier[] p_298746_, Identifier[] p_298698_
    ) {
        p_281657_.blitSprite(RenderPipelines.GUI_TEXTURED, p_298746_[p_281288_.getColor().ordinal()], 182, 5, 0, 0, p_283675_, p_282498_, p_283619_, 5);
        if (p_281288_.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
            p_281657_.blitSprite(RenderPipelines.GUI_TEXTURED, p_298698_[p_281288_.getOverlay().ordinal() - 1], 182, 5, 0, 0, p_283675_, p_282498_, p_283619_, 5);
        }
    }


    private static final Identifier[] BAR_BACKGROUND_SPRITES = new Identifier[]{
            Identifier.withDefaultNamespace("boss_bar/pink_background"),
            Identifier.withDefaultNamespace("boss_bar/blue_background"),
            Identifier.withDefaultNamespace("boss_bar/red_background"),
            Identifier.withDefaultNamespace("boss_bar/green_background"),
            Identifier.withDefaultNamespace("boss_bar/yellow_background"),
            Identifier.withDefaultNamespace("boss_bar/purple_background"),
            Identifier.withDefaultNamespace("boss_bar/white_background")
    };
    private static final Identifier[] BAR_PROGRESS_SPRITES = new Identifier[]{
            Identifier.withDefaultNamespace("boss_bar/pink_progress"),
            Identifier.withDefaultNamespace("boss_bar/blue_progress"),
            Identifier.withDefaultNamespace("boss_bar/red_progress"),
            Identifier.withDefaultNamespace("boss_bar/green_progress"),
            Identifier.withDefaultNamespace("boss_bar/yellow_progress"),
            Identifier.withDefaultNamespace("boss_bar/purple_progress"),
            Identifier.withDefaultNamespace("boss_bar/white_progress")
    };
    private static final Identifier[] OVERLAY_BACKGROUND_SPRITES = new Identifier[]{
            Identifier.withDefaultNamespace("boss_bar/notched_6_background"),
            Identifier.withDefaultNamespace("boss_bar/notched_10_background"),
            Identifier.withDefaultNamespace("boss_bar/notched_12_background"),
            Identifier.withDefaultNamespace("boss_bar/notched_20_background")
    };
    private static final Identifier[] OVERLAY_PROGRESS_SPRITES = new Identifier[]{
            Identifier.withDefaultNamespace("boss_bar/notched_6_progress"),
            Identifier.withDefaultNamespace("boss_bar/notched_10_progress"),
            Identifier.withDefaultNamespace("boss_bar/notched_12_progress"),
            Identifier.withDefaultNamespace("boss_bar/notched_20_progress")
    };
}
