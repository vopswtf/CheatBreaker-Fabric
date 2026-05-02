package cc.vops.cheatbreaker.client.ui.element.module;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.ui.element.AbstractModulesGuiElement;
import cc.vops.cheatbreaker.client.ui.element.AbstractScrollableElement;
import cc.vops.cheatbreaker.client.ui.module.CBModulesGui;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public class ModuleSettingsElement extends AbstractModulesGuiElement {
    private final int highlightColor;
    public final AbstractModule module;
    public final AbstractScrollableElement scrollbar;
    private float progressiveRect = 0;
    private final Identifier arrowIcon = CheatBreaker.asset("icons/right.png");

    public ModuleSettingsElement(AbstractScrollableElement scrollbar, int highlightColor, AbstractModule module, float scale) {
        super(scale);
        this.scrollbar = scrollbar;
        this.highlightColor = highlightColor;
        this.module = module;
    }

    @Override
    public void handleDrawElement(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTicks) {
        boolean bl = this.isMouseInside(mouseX, mouseY, false);
        int n3 = 75;
        RenderUtil.drawRect(gui, this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, 0x2F2F2F2F);

        if (this.scrollbar.hasSettings(this.module)) {
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
        }

        RenderUtil.drawIcon(gui, this.arrowIcon, 1.9411765f * 1.2878788f, (float)(this.x + 6), (float)this.y + (float)6, CheatBreaker.getColor(0, 0, 0, 0.35f));

        int color = this.scrollbar.hasSettings(this.module) ? -818991313 : 0x2F2F2F2F;
        RenderUtil.drawString(
                gui,
                Fonts.playBold18,
                this.module.getName().toUpperCase(),
                this.x + 14,
                this.y + 7,
                color
        );
    }

    @Override
    public void onClick(int mouseX, int mouseY, int button) {
        double d = this.height - 10;
        double d3 = d / this.scrollbar.scrollHeight * (double)100;
        double d4 = d / (double)100 * d3;
        double d5 = (double)this.scrollbar.scrollAmount / 100.0 * d3;
        boolean bl4 = (float)mouseX > (float)(this.x + this.width - 9) * this.scale && (float)mouseX < (float)(this.x + this.width - 3) * this.scale && (double)mouseY > ((double)(this.y + 11) - d5) * (double)this.scale && (double)mouseY < ((double)(this.y + 8) + d4 - d5) * (double)this.scale;
        boolean bl3 = (float)mouseX > (float)(this.x + this.width - 9) * this.scale && (float)mouseX < (float)(this.x + this.width - 3) * this.scale && (float)mouseY > (float)(this.y + 11) * this.scale && (double)mouseY < ((double)(this.y + 6) + d - (double)3) * (double)this.scale;
        if (button == 0 && bl3 || bl4) {
            this.scrollbar.hovering = true;
        }
        this.scrollbar.handleModuleMouseClick(this.module);

    }
}
