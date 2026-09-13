package cc.vops.cheatbreaker.client.ui.overlay.element;

import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.ui.fading.AbstractFade;
import cc.vops.cheatbreaker.client.ui.fading.MinMaxFade;
import cc.vops.cheatbreaker.client.ui.mainmenu.AbstractElement;
import cc.vops.cheatbreaker.client.util.Mouse;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.lwjgl.glfw.GLFW;

public class HorizontalSliderElement extends AbstractElement {
    @Getter
    private final Setting setting;
    private final AbstractFade fadeTime;
    private Number value;

    public HorizontalSliderElement(Setting cBSetting) {
        this.setting = cBSetting;
        this.fadeTime = new MinMaxFade(200L);
        this.value = (Number)cBSetting.getValue();
    }

    @Override
    protected void handleElementDraw(GuiGraphicsExtractor gfx, float f, float f2, boolean bl) {
        RenderUtil.drawRect(gfx, this.x, this.y, this.x + this.width, this.y + this.height, -13158601);
        if (!this.fadeTime.hasStartTime()) {
            this.value = (Number)this.setting.getValue();
        }
        float f3 = ((Number)this.setting.getValue()).floatValue();
        float f4 = this.setting.getMinimumValue().floatValue();
        float f5 = this.setting.getMaximumValue().floatValue();
        float f6 = f3 - this.value.floatValue();
        float f7 = (float)100 * ((this.value.floatValue() + f6 * this.fadeTime.getCurrentValue() - f4) / (f5 - f4));
        RenderUtil.drawRect(gfx, this.x, this.y, this.x + this.width / (float)100 * f7, this.y + this.height, -52429);
    }

    @Override
    public boolean handleElementMouseClicked(float f, float f2, int n, boolean bl) {
        if (!bl) {
            return false;
        }
        if (Mouse.isButtonDown(0) && this.isMouseInside(f, f2)) {
            this.fadeTime.reset();
            this.value = (Number)this.setting.getValue();
            float f3 = ((Number)this.setting.getMinimumValue()).floatValue();
            float f4 = ((Number)this.setting.getMaximumValue()).floatValue();
            if (f - this.x > this.width / 2.0f) {
                f += 2.0f;
            }
            float f5 = f3 + (f - this.x) * ((f4 - f3) / this.width);
            switch (this.setting.getType()) {
                case INTEGER: {
                    this.setting.setValue(this.setValue((Object)Integer.parseInt((int)f5 + "")));
                    break;
                }
                case FLOAT: {
                    this.setting.setValue(this.setValue(f5));
                    break;
                }
                case DOUBLE: {
                    this.setting.setValue(this.setValue(Double.parseDouble((double)f5 + "")));
                }
            }
        }
        return super.handleElementMouseClicked(f, f2, n, bl);
    }

    private Object setValue(Object object) {
        try {
            return object;
        }
        catch (ClassCastException classCastException) {
            return null;
        }
    }

}
