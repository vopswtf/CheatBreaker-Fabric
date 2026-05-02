package cc.vops.cheatbreaker.client.ui.overlay.element;

import cc.vops.cheatbreaker.client.ui.mainmenu.AbstractElement;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class FlatButtonElement extends AbstractElement {
    private String label;

    public FlatButtonElement(String string) {
        this.label = string;
    }

    @Override
    public void handleElementDraw(GuiGraphicsExtractor gfx, float f, float f2, boolean bl) {
        this.draw(gfx, this.label, f, f2, bl);
    }

    public void draw(GuiGraphicsExtractor gfx, String string, float f, float f2, boolean bl) {
        RenderUtil.drawRect(gfx, this.x, this.y, this.x + this.width, this.y + this.height, bl && this.isMouseInside(f, f2) ? -16747106 : -13158601);
        RenderUtil.drawCenteredString(gfx, Fonts.playRegular14, string, this.x + this.width / 2.0f, this.y + this.height / 2.0f - 2.5f, -1);
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String string) {
        this.label = string;
    }
}
