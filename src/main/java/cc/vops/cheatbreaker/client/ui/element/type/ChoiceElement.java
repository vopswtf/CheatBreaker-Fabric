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

import java.util.Arrays;

public class ChoiceElement extends AbstractModuleTypeElement {
    private final Setting setting;
    private final Identifier leftIcon = CheatBreaker.asset("icons/left.png");
    private final Identifier rightIcon = CheatBreaker.asset("icons/right.png");
    private int optionValueIndex = 0;
    private float animationSpeed = 0.0f;
    private String optionValue;

    public ChoiceElement(Setting cBSetting, float f) {
        super(f);
        this.setting = cBSetting;
        this.height = 12;

        // reset broken values
        if (Arrays.stream(this.setting.getAcceptedValues()).noneMatch(acceptedValue -> acceptedValue.equalsIgnoreCase(this.setting.getValue().toString()))) {
            this.setting.setValue(this.setting.getAcceptedValues()[0]);
        }
    }

    private int getLongOffset() {
        return 0;
//        int maxWidth = 0;
//        for (Object acceptedValue : this.setting.getAcceptedValues()) {
//            int valueWidth = Fonts.ubuntuMedium16.width(acceptedValue.toString());
//            if (valueWidth > maxWidth) {
//                maxWidth = valueWidth;
//            }
//        }
//        return maxWidth > 80 ? maxWidth : 0;
    }

    @Override
    public void drawElement(GuiGraphicsExtractor gfx, int mouseX, int mouseY, float partialTicks) {
        int longOffset = this.getLongOffset();

        boolean leftHovered = (float) mouseX > (float)((this.x - (longOffset)) + this.width - 92) * this.scale && (float) mouseX < (float)(this.x - (longOffset) + this.width - 48) * this.scale && (float) mouseY > (float)(this.y + this.yOffset) * this.scale && (float) mouseY < (float)(this.y + 14 + this.yOffset) * this.scale;
        boolean rightHovered = (float) mouseX > (float)(this.x + this.width - 48) * this.scale && (float) mouseX < (float)(this.x + this.width - 10) * this.scale && (float) mouseY > (float)(this.y + this.yOffset) * this.scale && (float) mouseY < (float)(this.y + 14 + this.yOffset) * this.scale;
//        CheatBreaker.getInstance().ubuntuMedium16px.drawString(this.setting.getLabel().toUpperCase(), this.x + 10, (float)(this.y + 4), leftHovered || rightHovered ? -1090519040 : -1895825408);
        RenderUtil.drawString(
                gfx,
                Fonts.ubuntuMedium16,
                this.setting.getLabel().toUpperCase(),
                this.x + 10,
                (float)(this.y + 4),
                leftHovered || rightHovered ? -1090519040 : -1895825408
        );
        boolean bl3 = this.setting.getLabel().toLowerCase().endsWith("color");
        String value = this.setting.getValue().toString();
        String[] split;
        if (!bl3) {
            if (this.optionValueIndex == 0) {
                RenderUtil.drawCenteredString(gfx, Fonts.ubuntuMedium16, value, this.x + this.width - 48 + longOffset, this.y + 4, -1895825408);
            } else {
                boolean bl4 = this.optionValueIndex == 1;
                RenderUtil.drawCenteredString(gfx, Fonts.ubuntuMedium16, value, ((this.x + this.width - 48) - (bl4 ? -this.animationSpeed : this.animationSpeed) + longOffset), this.y + 4, -1895825408);
                if (bl4) {
                    RenderUtil.drawCenteredString(gfx, Fonts.ubuntuMedium16, value, ((this.x + this.width - 98) + this.animationSpeed + longOffset), this.y + 4, -1895825408);
                } else {
                    RenderUtil.drawCenteredString(gfx, Fonts.ubuntuMedium16, value, ((this.x + this.width + 2) - this.animationSpeed + longOffset), this.y + 4, -1895825408);
                }
                if (this.animationSpeed >= (float)50) {
                    this.optionValueIndex = 0;
                    this.animationSpeed = 0.0f;
                } else {
                    float f2 = CBModulesGui.getSmoothFloat((float)50 + this.animationSpeed * (float)15);
                    this.animationSpeed = this.animationSpeed + f2 >= (float)50 ? (float)50 : (this.animationSpeed += f2);
                }
                RenderUtil.drawRect(gfx, this.x + this.width - 130, this.y + 2, this.x + this.width - 72, this.y + 12, -723724);
                RenderUtil.drawRect(gfx, this.x + this.width - 22, this.y + 2, this.x + this.width + 4, this.y + 12, -723724);
            }
        } else if (this.optionValueIndex == 0) {
            float f3 = Fonts.ubuntuMedium16.width(value);
            RenderUtil.drawCenteredString(gfx, Fonts.ubuntuMedium16, value, ((this.x + this.width) - 44.738373f * 1.0617284f - f3 / 2.0f) + longOffset, this.y + 4, -16777216);
            RenderUtil.drawCenteredString(gfx, Fonts.ubuntuMedium16, "§" + value + value, ((this.x + this.width - 48) - f3 / 2.0f) + longOffset, this.y + 4, -16777216);
        } else {
            boolean bl5 = this.optionValueIndex == 1;
            RenderUtil.drawCenteredString(gfx, Fonts.ubuntuMedium16, this.optionValue, ((this.x + this.width - 48) - (bl5 ? -this.animationSpeed : this.animationSpeed) + longOffset), this.y + 4, -1895825408);
            float f4 = Fonts.ubuntuMedium16.width(value);
            if (bl5) {
                RenderUtil.drawCenteredString(gfx, Fonts.ubuntuMedium16, value, ((this.x + this.width) - 110.21739f * 0.88461536f - f4 / 2.0f + this.animationSpeed + longOffset), this.y + 4, -16777216);
                RenderUtil.drawCenteredString(gfx, Fonts.ubuntuMedium16, "§" + value + value, ((this.x + this.width - 98) - f4 / 2.0f + this.animationSpeed + longOffset), this.y + 4, -16777216);
            } else {
                RenderUtil.drawCenteredString(gfx, Fonts.ubuntuMedium16, value, ((this.x + this.width) - 2.6296296f * 0.57042253f - f4 / 2.0f - this.animationSpeed + longOffset), this.y + 4, -16777216);
                RenderUtil.drawCenteredString(gfx, Fonts.ubuntuMedium16, "§" + value + value, ((this.x + this.width - 2) - f4 / 2.0f - this.animationSpeed + longOffset), this.y + 4, -16777216);
            }
            if (this.animationSpeed >= (float)50) {
                this.optionValueIndex = 0;
                this.animationSpeed = 0.0f;
            } else {
                float f5 = CBModulesGui.getSmoothFloat((float)50 + this.animationSpeed * (float)15);
                this.animationSpeed = this.animationSpeed + f5 >= (float)50 ? (float)50 : (this.animationSpeed += f5);
            }
            RenderUtil.drawRect(gfx, this.x + this.width - 130, this.y + 2, this.x + this.width - 72, this.y + 12, -723724);
            RenderUtil.drawRect(gfx, this.x + this.width - 22, this.y + 2, this.x + this.width + 4, this.y + 12, -723724);
        }

        RenderUtil.drawIcon(gfx, this.leftIcon, (float)4, (float)(this.x + this.width - 82 + longOffset), (float)(this.y + 4), CheatBreaker.getColor(0, 0, 0, leftHovered ? 0.6857143f * 1.1666666f : 0.5416667f * 0.8307692f));
        RenderUtil.drawIcon(gfx, this.rightIcon, (float)4, (float)(this.x + this.width - 22), (float)(this.y + 4), CheatBreaker.getColor(0, 0, 0, rightHovered ? 0.82580644f * 0.96875f : 3.3793104f * 0.13316326f));
    }

    @Override
    public void handleElementClick(int mouseX, int mouseY, int button) {
        int longOffset = this.getLongOffset();

        boolean leftHovered = (float) mouseX > (float) ((this.x - longOffset)+ this.width - 92) * this.scale && (float) mouseX < (float) (this.x - longOffset + this.width - 48) * this.scale && (float) mouseY > (float) (this.y + this.yOffset) * this.scale && (float) mouseY < (float) (this.y + 14 + this.yOffset) * this.scale;
        boolean rightHovered = (float) mouseX > (float)(this.x + this.width - 48) * this.scale && (float) mouseX < (float)(this.x + this.width - 10) * this.scale && (float) mouseY > (float)(this.y + this.yOffset) * this.scale && (float) mouseY < (float)(this.y + 14 + this.yOffset) * this.scale;
//        System.out.println("leftHovered: " + leftHovered + ", rightHovered: " + rightHovered);

        if ((leftHovered || rightHovered) && this.optionValueIndex == 0) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            for (int i = 0; i < this.setting.getAcceptedValues().length; ++i) {
                if (!((String[])this.setting.getAcceptedValues())[i].toLowerCase().equalsIgnoreCase((String)this.setting.getValue())) continue;
                this.optionValue = (String)this.setting.getValue();
                if (rightHovered) {
                    if (i + 1 >= this.setting.getAcceptedValues().length) {
                        this.optionValueIndex = 2;
                        this.setting.setValue(((String[])this.setting.getAcceptedValues())[0]);
                        break;
                    }
                    this.optionValueIndex = 2;
                    this.setting.setValue(((String[])this.setting.getAcceptedValues())[i + 1]);
                    break;
                }
                if (i - 1 < 0) {
                    this.optionValueIndex = 1;
                    this.setting.setValue(((String[])this.setting.getAcceptedValues())[this.setting.getAcceptedValues().length - 1]);
                    break;
                }
                this.optionValueIndex = 1;
                this.setting.setValue(((String[])this.setting.getAcceptedValues())[i - 1]);
                break;
            }

            if (this.setting == CheatBreaker.getInstance().getGlobalSettings().clearGlass) {
                // TODO: Clear glass refresh
            }
        }
    }
}