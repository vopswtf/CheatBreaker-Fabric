package cc.vops.cheatbreaker.client.ui.element.type;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.ui.element.AbstractModuleTypeElement;
import cc.vops.cheatbreaker.client.util.Keyboard;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.Mouse;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;

public class SliderElement extends AbstractModuleTypeElement {
    private Setting setting;
    private float settingValue = -1;
    private float value;
    private boolean interacting = false;
    private long lastKeyboardUpdate = 0;

    public SliderElement(Setting setting, float scale) {
        super(scale);
        this.setting = setting;
        this.height = 14;
        this.settingValue = Float.parseFloat("" + setting.getValue());
    }

    @Override
    public void handleElementClick(int mouseX, int mouseY, int button) {
        int offset = 170;
        int width = 170;
        boolean bl2 = (float) mouseX > (float)(this.x + offset) * this.scale && (float) mouseX < (float)(this.x + offset + width - 2) * this.scale && (float) mouseY > (float)(this.y + 4 + this.yOffset) * this.scale && (float) mouseY < (float)(this.y + 10 + this.yOffset) * this.scale;
        if (button == 0 && bl2) {
            this.interacting = true;
        }
    }

    @Override
    public void drawElement(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTicks) {
        float f2;
        float f3;
        int n3 = 150;
//        CheatBreaker.getInstance().ubuntuMedium16px.drawString(this.setting.getLabel().toUpperCase(), this.x + 10, (float)(this.y + 2), -1895825408);
        RenderUtil.drawString(gui, Fonts.ubuntuMedium16, this.setting.getLabel().toUpperCase(), this.x + 10, this.y + 4, -1895825408);
        if (this.interacting && !Mouse.isButtonDown(0)) {
            this.interacting = false;
        }
        String string = this.setting.getValue() + "";
//        CheatBreaker.getInstance().ubuntuMedium16px.drawCenteredString(string, this.x + 154, (float)(this.y + 2), -1895825408);
        RenderUtil.drawCenteredString(gui, Fonts.ubuntuMedium16, string, this.x + 82 + n3 / 2, (this.y + 4), -1895825408);
        boolean bl = (float) mouseX > (float)(this.x + 172) * this.scale && (float) mouseX < (float)(this.x + 172 + n3 - 2) * this.scale && (float) mouseY > (float)(this.y + 4 + this.yOffset) * this.scale && (float) mouseY < (float)(this.y + 10 + this.yOffset) * this.scale;

        float minVal = Float.parseFloat("" + this.setting.getMinimumValue());
        float maxVal = Float.parseFloat("" + this.setting.getMaximumValue());

        KeyEvent keyEvent = Keyboard.peekKeyEvent();
        if (bl && !Mouse.isButtonDown(0) && keyEvent != null && System.currentTimeMillis() - this.lastKeyboardUpdate > 150) {
            if (keyEvent.key() != GLFW.GLFW_KEY_LEFT && keyEvent.key() != GLFW.GLFW_KEY_RIGHT) return;

            this.value = Float.parseFloat("" + this.setting.getValue());

            if (keyEvent.key() == GLFW.GLFW_KEY_LEFT) {
                this.value = this.value - this.setting.getDelta();
            } else {
                this.value = this.value + this.setting.getDelta();
            }

            if (this.setting.getType().equals(Setting.Type.INTEGER)) {
                this.value = Math.round(this.value);
            } else {
                this.value = (float)Math.round(this.value * 100) / 100;
            }

            this.value = Math.clamp(this.value, minVal, maxVal);

            onUpdate();
            lastKeyboardUpdate = System.currentTimeMillis();
        }

        RenderUtil.drawRoundedRect(gui, (double) (this.x + 174), (double) (this.y + 6), (double) (this.x + 170 + n3 - 4), (double) (this.y + 8), 1.0, bl ? -1895825408 : 0x6F000000);
        double d = n3 - 18;
        if (this.interacting) {
            this.value = (float)Math.round(((double)minVal + (double)((float) mouseX - (float)(this.x + 180) * this.scale) * ((double)(maxVal - minVal) / (d * (double)this.scale))) * (double)100) / (float)100;
            if (this.setting.getType().equals(Setting.Type.INTEGER)) {
                this.value = Math.round(this.value);
            }

            this.value = Math.clamp(this.value, minVal, maxVal);

            onUpdate();
        }
        f3 = (f3 = Float.parseFloat(this.setting.getValue() + "")) < this.settingValue ? this.settingValue - f3 : (f3 -= this.settingValue);
        float f6 = ((maxVal - minVal) / (float)20 + f3 * (float)8) / (float)(Minecraft.getInstance().getFps() + 1);
        if ((double)f6 < 43.5 * 2.2988505747126437E-6) {
            f6 = 1.9523809f * 5.121951E-5f;
        }
        if (this.settingValue < (f2 = Float.parseFloat(this.setting.getValue() + ""))) {
            this.settingValue = this.settingValue + f6 <= f2 ? (this.settingValue += f6) : f2;
        } else if (this.settingValue > f2) {
            this.settingValue = this.settingValue - f6 >= f2 ? (this.settingValue -= f6) : f2;
        }
        double d2 = (float)100 * ((this.settingValue - minVal) / (maxVal - minVal));
        RenderUtil.drawRoundedRect(gui, (double)(this.x + 174), (double)(this.y + 6), (double)(this.x + 180) + d * d2 / (double)100, (double)(this.y + 8), (double)4, -12418828);
        RenderUtil.drawCircle(gui, (double)((float)this.x + 543.75f * 0.33333334f) + d * d2 / (double)100, (float)this.y + 0.6666667f * 10.875f, 2.531249981140718 * 1.7777777910232544, CheatBreaker.getColor(0.5714286f * 0.4375f, 0.45849055f * 0.9814815f, 1.0f, 1.0f));
        RenderUtil.drawCircle(gui, (double)((float)this.x + 0.8804348f * 205.8642f) + d * d2 / (double)100, (float)this.y + 0.13043478f * 55.583332f, 2.639325754971479 * 1.0229885578155518, CheatBreaker.getColor(1.0f, 1.0f, 1.0f, 1.0f));

        if (this.settingValue != Float.parseFloat(this.setting.getValue() + "")) {
            CheatBreaker.getInstance().getModuleManager().keyStrokes.initialize();
        }
    }

    void onUpdate() {
        switch (this.setting.getType()) {
            case INTEGER: {
                this.setting.setValue(Integer.parseInt((int)this.value + ""));
                break;
            }
            case FLOAT: {
                this.setting.setValue(this.value);
                break;
            }
            case DOUBLE: {
                this.setting.setValue(Double.parseDouble(this.value + ""));
            }
        }
        CheatBreaker.getInstance().getModuleManager().keyStrokes.initialize();
    }
}