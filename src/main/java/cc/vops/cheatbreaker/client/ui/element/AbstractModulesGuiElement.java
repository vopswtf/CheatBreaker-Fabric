package cc.vops.cheatbreaker.client.ui.element;

import cc.vops.cheatbreaker.client.ui.module.CBModulesGui;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

@Getter
public abstract class AbstractModulesGuiElement {
    public float scale;
    public int yOffset = 0;
    public int x;
    protected int y;
    protected int width;
    protected int height;

    public AbstractModulesGuiElement(float scaleFactor) {
        this.scale = scaleFactor;
    }

    public void setDimensions(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void handleDrawElement(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTicks);
    public abstract void onClick(int mouseX, int mouseY, int button);

    protected boolean isMouseInsideIgnoreOffset(double mouseX, double mouseY, boolean click) {
        if (!(Minecraft.getInstance().screen instanceof CBModulesGui)) return false;
        return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height;
    }

    public boolean isMouseInside(int mouseX, int mouseY, boolean click) {
        return isMouseInside(mouseX, (double) mouseY, click);
    }

    public boolean isMouseInside(double mouseX, double mouseY, boolean click) {
        if (!(Minecraft.getInstance().screen instanceof CBModulesGui)) return false;
//        return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height;
        // use yOffset for mouse y position to prevent issues with scrolling
        return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y + this.yOffset && mouseY <= this.y + this.height + this.yOffset;
    }

    public void onScroll(GuiGraphicsExtractor gui, int n) {
    }
}