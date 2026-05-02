package cc.vops.cheatbreaker.client.ui.element.type;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.ui.element.AbstractModuleTypeElement;
import cc.vops.cheatbreaker.client.ui.module.CBModulesGui;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;

public class ToggleElement extends AbstractModuleTypeElement {
    private Setting setting;
    private Identifier rightArrowIcon = CheatBreaker.asset("icons/left.png");
    private Identifier leftArrowIcon = CheatBreaker.asset("icons/right.png");
    private int optionValueIndex = 0;
    private float animationSpeed = 0.0f;
    private String displayString;

    public ToggleElement(Setting cBSetting, float f) {
        super(f);
        this.setting = cBSetting;
        this.height = 12;
    }

    @Override
    public void drawElement(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTicks) {
        boolean bl = (float) mouseX > (float) (this.x + this.width - 48) * this.scale && (float) mouseX < (float) (this.x + this.width - 10) * this.scale && (float) mouseY > (float) (this.y + this.yOffset) * this.scale && (float) mouseY < (float) (this.y + 10 + this.yOffset) * this.scale;
        boolean bl2 = (float) mouseX > (float) (this.x + this.width - 92) * this.scale && (float) mouseX < (float) (this.x + this.width - 48) * this.scale && (float) mouseY > (float) (this.y + this.yOffset) * this.scale && (float) mouseY < (float) (this.y + 10 + this.yOffset) * this.scale;
//        CheatBreaker.getInstance().ubuntuMedium16px.drawString(this.setting.getLabel().toUpperCase(), this.x + 10, (float) (this.y + 2), bl2 || bl ? -1090519040 : -1895825408);
        RenderUtil.drawString(gui, Fonts.ubuntuMedium16, this.setting.getLabel().toUpperCase(), this.x + 10, (float) (this.y + 4), bl2 || bl ? -1090519040 : -1895825408);
        if (this.optionValueIndex == 0) {
//            CheatBreaker.getInstance().ubuntuMedium16px.drawCenteredString((Boolean) this.setting.getValue() ? "ON" : "OFF", this.x + this.width - 48, this.y + 2, -1895825408);
            RenderUtil.drawCenteredString(gui, Fonts.ubuntuMedium16, (Boolean) this.setting.getValue() ? "ON" : "OFF", this.x + this.width - 48, this.y + 4, -1895825408);
        } else {
            boolean bl3 = this.optionValueIndex == 1;
//            CheatBreaker.getInstance().ubuntuMedium16px.drawCenteredString(this.displayString, (float) (this.x + this.width - 48) - (bl3 ? -this.animationSpeed : this.animationSpeed), this.y + 2, -1895825408);
            RenderUtil.drawCenteredString(gui, Fonts.ubuntuMedium16, this.displayString, ((float) (this.x + this.width - 48) - (bl3 ? -this.animationSpeed : this.animationSpeed)), this.y + 2, -1895825408);
            if (bl3) {
//                CheatBreaker.getInstance().ubuntuMedium16px.drawCenteredString((Boolean) this.setting.getValue() ? "ON" : "OFF", (float) (this.x + this.width - 98) + this.animationSpeed, this.y + 2, -1895825408);
                RenderUtil.drawCenteredString(gui, Fonts.ubuntuMedium16, (Boolean) this.setting.getValue() ? "ON" : "OFF", ((float) (this.x + this.width - 98) + this.animationSpeed), this.y + 2, -1895825408);
            } else {
//                CheatBreaker.getInstance().ubuntuMedium16px.drawCenteredString((Boolean) this.setting.getValue() ? "ON" : "OFF", (float) (this.x + this.width + 2) - this.animationSpeed, this.y + 2, -1895825408);
                RenderUtil.drawCenteredString(gui, Fonts.ubuntuMedium16, (Boolean) this.setting.getValue() ? "ON" : "OFF", ((float) (this.x + this.width + 2) - this.animationSpeed), this.y + 2, -1895825408);
            }
            if (this.animationSpeed >= (float) 50) {
                this.optionValueIndex = 0;
                this.animationSpeed = 0.0f;
            } else {
                float f2 = CBModulesGui.getSmoothFloat((float) 50 + this.animationSpeed * (float) 15);
                this.animationSpeed = this.animationSpeed + f2 >= (float) 50 ? (float) 50 : (this.animationSpeed += f2);
            }
//            RenderUtil.drawRect(gfx, this.x + this.width - 130, this.y + 2, this.x + this.width - 72, this.y + 12, -723724);
//            RenderUtil.drawRect(gfx, this.x + this.width - 22, this.y + 2, this.x + this.width + 4, this.y + 12, -723724);
            RenderUtil.drawRect(gui, this.x + this.width - 130, this.y + 2, this.x + this.width - 72, this.y + 12, -723724);
            RenderUtil.drawRect(gui, this.x + this.width - 22, this.y + 2, this.x + this.width + 4, this.y + 12, -723724);
        }
//        GL11.glColor4f(0.0f, 0.0f, 0.0f, bl2 ? 0.74000007f * 1.081081f : 0.288f * 1.5625f);
//        RenderUtil.drawIcon(this.rightArrowIcon, (float) 4, (float) (this.x + this.width - 82), (float) (this.y + 3));
//        GL11.glColor4f(0.0f, 0.0f, 0.0f, bl ? 0.4244898f * 1.8846154f : 0.64285713f * 0.7f);
//        RenderUtil.drawIcon(this.leftArrowIcon, (float) 4, (float) (this.x + this.width - 22), (float) (this.y + 3));

        RenderUtil.drawIcon(gui, this.rightArrowIcon, (float) 4, (float) (this.x + this.width - 82), (float) (this.y + 3), CheatBreaker.getColor(0, 0, 0, bl2 ? 0.74000007f * 1.081081f : 0.288f * 1.5625f));
        RenderUtil.drawIcon(gui, this.leftArrowIcon, (float) 4, (float) (this.x + this.width - 22), (float) (this.y + 3), CheatBreaker.getColor(0, 0, 0, bl ? 0.4244898f * 1.8846154f : 0.64285713f * 0.7f));
    }

    @Override
    public void handleElementClick(int mouseX, int mouseY, int button) {
        boolean bl;
        boolean bl2 = (float) mouseX > (float) (this.x + this.width - 48) * this.scale && (float) mouseX < (float) (this.x + this.width - 10) * this.scale && (float) mouseY > (float) (this.y + this.yOffset) * this.scale && (float) mouseY < (float) (this.y + 10 + this.yOffset) * this.scale;
        bl = (float) mouseX > (float) (this.x + this.width - 92) * this.scale && (float) mouseX < (float) (this.x + this.width - 48) * this.scale && (float) mouseY > (float) (this.y + this.yOffset) * this.scale && (float) mouseY < (float) (this.y + 10 + this.yOffset) * this.scale;
        if ((bl || bl2) && this.optionValueIndex == 0) {
            this.optionValueIndex = bl ? 1 : 2;
            this.animationSpeed = 0.0f;
            this.displayString = (Boolean) this.setting.getValue() ? "ON" : "OFF";
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            this.setting.setValue(!((Boolean) this.setting.getValue()));
            if (this.setting == CheatBreaker.getInstance().getModuleManager().keyStrokes.replaceNamesWithArrows) {
                CheatBreaker.getInstance().getModuleManager().keyStrokes.initialize();
            } else if (this.setting == CheatBreaker.getInstance().getGlobalSettings().enableTeamView && !(Boolean) CheatBreaker.getInstance().getGlobalSettings().enableTeamView.getValue()) {
                CheatBreaker.getInstance().getGlobalSettings().enableTeamView.setValue(false);
            }
        }
    }
}