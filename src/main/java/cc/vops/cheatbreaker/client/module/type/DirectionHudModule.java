package cc.vops.cheatbreaker.client.module.type;


import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.event.type.GuiDrawEvent;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.ui.module.GuiAnchor;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import com.mojang.blaze3d.opengl.GlStateManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class DirectionHudModule extends AbstractModule {

    private final Setting markerColor;
    private final Setting directionColor;
    private final Setting showWhileTyping;
    private final Identifier texture = CheatBreaker.asset("textures/compass.png");

    public DirectionHudModule() {
        super("Direction HUD");
        this.setDefaultAnchor(GuiAnchor.MIDDLE_TOP);
        this.setState(false);
        this.showWhileTyping = new Setting(this, "Show While Typing").setValue(true);
        this.markerColor = new Setting(this, "Marker Color").setValue(-43691).setMinMax(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.directionColor = new Setting(this, "Direction Color").setValue(-1).setMinMax(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.setPreviewIcon(CheatBreaker.asset("icons/mods/dirhud.png"), 65, 12);
        this.addEvent(GuiDrawEvent.class, this::renderReal);
    }

    private void renderReal(GuiDrawEvent guiDrawEvent) {
        if (!this.isRenderHud()) {
            return;
        }
        GuiGraphicsExtractor gfx = guiDrawEvent.getGraphics();
        gfx.pose().pushMatrix();
        gfx.pose().scale(CheatBreaker.getScaleFactor(), CheatBreaker.getScaleFactor());
        GlStateManager._enableBlend();
        this.scaleAndTranslate(gfx);
        this.setDimensions(66, 18);
        if (!(minecraft.screen instanceof ChatScreen) || (Boolean) this.showWhileTyping.getValue()) {
            this.render(gfx, guiDrawEvent.getDeltaTracker());
        }
        GlStateManager._disableBlend();
        gfx.pose().popMatrix();
    }

    private void render(GuiGraphicsExtractor gfx, DeltaTracker tracker) {
        if (minecraft.player == null) return;
        float partialTick = tracker == null ? 1.0f : tracker.getGameTimeDeltaTicks();

        float interpolatedRot = Mth.lerp(partialTick, minecraft.player.yRotO, minecraft.player.getYRot()) % 360;
        if (interpolatedRot < 0) interpolatedRot += 360;

        float n = (interpolatedRot * (float)256 / (float)360) + 0.5f;
        n = n % 256;

        int n2 = 0;
        int n3 = 0;
        int backgroundColor = 0xFF212121;

        if ((Integer)this.directionColor.getValue() != 4095) {
            int n4 = this.directionColor.getColorValue();
            int color = CheatBreaker.getColor((float)(backgroundColor >> 16 & 0xFF) / (float)255, (float)(backgroundColor >> 8 & 0xFF) / (float)255, (float)(backgroundColor & 0xFF) / (float)255, (float)(backgroundColor >> 24 & 255) / (float)255);
            if (n < 128) {
                RenderUtil.drawTexturedModalRect(gfx, texture, n3, n2, n, 0, 65, 12, color);
            } else {
                RenderUtil.drawTexturedModalRect(gfx, texture, n3, n2, n - 128, 12, 65, 12, color);
            }
            int color2 = CheatBreaker.getColor((float)(n4 >> 16 & 0xFF) / (float)255, (float)(n4 >> 8 & 0xFF) / (float)255, (float)(n4 & 0xFF) / (float)255, 1.0f);
            if (n < 128) {
                RenderUtil.drawTexturedModalRect(gfx, texture, n3, n2, n, 24, 65, 12, color2);
            } else {
                RenderUtil.drawTexturedModalRect(gfx, texture, n3, n2, n - 128, 36, 65, 12, color2);
            }
        } else {
            if (n < 128) {
                RenderUtil.drawTexturedModalRect(gfx, texture, n3, n2, n, 0, 65, 12, 0);
            } else {
                RenderUtil.drawTexturedModalRect(gfx, texture, n3, n2, n - 128, 12, 65, 12, -100);
            }
        }
        RenderUtil.drawString(gfx, minecraft.font, "|", n3 + 32, n2 + 1, this.markerColor.getColorValue());
        RenderUtil.drawString(gfx, minecraft.font, "|§r", n3 + 32, n2 + 5, this.markerColor.getColorValue());
    }


}
