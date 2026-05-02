package cc.vops.cheatbreaker.client.ui.element.module;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.ui.element.AbstractModulesGuiElement;
import cc.vops.cheatbreaker.client.ui.element.AbstractScrollableElement;
import cc.vops.cheatbreaker.client.ui.module.CBModulePlaceGui;
import cc.vops.cheatbreaker.client.ui.module.CBModulesGui;
import cc.vops.cheatbreaker.client.util.font.CBFontRenderer;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.sounds.SoundEvents;

import java.awt.*;
import java.util.Objects;

public class ModulePreviewElement extends AbstractModulesGuiElement {
    private final AbstractModule module;
    private final ModulesGuiButtonElement optionsButton;
    private final ModulesGuiButtonElement toggleOrHideFromHud;
    private final ModulesGuiButtonElement toggle;
    private final AbstractScrollableElement scrollableElement;

    public ModulePreviewElement(AbstractScrollableElement scrollableElement, AbstractModule module, float scale) {
        super(scale);
        this.scrollableElement = scrollableElement;
        this.module = module;

        CBFontRenderer optionsFontRenderer = Fonts.playBold18;
        CBFontRenderer hideOrToggleFontRenderer = Fonts.playRegular14;

        this.optionsButton = new ModulesGuiButtonElement(optionsFontRenderer, null, "Options", this.x + 4, this.y + this.height - 20, this.x + this.width - 4, this.y + this.height - 6, -12418828, scale);
        this.toggleOrHideFromHud = new ModulesGuiButtonElement(hideOrToggleFontRenderer, null, module.getGuiAnchor() == null ? (module.isRenderHud() ? "Disable" : "Enable") : (module.isRenderHud() ? "Hide from HUD" : "Add to HUD"), this.x + 4, this.y + this.height - 38, this.x + this.width / 2 - 2, this.y + this.height - 24, module.isRenderHud() ? -5756117 : -13916106, scale);
        this.toggleOrHideFromHud.allowAddToHud(module != CheatBreaker.getInstance().getModuleManager().notifications);
        this.toggle = new ModulesGuiButtonElement(hideOrToggleFontRenderer, null, module.isEnabled() ? "Disable" : "Enable", this.x + this.width / 2 + 2, this.y + this.height - 38, this.x + this.width - 4, this.y + this.height - 24, module.isEnabled() ? -5756117 : -13916106, scale);
    }

    @Override
    public void handleDrawElement(GuiGraphicsExtractor gui, int mouseX, int my, float partialTicks) {
        float f2;
        String object;
        int n3 = 0;
        int n4 = 0;

        int mouseY = my;

        if (this.module.isEnabled()) {
            RenderUtil.drawRect(gui, this.x, this.y, this.x + this.width, this.y + this.height, -13916106);
        } else {
            RenderUtil.drawRect(gui, this.x, this.y, this.x + this.width, this.y + this.height, -1347374928);
        }

        gui.pose().pushMatrix();

        if (this.module == CheatBreaker.getInstance().getModuleManager().potionStatus) {
            n4 = -20;
            RenderUtil.drawStringWithShadow(gui, Minecraft.getInstance().font, "Speed II", this.x + (float) this.width / 2 - 28 + 8 + 18, this.y + (float) this.height / 2 - 36 + 2, -1);
            RenderUtil.drawStringWithShadow(gui, Minecraft.getInstance().font, "0:42", this.x + (float) this.width / 2 - 28 + 8 + 18, this.y + (float) this.height / 2 - 26 + 2, -1);
        } else if (this.module == CheatBreaker.getInstance().getModuleManager().scoreboard) {
            RenderUtil.drawRect(gui, this.x + 20, this.y + (float) this.height / 2 - 44, this.x + this.width - 20, this.y + (float) this.height / 2 - 6, new Color(0, 0, 0, 111).getRGB());
            RenderUtil.drawCenteredStringWithShadow(gui, Minecraft.getInstance().font, "Score", this.x + this.width / 2, this.y + this.height / 2 - 40, -1);
            RenderUtil.drawStringWithShadow(gui, Minecraft.getInstance().font, "Steve", this.x + 24, this.y + (float) this.height / 2 - 28, -1);
            RenderUtil.drawStringWithShadow(gui, Minecraft.getInstance().font, "Alex", this.x + 24, this.y + (float) this.height / 2 - 18, -1);
            RenderUtil.drawCenteredStringWithShadow(gui, Minecraft.getInstance().font, "§c0", this.x + this.width - 26, this.y + this.height / 2 - 18, -1);
            RenderUtil.drawCenteredStringWithShadow(gui, Minecraft.getInstance().font, "§c1", this.x + this.width - 26, this.y + this.height / 2 - 28, -1);
        }

        if ((this.module.getPreviewType() == null || this.module.getPreviewType() == AbstractModule.PreviewType.LABEL) && this.module != CheatBreaker.getInstance().getModuleManager().scoreboard) {
            object = "";
            Font minecraftFont = Minecraft.getInstance().font;
            if (this.module.getPreviewType() == null) {
                f2 = 2.0f;
                for (String string : this.module.getName().split(" ")) {
                    String string2 = string.substring(0, 1);
                    object = object + (Objects.equals(object, "") ? string2 : string2.toLowerCase());
                }
            } else {
                f2 = this.module.getPreviewLabelSize();
                object = this.module.getPreviewLabel();
            }
            gui.pose().scale(f2, f2);
            float f3 = (float)minecraftFont.width(object) * f2;
            if (this.module.getPreviewType() == null) {
                RenderUtil.drawString(gui, minecraftFont, object, (((float)(this.x + 1 + this.width / 2) - f3 / 2.0f) / f2), ((float)(this.y + this.height / 2 - 32) / f2), -13750738);
            } else {
                RenderUtil.drawStringWithShadow(gui, minecraftFont, object, (((float)(this.x + 1 + this.width / 2) - f3 / 2.0f) / f2), ((float)(this.y + this.height / 2 - 32) / f2), -1);
            }
        } else if (this.module.getPreviewType() == AbstractModule.PreviewType.ICON) {
            float f4 = this.module.getPreviewIconWidth();
            f2 = this.module.getPreviewIconHeight();
            RenderUtil.drawIcon(gui, this.module.getPreviewIcon(), (float)(this.x + this.width / 2) - f4 / 2.0f + (float)n4, (float)(this.y + n3 + this.height / 2 - 26) - f2 / 2.0f, f4, f2);
        }

        gui.pose().popMatrix();
        float moduleNameOffset = this.y + this.height / 2f;

        RenderUtil.drawCenteredStringWithShadow(gui, Fonts.playBold18, this.module.getName(), ((this.x + (float) this.width / 2) - 1.125f * 1.3333334f), moduleNameOffset, -1);

        this.toggle.displayString = this.module.isEnabled() ? "Disable" : "Enable";
        this.toggle.yOffset = this.yOffset;
        this.toggle.highlightColor = this.module.isEnabled() ? -5756117 : -13916106;
        this.toggleOrHideFromHud.displayString = this.module.getGuiAnchor() == null ? (this.module.isRenderHud() && this.module.isEnabled() ? "Disable" : "Enable") : (this.module.isRenderHud() && this.module.isEnabled() ? "Hide from HUD" : "Add to HUD");
        this.toggleOrHideFromHud.highlightColor = this.module.isRenderHud() && this.module.isEnabled() ? -5756117 : -13916106;
        this.optionsButton.setDimensions(this.x + 4, this.y + this.height - 20, this.width - 8, 16);
        this.optionsButton.yOffset = this.yOffset;
        this.optionsButton.handleDrawElement(gui, mouseX, mouseY, partialTicks);
        this.toggleOrHideFromHud.setDimensions(this.x + 4, this.y + this.height - 38, this.module.isEditable ? this.width - 8 : this.width / 2 + 2, this.y + this.height - 24 - (this.y + this.height - 38));
        this.toggleOrHideFromHud.yOffset = this.yOffset;
        this.toggleOrHideFromHud.handleDrawElement(gui, mouseX, mouseY, partialTicks);

        if (!this.module.isEditable) {
            this.toggle.setDimensions(this.x + this.width / 2 + 8, this.y + this.height - 38, this.width / 2 - 12, this.y + this.height - 24 - (this.y + this.height - 38));
            this.toggle.handleDrawElement(gui, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void onClick(int mouseX, int mouseY, int button) {
        if (this.optionsButton.isMouseInside(mouseX, mouseY, true)) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            ((ModuleListElement) CBModulesGui.instance.settingsElement).resetColor = false;
            ((ModuleListElement) CBModulesGui.instance.settingsElement).scrollable = this.scrollableElement;
            ((ModuleListElement) CBModulesGui.instance.settingsElement).module = this.module;
            CBModulesGui.instance.currentScrollableElement = CBModulesGui.instance.settingsElement;
        } else if (!this.module.isEditable && this.toggle.isMouseInside(mouseX, mouseY, true)) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            this.module.setState(!this.module.isEnabled());
            this.toggle.displayString = this.module.isEnabled() ? "Disable" : "Enable";
            this.toggle.highlightColor = this.module.isEnabled() ? -5756117 : -13916106;
            if (this.module.isEnabled()) {
                this.applyDefaultColor();
                this.module.setState(true);
            }
        } else if (this.toggleOrHideFromHud.moduleAllowedToAddToHud && this.toggleOrHideFromHud.isMouseInside(mouseX, mouseY, true)) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            if (!this.module.isEnabled()) {
                this.module.setRenderHud(true);
                this.applyDefaultColor();
                if (this.module.getGuiAnchor() == null) {
                    this.module.setState(true);
                } else {
                    Minecraft.getInstance().setScreen(new CBModulePlaceGui(CBModulesGui.instance, this.module));
                }
            } else {
                this.module.setRenderHud(!this.module.isRenderHud());
                if (this.module.isRenderHud()) {
                    this.applyDefaultColor();
                    if (this.module.getGuiAnchor() == null) {
                        this.module.setState(true);
                    } else {
                        Minecraft.getInstance().setScreen(new CBModulePlaceGui(CBModulesGui.instance, this.module));
                    }
                } else if (this.module.isEditable && this.module.isEnabled()) {
                    this.module.setState(false);
                }
            }
            this.toggleOrHideFromHud.displayString = this.module.getGuiAnchor() == null ? (this.module.isRenderHud() && this.module.isEnabled() ? "Disable" : "Enable") : (this.module.isRenderHud() && this.module.isEnabled() ? "Hide from HUD" : "Add to HUD");
            this.toggleOrHideFromHud.highlightColor = this.module.isRenderHud() && this.module.isEnabled() ? -5756117 : -13916106;
        }
    }

    private void applyDefaultColor() {
//        if (this.module == CheatBreaker.getInstance().getModuleManager().directionHUDMod
//                || this.module == CheatBreaker.getInstance().getModuleManager().playerListMod
//                || this.module == CheatBreaker.getInstance().getModuleManager().enchantmentGlintMod
//                || this.module == CheatBreaker.getInstance().getModuleManager().packTweaksMod
//                || this.module == CheatBreaker.getInstance().getModuleManager().blockOverlayMod
//                || this.module == CheatBreaker.getInstance().getModuleManager().hitColorMod
//                || this.module == CheatBreaker.getInstance().getModuleManager().crosshairMod
//                || this.module == CheatBreaker.getInstance().getModuleManager().scoreboardMod
//                || !(Boolean) CheatBreaker.getInstance().getGlobalSettings().resetColors.getValue()) {
//            return;
//        }
        for (Setting cBSetting : this.module.getSettingsList()) {
            if (cBSetting.getType() != Setting.Type.INTEGER || !cBSetting.getLabel().toLowerCase().contains("color") || cBSetting.getLabel().toLowerCase().contains("background") || cBSetting.getLabel().toLowerCase().contains("pressed")) continue;
            cBSetting.setValue(CheatBreaker.getInstance().getGlobalSettings().defaultColor.getValue());
        }
    }
}
