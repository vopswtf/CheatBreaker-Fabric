package cc.vops.cheatbreaker.client.ui.mainmenu.element;

import cc.vops.cheatbreaker.client.ui.fading.ColorFade;
import cc.vops.cheatbreaker.client.ui.mainmenu.AbstractElement;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class TextButtonElement extends AbstractElement {
    private final String text;
    private final ColorFade textColorFade;

    public TextButtonElement(String string) {
        this.text = string;
        this.textColorFade = new ColorFade(-1879048193, -1);
    }

    @Override
    protected void handleElementDraw(GuiGraphicsExtractor gfx, float mouseX, float mouseY, boolean enableMouse) {
//        CBFonts.robotoBold14px.drawString(this.text, this.x + 6f, this.y + 6f, this.textColorFade.get(this.isMouseInside(mouseX, mouseY) && enableMouse).getRGB());
        RenderUtil.drawString(
                gfx,
                Fonts.robotoBold14,
                this.text,
                (this.x + 6f),
                (this.y + 6f),
                this.textColorFade.get(this.isMouseInside(mouseX, mouseY) && enableMouse).getRGB()
        );
    }
}