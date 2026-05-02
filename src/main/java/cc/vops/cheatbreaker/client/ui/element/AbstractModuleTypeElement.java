package cc.vops.cheatbreaker.client.ui.element;

import cc.vops.cheatbreaker.client.ui.module.CBModulesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class AbstractModuleTypeElement extends AbstractModulesGuiElement {

    public AbstractModuleTypeElement(float scaleFactor) {
        super(scaleFactor);
    }

    public abstract void drawElement(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTicks);
    public abstract void handleElementClick(int mouseX, int mouseY, int button);

    @Override
    public void handleDrawElement(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTicks) {
        int mouseXAdjusted = (int) (mouseX * this.scale);
        int mouseYAdjusted = (int) (mouseY * this.scale);

        this.drawElement(gui, mouseXAdjusted, mouseYAdjusted, partialTicks);
    }


    @Override
    public void onClick(int mouseX, int mouseY, int button) {
        int mouseXAdjusted = (int) (mouseX * this.scale);
        int mouseYAdjusted = (int) ((mouseY * this.scale));

        this.handleElementClick(mouseXAdjusted, mouseYAdjusted, button);
    }
}
