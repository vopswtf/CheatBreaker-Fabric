package cc.vops.cheatbreaker.client.ui.overlay;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.fading.AbstractFade;
import cc.vops.cheatbreaker.client.ui.fading.FloatFade;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.List;

public class Alert {
    private final AbstractFade fade = new FloatFade(275L);
    private static final int lIIIIIIIIIlIllIIllIlIIlIl = 140;
    private static final int IlllIIIlIlllIllIlIIlllIlI = 55;
    private boolean showTitleBar;
    private float x;
    private float lastHeight;
    private float height;
    private final String IllIIIIIIIlIlIllllIIllIII;
    private final String[] lines;
    private final long createdTime;

    public Alert(String string, String[] arrstring) {
        this.IllIIIIIIIlIlIllllIIllIII = string;
        this.lines = arrstring;
        this.createdTime = System.currentTimeMillis();

        float scaleFactor = CheatBreaker.getScaleFactor();

        int n = Alert.getWidth();
        float f = CheatBreaker.getScaledWidth() * (1f / scaleFactor) - (float)n;
        float f2 = CheatBreaker.getScaledHeight() * (1f / scaleFactor);

        this.x = f;
        this.height = f2;
        this.lastHeight = f2;
    }

    public void resize() {
        float scaleFactor = CheatBreaker.getScaleFactor();

        int n = Alert.getWidth();
        float f = CheatBreaker.getScaledWidth() * (1f / scaleFactor) - (float)n;
        float f2 = CheatBreaker.getScaledHeight() * (1f / scaleFactor);

        this.x = f;
        this.height = f2;
        this.lastHeight = f2;
    }

    public void drawAlert(GuiGraphicsExtractor gfx) {
        if (height < 0) {
            resize();
            return;
        }

        gfx.pose().pushMatrix();

        float f = this.lastHeight - (this.lastHeight - this.height) * this.fade.getCurrentValue();
        if (this.showTitleBar) {
//            gfx.fillGradient((int) this.x, (int) f, (int) (this.x + 140), (int) (f + 55), -819057106, -822083584);
            RenderUtil.drawGradientRect(gfx, this.x, f, (this.x + 140), (f + 55), -819057106, -822083584);
            for (int i = 0; i < this.lines.length && i <= 3; ++i) {
//                CheatBreaker.getInstance().playRegular16px.drawString(this.lines[i], this.x + (float)4, f + (float)4 + (float)(i * 10), -1);
                RenderUtil.drawString(gfx, Fonts.playRegular16, this.lines[i], this.x + (float)4, f + (float)4 + (float)(i * 10), -1);
            }
        } else {
//            gfx.fillGradient((int) this.x, (int) f, (int) (this.x + 140), (int) (f + 55), -819057106, -822083584);
            RenderUtil.drawGradientRect(gfx, this.x, f, (this.x + 140), (f + 55), -819057106, -822083584);
//            CheatBreaker.getInstance().playRegular16px.drawString(this.IllIIIIIIIlIlIllllIIllIII, this.x + (float)4, f + (float)4, -1);
            RenderUtil.drawString(gfx, Fonts.playRegular16, this.IllIIIIIIIlIlIllllIIllIII, this.x + (float)4, f + (float)4, -1);
            RenderUtil.drawRect(gfx, this.x + (float)4, f + 14.5f, this.x + (float)140 - (float)5, f + (float)15, 0x2E5E5E5E);
            for (int i = 0; i < this.lines.length && i <= 2; ++i) {
//                CheatBreaker.getInstance().playRegular16px.drawString(this.lines[i], this.x + (float)4, f + (float)17 + (float)(i * 10), -1);
                RenderUtil.drawString(gfx, Fonts.playRegular16, this.lines[i], this.x + (float)4, f + (float)17 + (float)(i * 10), -1);
            }
        }
        if (!(Minecraft.getInstance().screen instanceof SocialOverlayScreen)) {
//            CheatBreaker.getInstance().playRegular16px.drawString("Press Shift + Tab", this.x + (float)4, f + (float) Alert.IIIIllIIllIIIIllIllIIIlIl() - (float)12, 0x6FFFFFFF);
            RenderUtil.drawString(gfx, Fonts.playRegular16, "Press Shift + Tab", this.x + (float)4, f + (float) Alert.getHeight() - (float)12, 0x6FFFFFFF);
        }
        gfx.pose().popMatrix();
    }

    public void showTitleBar(float f) {
        this.lastHeight = this.height;
        this.height = f;
        this.fade.reset();
    }

    public boolean isFading() {
        return !this.fade.hasStartTime() || this.fade.isExpired();
    }

    public void setMaxHeight(float f) {
        this.lastHeight = this.height;
        this.height = f;
        this.fade.reset();
    }

    public boolean shouldDisplay() {
        return System.currentTimeMillis() - this.createdTime > 3500L;
    }

    public static void displayMessage(String string, String string2) {
        SocialOverlayScreen.getInstance().queueAlert(string, string2);
    }

    public static void showTitleBar(String string) {
        SocialOverlayScreen.getInstance().setSection(string);
    }

    public static int getWidth() {
        return 140;
    }

    public static int getHeight() {
        return 55;
    }

    public void showTitleBar(boolean bl) {
        this.showTitleBar = bl;
    }

    public float getX() {
        return this.x;
    }

    public float getLastHeight() {
        return this.lastHeight;
    }

    public float getMaxHeight() {
        return this.height;
    }

    public void setX(float f) {
        this.x = f;
    }

    public void shouldDisplay(float f) {
        this.lastHeight = f;
    }

    public void getWidth(float f) {
        this.height = f;
    }
}
