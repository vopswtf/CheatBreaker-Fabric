package cc.vops.cheatbreaker.client.util;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.fading.CosineFade;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.resources.Identifier;
import org.lwjgl.opengl.GL11;

public class PauseUtil {
    public static final Identifier outer = CheatBreaker.asset("logo_255_outer.png");
    public static final Identifier inner = CheatBreaker.asset("logo_108_inner.png");
    public static final CosineFade fade = new CosineFade(4000L);

    public static void renderRotatingLogo(PauseScreen screen, GuiGraphicsExtractor gfx) {
        double d = screen.width;
        double d2 = screen.height;

        try {
            float f = 18;
            double d3 = d / (double)2 - (double)f;
            double d4 = screen.children().size() > 2 ? ((float)((net.minecraft.client.gui.components.Button)screen.children().get(1)).getY() - f - (float)32) : (double)-100;

            gfx.pose().pushMatrix();
            gfx.pose().translate((float)d3, (float)d4 - 24);
            gfx.pose().translate(f, f);
            gfx.pose().rotate((float) Math.toRadians(180f * fade.getCurrentValue()));
            gfx.pose().translate(-f, -f);
            RenderUtil.drawIcon(gfx, outer, f, 0.0f, 0.0f);
            gfx.pose().popMatrix();
            RenderUtil.drawIcon(gfx, inner, f, (float)d3, (float)d4 - 24);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}
