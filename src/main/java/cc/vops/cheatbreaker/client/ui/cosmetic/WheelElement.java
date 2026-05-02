package cc.vops.cheatbreaker.client.ui.cosmetic;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.fading.ColorFade;
import cc.vops.cheatbreaker.client.ui.mainmenu.AbstractElement;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.*;

public class WheelElement extends AbstractElement {
    private final WheelGUI wheelgui;
    private final float location;
    private final IconButton iconButton;
    private final Color baseColor = new Color(0.3F, 0.3F, 0.3F, 0.3F);
    private final ColorFade radialColorFade;
    private final ColorFade iconColorFade;

    public WheelElement(WheelGUI var1, int var2, IconButton var3) {
        this.wheelgui = var1;
        float var4 = 45.0F;
        this.location = var4 / 2.0F + (float) var2 * var4;
        this.iconButton = var3;
        this.radialColorFade = new ColorFade(500L, (new Color(0.2F, 0.2F, 0.2F, 0.1F)).getRGB(),
                (new Color(0.2F, 0.2F, 0.2F, 0.1F)).getRGB());
        this.iconColorFade = new ColorFade(500L, (new Color(0.0F, 0.0F, 0.0F, 0.9F)).getRGB(), -1);
    }

    @Override
    protected void handleElementDraw(GuiGraphicsExtractor gfx, float var1, float var2, boolean var3) {
        this.radialColorFade.setEndColor(0x8033334D);
        boolean var4 = this.iconButton != null && this.isMouseInsideElement(var1, var2) && var3;
        Color var5 = this.radialColorFade.get(var4);
        float var7 = CheatBreaker.getScaledWidth() / CheatBreaker.getScaleFactor();
        float var8 = CheatBreaker.getScaledHeight() / CheatBreaker.getScaleFactor();
        float var9 = 10.0F;
        float var10 = (float) this.wheelgui.tick >= var9 ? 1.0F : (float) this.wheelgui.tick / var9;
//        GL11.glPushMatrix();
        gfx.pose().pushMatrix();
        int color;
        if (this.iconButton == null) {
//            GL11.glColor4d((float) this.baseColor.getRed() / 255.0F, (float) this.baseColor.getGreen() / 255.0F, (float) this.baseColor.getBlue() / 255.0F, (float) this.baseColor.getAlpha() / 255.0F * var10);
            color = CheatBreaker.getColor((float) this.baseColor.getRed() / 255.0F, (float) this.baseColor.getGreen() / 255.0F, (float) this.baseColor.getBlue() / 255.0F, (float) this.baseColor.getAlpha() / 255.0F * var10);
        } else {
//            GL11.glColor4d((float) var5.getRed() / 255.0F, (float) var5.getGreen() / 255.0F, (float) var5.getBlue() / 255.0F, (float) var5.getAlpha() / 255.0F * var10);
            color = CheatBreaker.getColor((float) var5.getRed() / 255.0F, (float) var5.getGreen() / 255.0F, (float) var5.getBlue() / 255.0F, (float) var5.getAlpha() / 255.0F * var10);
        }

        RenderUtil.drawRadial(gfx, (float) var7 / 2.0F, (float) var8 / 2.0F, 88.0, 20.0, this.location - (45 * 3),  this.location - 90F, color);
//        GL11.glPopMatrix();
        gfx.pose().popMatrix();

        if (this.iconButton != null) {
            var5 = this.iconColorFade.get(var4);
//            GL11.glPushMatrix();
            gfx.pose().pushMatrix();
            float var11 = this.location - 90.0F;
            var11 -= 22.5F;
            double var12 = Math.toRadians(var11);
            byte var14 = 60;
            double var15 = (double) ((float) var7 / 2.0F) + (double) var14 * Math.cos(var12);
            double var17 = (double) ((float) var8 / 2.0F) + (double) var14 * Math.sin(var12);
//            GL11.glColor4d((float) var5.getRed() / 255.0F, (float) var5.getGreen() / 255.0F, (float) var5.getBlue() / 255.0F, (float) var5.getAlpha() / 255.0F * var10);
            int c = CheatBreaker.getColor((float) var5.getRed() / 255.0F, (float) var5.getGreen() / 255.0F, (float) var5.getBlue() / 255.0F, (float) var5.getAlpha() / 255.0F * var10);
            RenderUtil.drawIcon(gfx, this.iconButton.getImage(), 20.0F, (float) var15 - 20.0F, (float) var17 - 20.0F, c);
            gfx.pose().popMatrix();
        }

    }

    public boolean isMouseInsideElement(float var1, float var2) {
        float var4 = CheatBreaker.getScaledWidth() / CheatBreaker.getScaleFactor();
        float var5 = CheatBreaker.getScaledHeight() / CheatBreaker.getScaleFactor();
        int var6 = (int) (var1 - (float) (var4 / 2));
        int var7 = (int) (var2 - (float) (var5 / 2));
        double var8 = Math.sqrt(var6 * var6 + var7 * var7);
        double var10 = Math.toDegrees(Math.atan2(var7, var6)) + 90.0;
        double var12 = this.location - 45.0F;
        if (var12 < 0.0) {
            var12 += 360.0;
        }

        double var14 = this.location;
        boolean var16 = var10 < 0.0;
        if (var12 > var14) {
            var14 += 360.0;
            var16 = true;
        }

        if (var16) {
            var10 += 360.0;
        }

        return var8 >= 20.0 && var8 <= 175.0 && var10 >= var12 && var10 <= var14;
    }
}
