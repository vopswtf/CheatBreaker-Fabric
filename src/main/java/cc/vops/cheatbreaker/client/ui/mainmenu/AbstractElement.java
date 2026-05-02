package cc.vops.cheatbreaker.client.ui.mainmenu;


import cc.vops.cheatbreaker.CheatBreaker;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class AbstractElement {
    @Getter
    @Setter
    protected float x;
    @Getter
    @Setter
    protected float y;
    @Getter
    @Setter
    protected float width;
    @Getter
    @Setter
    protected float height;
    protected final Minecraft mc = Minecraft.getInstance();
    protected final CheatBreaker client = CheatBreaker.getInstance();

    public boolean isMouseInside(float mouseX, float mouseY) {
        boolean minX = mouseX > this.x;
        boolean maxX = mouseX < this.x + this.width;
        boolean minY = mouseY > this.y;
        boolean maxY = mouseY < this.y + this.height;
        return minX && maxX && minY && maxY;
    }

    public boolean isMouseInside(double mouseX, double mouseY) {
        boolean minX = mouseX > this.x;
        boolean maxX = mouseX < this.x + this.width;
        boolean minY = mouseY > this.y;
        boolean maxY = mouseY < this.y + this.height;
        return minX && maxX && minY && maxY;
    }

    public boolean drawElement(GuiGraphicsExtractor graphics, float f, float f2, boolean bl) {
        this.handleElementDraw(graphics, f, f2, bl);
        return this.isMouseInside(f, f2);
    }

    public void setElementSize(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void handleElementUpdate() {
    }

    public void handleElementClose() {
    }

    public void handleElementKeyTyped(int keyCode, int scanCode, int modifiers) {
    }

    public void handleElementMouse() {
    }

    public void handleScroll(GuiGraphicsExtractor gfx, int delta) {
    }

    public void handleCharInput(char typedChar, int keyCode) {
    }

    protected abstract void handleElementDraw(GuiGraphicsExtractor gfx, float var1, float var2, boolean var3);

    public boolean handleElementMouseClicked(float f, float f2, int n, boolean bl) {
        return false;
    }

    public boolean handleElementMouseRelease(float f, float f2, int n, boolean bl) {
        return false;
    }

    public boolean handleMouseClickedInternal(float f, float f2, int n) {
        return false;
    }

}