package cc.vops.cheatbreaker.client.ui.element.type.custom;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.element.AbstractModulesGuiElement;
import cc.vops.cheatbreaker.client.ui.element.AbstractScrollableElement;
import cc.vops.cheatbreaker.client.ui.module.CBModulesGui;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public class GlobalSettingsElement extends AbstractModulesGuiElement {
    private final int highlightColor;
    private final AbstractScrollableElement scrollbar;
    private float progressiveRect = 0;
    private final Identifier arrowIcon = CheatBreaker.asset("icons/right.png");

    public GlobalSettingsElement(AbstractScrollableElement scrollbar, int highlightColor, float scale) {
        super(scale);
        this.scrollbar = scrollbar;
        this.highlightColor = highlightColor;
    }

    @Override
    public void handleDrawElement(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTicks) {
        boolean bl = this.isMouseInside(mouseX, mouseY, false);
        int n3 = 75;
        RenderUtil.drawRect(gui, this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, 0x2F2F2F2F);
        float f2 = CBModulesGui.getSmoothFloat(790);
        if (bl) {
            if (this.progressiveRect < n3) {
                this.progressiveRect = ((float)this.progressiveRect + f2);
                if (this.progressiveRect > n3) {
                    this.progressiveRect = n3;
                }
            }
        } else if (this.progressiveRect > 0) {
            this.progressiveRect = (float)this.progressiveRect - f2 < 0.0f ? 0 : ((float)this.progressiveRect - f2);
        }
        if (this.progressiveRect > 0) {
            float f3 = (float)this.progressiveRect / (float)n3 * (float)100;
            RenderUtil.drawRect(gui, this.x, ((float)this.y + ((float)this.height - (float)this.height * f3 / (float)100)), this.x + this.width, this.y + this.height, this.highlightColor);
        }

        RenderUtil.drawIcon(gui, this.arrowIcon, 2.2f * 1.1363636f, (float)(this.x + 6), (float)this.y + (float)6, CheatBreaker.getColor(0, 0, 0, 1.4666667f * 0.23863636f));
        RenderUtil.drawString(
                gui,
                Fonts.playBold18,
                "CheatBreaker Settings".toUpperCase(),
                this.x + 14,
                this.y + 7,
                -818991313
        );
    }

    @Override
    public void onClick(int mouseX, int mouseY, int button) {

    }
}
