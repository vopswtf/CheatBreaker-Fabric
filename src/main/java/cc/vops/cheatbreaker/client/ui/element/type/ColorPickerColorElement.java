package cc.vops.cheatbreaker.client.ui.element.type;

import cc.vops.cheatbreaker.client.ui.element.AbstractModuleTypeElement;
import cc.vops.cheatbreaker.client.ui.element.AbstractModulesGuiElement;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class ColorPickerColorElement extends AbstractModuleTypeElement {
    public int color;

    public ColorPickerColorElement(float f, int n) {
        super(f);
        this.color = n;
    }

    @Override
    public void drawElement(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(gui, this.x, this.y, this.x + this.width, this.y + this.height, -1358954496);
        RenderUtil.drawRect(gui, this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, this.color | 0xFF000000);
    }

    @Override
    public void handleElementClick(int mouseX, int mouseY, int button) {

    }
}
