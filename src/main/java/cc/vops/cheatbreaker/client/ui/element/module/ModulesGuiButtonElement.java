package cc.vops.cheatbreaker.client.ui.element.module;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.element.AbstractModulesGuiElement;
import cc.vops.cheatbreaker.client.ui.element.AbstractScrollableElement;
import cc.vops.cheatbreaker.client.ui.module.CBModulesGui;
import cc.vops.cheatbreaker.client.util.font.CBFontRenderer;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public class ModulesGuiButtonElement extends AbstractModulesGuiElement {
    public int highlightColor;
    public String displayString;
    public final AbstractScrollableElement scrollableElement;
    public boolean moduleAllowedToAddToHud = true;
    public CBFontRenderer font;
    private int timing = 0;

    public ModulesGuiButtonElement(CBFontRenderer font, AbstractScrollableElement scrollableElement, String displayString, int n, int n2, int n3, int n4, int n5, float scale) {
        super(scale);
        this.font = font;
        this.displayString = displayString;
        this.setDimensions(n, n2, n3, n4);
        this.highlightColor = n5;
        this.scrollableElement = scrollableElement;
    }

    public ModulesGuiButtonElement(AbstractScrollableElement scrollableElement, String displayString, int n, int n2, int n3, int n4, int n5, float scale) {
        this(Fonts.playBold22, scrollableElement, displayString, n, n2, n3, n4, n5, scale);
    }

    @Override
    public void handleDrawElement(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTicks) {
        float f2;
        boolean mouseInside = this.isMouseInside(mouseX, mouseY, false);
        int n3 = 120;

        if (mouseInside && this.moduleAllowedToAddToHud) {
            RenderUtil.drawRect(gui, this.x - 2, this.y - 2, this.x + this.width + 2, this.y + this.height + 2, -854025);
            f2 = CBModulesGui.getSmoothFloat(790);
            this.timing = (float)this.timing + f2 < (float)n3 ? (int)((float)this.timing + f2) : n3;
        } else if (this.timing > 0) {
            f2 = CBModulesGui.getSmoothFloat(790);
            this.timing = (float)this.timing - f2 < 0.0f ? 0 : (int)((float)this.timing - f2);
        }

        if (this.moduleAllowedToAddToHud) {
            RenderUtil.drawRect(gui, this.x, this.y, this.x + this.width, this.y + this.height, -723724);
        } else {
            RenderUtil.drawRect(gui, this.x, this.y, this.x + this.width, this.y + this.height, -1611336460);
        }

        if (this.timing > 0) {
            f2 = (float)this.timing / (float)n3 * (float)100;
            RenderUtil.drawRect(gui, this.x, ((float)this.y + ((float)this.height - (float)this.height * f2 / (float)100)), this.x + this.width, this.y + this.height, this.highlightColor);
        }

        if (this.displayString.contains(".png")) {
            Identifier iconLocation = CheatBreaker.asset("icons/" + this.displayString);
            int color = CheatBreaker.getColor(0.0f, 0.0f, 0.0f, 0.47368422f * 0.9499999f);

            RenderUtil.drawIcon(gui, iconLocation, 8.0f, (float)(this.x + 6), (float)(this.y + 6), color);
        } else {
            f2 = this.font == Fonts.playBold22 ? -1.0f : 0.54545456f * 0.9166667f;
            if (font == Fonts.playBold18) f2 *= 2;
            if (font == Fonts.playRegular14) f2 -= 0.5f;

            float xOffset = font == Fonts.playRegular14 || this.font == Fonts.playBold22 ? -1f : 0f;


            RenderUtil.drawCenteredString(
                    gui,
                    this.font,
                    this.displayString.toUpperCase(),
                    (this.x + (float) this.width / 2) + xOffset,
                    ((float)(this.y + this.height / 2 - this.font.height() / 2) + f2),
                    0x6F000000
            );
        }
    }

    @Override
    public void onClick(int mouseX, int mouseY, int button) {
//        CheatBreaker.LOGGER.info("Clicked on module button: " + this.displayString);
    }

    public void allowAddToHud(boolean allowed) {
        this.moduleAllowedToAddToHud = allowed;
    }
}
