package cc.vops.cheatbreaker.client.ui.module;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.ui.AbstractGui;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.Mouse;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.sounds.SoundEvents;

public class CBModulePlaceGui extends AbstractGui {
    private final AbstractModule module;
    private final CBModulesGui eventButton;

    public CBModulePlaceGui(CBModulesGui cBModulesGui, AbstractModule abstractModule) {
        abstractModule.setState(true);
        this.module = abstractModule;
        this.eventButton = cBModulesGui;
    }

    @Override
    protected void initMenu() {

    }

    @Override
    protected void drawMenu(GuiGraphicsExtractor gfx, float mouseX, float mouseY, float delta) {
        float f2 = 1.0f / CheatBreaker.getScaleFactor();

        gfx.pose().pushMatrix();
        gfx.pose().scale(f2);

        RenderUtil.drawRoundedRect(gfx, 0.0, this.height / 3, this.width, (float)(this.height / 3) + 2.1086957f * 0.23711339f, 0.0, 0x6F000000);
        RenderUtil.drawRoundedRect(gfx, 0.0, this.height / 3 * 2, this.width, (float)(this.height / 3 * 2) + 1.1388888f * 0.43902442f, 0.0, 0x6F000000);
        RenderUtil.drawRoundedRect(gfx, this.width / 3, 0.0, (float)(this.width / 3) + 0.42073172f * 1.1884058f, this.height, 0.0, 0x6F000000);
        RenderUtil.drawRoundedRect(gfx, this.width / 3 * 2, 0.0, (float)(this.width / 3 * 2) + 0.28070176f * 1.78125f, this.height, 0.0, 0x6F000000);
        RenderUtil.drawRoundedRect(gfx, this.width / 3 + this.width / 6, this.height / 3 * 2, (float)(this.width / 3 + this.width / 6) + 6.7000003f * 0.07462686f, this.height, 0.0, 0x6F000000);

        gfx.pose().popMatrix();

        float scaledWidth = CheatBreaker.getScaledWidth() / CheatBreaker.getScaleFactor();
        float scaledHeight = CheatBreaker.getScaledHeight() / CheatBreaker.getScaleFactor();

        float[] arrf = CBAnchorHelper.getPositions(mouseX, mouseY, this);

        gfx.pose().pushMatrix();
        GuiAnchor hoveredAnchor = CBAnchorHelper.getAnchor(mouseX, mouseY, this);
        if (hoveredAnchor != GuiAnchor.MIDDLE_MIDDLE) {
            if (hoveredAnchor == GuiAnchor.MIDDLE_BOTTOM_LEFT || hoveredAnchor == GuiAnchor.MIDDLE_BOTTOM_RIGHT) {
                RenderUtil.drawRect(gfx, arrf[0], arrf[1], (arrf[0] + (float)(scaledWidth / 6)), (arrf[1] + (float)(scaledHeight / 3)), 0x2F000000);
            } else {
                RenderUtil.drawRect(gfx, arrf[0], arrf[1], (arrf[0] + (float)(scaledWidth / 3)), (arrf[1] + (float)(scaledHeight / 3)), 0x2F000000);
            }
        }

        int n3 = CheatBreaker.getScaledWidth();
        int n4 = CheatBreaker.getScaledHeight();


        float[] arrf2 = CBAnchorHelper.getPositions(this.module, mouseX, mouseY, this);
        if (hoveredAnchor != this.module.getGuiAnchor()) {
            this.module.setAnchor(hoveredAnchor);
            this.module.setTranslations(0.0f, 0.0f);
        }
        if (!Mouse.isButtonDown(1)) {
            gfx.pose().pushMatrix();
            gfx.pose().scale(f2);
            RenderUtil.drawRoundedRect(gfx, 2, 0.0, 1.8636363192038112 * 1.3414634466171265, n4, 0.0, -15599126);
            RenderUtil.drawRoundedRect(gfx, (float)n3 - 1.1197916f * 2.2325583f, 0.0, n3 - 2, n4, 0.0, -15599126);
            RenderUtil.drawRoundedRect(gfx, 0.0, 2, n3, 0.4375 * 5.714285714285714, 0.0, -15599126);
            RenderUtil.drawRoundedRect(gfx, 0.0, (float)n4 - 0.557971f * 6.2727275f, n3, n4 - 3, 0.0, -15599126);
            gfx.pose().popMatrix();
        }
        float f4 = (float) mouseX - arrf[0] - arrf2[0];
        float f5 = (float) mouseY - arrf[1] - arrf2[1];
        if (!Mouse.isButtonDown(1)) {
            float[] arrf3 = this.module.getScaledPoints(false);
            f4 = this.getXTranslation(this.module, f4, arrf3, (float)((int)(this.module.width * (Float) this.module.scale.getValue())));
            f5 = this.getYTranslation(this.module, f5, arrf3, (float)((int)(this.module.height * (Float) this.module.scale.getValue())));
        }
        this.module.setTranslations(f4, f5);
        gfx.pose().pushMatrix();
        this.module.scaleAndTranslate(gfx);

        RenderUtil.drawRoundedRect(gfx, -2, -2, this.module.width + 2.0f, this.module.height + 2.0f, 4, 551805923);
        gfx.pose().pushMatrix();
        RenderUtil.drawString(gfx, Fonts.ubuntuMedium16, this.module.getName(), 0.0f, -10f, 0x6F000000);
        gfx.pose().popMatrix();
        gfx.pose().popMatrix();
        gfx.pose().popMatrix();
    }

    private float getXTranslation(AbstractModule cBModule, float f, float[] arrf, float f2) {
        if (f + arrf[0] < 3f) {
            f = -arrf[0] + 3f;
        } else if (f + arrf[0] * (Float) cBModule.scale.getValue() + f2 > (this.width - 3f)) {
            f = (int)((float)this.width - arrf[0] * (Float) cBModule.scale.getValue() - f2 - 3f);
        }
        return f;
    }

    private float getYTranslation(AbstractModule cBModule, float f, float[] arrf, float f2) {
        if (f + arrf[1] < 2f) {
            f = -arrf[1] + 2f;
        } else if (f + arrf[1] * (Float) cBModule.scale.getValue() + f2 > (this.height - 2f)) {
            f = (int)((float)this.height - arrf[1] * (Float) cBModule.scale.getValue() - f2 - 2f);
        }
        return f;
    }

    @Override
    protected boolean onMouseClicked(double mx, double my, int button) {
        if (button != 0) return false;
        GuiAnchor cBGuiAnchor = CBAnchorHelper.getAnchor((float)mx, (float)my, this);
        this.module.setAnchor(cBGuiAnchor);
        CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
        this.module.setState(true);
        CBModulesGui modulesGui = new CBModulesGui();
        this.mc.setScreen(modulesGui);
        modulesGui.currentScrollableElement = modulesGui.modulesElement;
        modulesGui.currentScrollableElement.bottom = false;
        modulesGui.currentScrollableElement.scrollAmount = this.eventButton.modulesElement.scrollAmount;
        modulesGui.currentScrollableElement.yOffset = 0;

        return false;
    }

    @Override
    protected void onMouseReleased(double mx, double my, int button) {

    }
}
