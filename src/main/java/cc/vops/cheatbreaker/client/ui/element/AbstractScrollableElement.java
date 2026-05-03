package cc.vops.cheatbreaker.client.ui.element;


import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.ui.element.module.ModulePreviewElement;
import cc.vops.cheatbreaker.client.util.Mouse;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class AbstractScrollableElement extends AbstractModulesGuiElement {
    protected double startPosition = 0.0;
    public int scrollAmount = 0;
    public int scrollHeight = 0;
    public int x2;
    protected int y2;
    public boolean bottom = false;
    public boolean hovering = false;
    public int anchorY = -1;
    private float scrollPosition;

    public AbstractScrollableElement(float scale, int x, int y, int width, int height) {
        super(scale);
        this.x2 = x;
        this.y2 = y;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void onClick(int mouseX, int mouseY, int button) {
    }

    @Override
    public void onScroll(GuiGraphicsExtractor gui, int delta) {
        if (delta != 0 && this.scrollHeight >= this.height) {
            this.startPosition += (double) delta * 25.0;
        }
    }

    public void preDraw(GuiGraphicsExtractor gui, int n, int n2) {
        if (this.isMouseInsideIgnoreOffset(n, n2, false)) {
            double d = Math.round(this.startPosition / (double) 25);
            this.startPosition -= d;
            if (this.startPosition != 0.0) {
                this.scrollAmount = (int) ((double) this.scrollAmount + d);
            }
        } else {
            this.startPosition = 0.0;
        }
        if (this.bottom) {
            if (this.scrollAmount < -this.scrollHeight + this.height) {
                this.scrollAmount = -this.scrollHeight + this.height;
                this.startPosition = 0.0;
            }
            if (this.scrollAmount > 0) {
                this.scrollAmount = 0;
                this.startPosition = 0.0;
            }
        }

        gui.pose().pushMatrix();
        gui.pose().translate(0.0f, (float) this.scrollAmount);
    }

    public void postDraw(GuiGraphicsExtractor graphics, int mx, int my) {
        float mouseX = mx * CheatBreaker.getScaleFactor();
        float mouseY = my * CheatBreaker.getScaleFactor();

        this.bottom = true;
        boolean bl = this.scrollHeight > this.height;
        graphics.pose().popMatrix();
        if (!(!this.hovering || Mouse.isButtonDown(0))) {
            this.hovering = false;
        }
        if (this.hovering && !Mouse.isButtonDown(0)) {
            this.hovering = false;
        }
        double d = this.height - 10;
        double d2 = this.scrollHeight;
        double d3 = d / d2 * (double)100;
        double d4 = d / (double)100 * d3;
        double d5 = (double)this.scrollAmount / 100.0 * d3;
        if (bl) {
            int n3 = this.height;
            boolean bl4 = (float)mouseX > (float)(this.x + this.width - 9) * this.scale && (float)mouseX < (float)(this.x + this.width - 3) * this.scale && (double)mouseY > ((double)(this.y + 11) - d5) * (double)this.scale && (double)mouseY < ((double)(this.y + 8) + d4 - d5) * (double)this.scale;
            boolean bl3 = (float)mouseX > (float)(this.x + this.width - 9) * this.scale && (float)mouseX < (float)(this.x + this.width - 3) * this.scale && (float)mouseY > (float)(this.y + 11) * this.scale && (double)mouseY < ((double)(this.y + 6) + d - (double)3) * (double)this.scale;
            if (Mouse.isButtonDown(0) && !this.hovering && bl3) {
                this.hovering = true;
                this.scrollPosition = my;
            }

            if (this.hovering && Mouse.isButtonDown(0)) {
                this.scrollAmount = (int) (this.scrollAmount - ((int)(my - this.scrollPosition) * ((double) this.scrollHeight / d)));
                this.scrollPosition = my;
            } else if (this.hovering) {
                this.hovering = false;
                this.scrollPosition = 0;
            }
            if (this.scrollAmount < -this.scrollHeight + n3) {
                this.scrollAmount = -this.scrollHeight + n3;
                this.startPosition = 0.0;
            }
            if (this.scrollAmount > 0) {
                this.scrollAmount = 0;
                this.startPosition = 0.0;
            }
            RenderUtil.drawRoundedRect(graphics, this.x + this.width - 6, this.y + 11, this.x + this.width - 4, (double)(this.y + 6) + d - (double)3, 2, bl3 && !bl4 ? 0x6F000000 : 0x3F000000);
            RenderUtil.drawRoundedRect(graphics, this.x + this.width - 7, (double)(this.y + 11) - d5, this.x + this.width - 3, (double)(this.y + 8) + d4 - d5, 4, bl4 || this.hovering ? 0xFF0240FF : -12418828);
        }
        if (!bl && this.scrollAmount != 0) {
            this.scrollAmount = 0;
        }
    }

    public abstract boolean hasSettings(AbstractModule module);
    public abstract void handleModuleMouseClick(AbstractModule module);
}

