package cc.vops.cheatbreaker.client.ui.element.module;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.module.staff.StaffModule;
import cc.vops.cheatbreaker.client.ui.element.AbstractModulesGuiElement;
import cc.vops.cheatbreaker.client.ui.element.AbstractScrollableElement;
import cc.vops.cheatbreaker.client.ui.element.type.*;
import cc.vops.cheatbreaker.client.ui.element.type.custom.GlobalSettingsElement;
import cc.vops.cheatbreaker.client.ui.module.CBModulesGui;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.sounds.SoundEvents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleListElement extends AbstractScrollableElement {
    private final int colour;
    protected final List<ModuleSettingsElement> moduleSetting;
    private final boolean isStaffModules;
    private final GlobalSettingsElement globalSettings;
    public AbstractScrollableElement scrollable;
    public boolean resetColor = false;
    public AbstractModule module;
    private final ModulesGuiButtonElement backButton;
    private final ModulesGuiButtonElement applyToAllTextButton;
    private final Map<AbstractModule, List<AbstractModulesGuiElement>> moduleElementListMap;
    private final List<AbstractModulesGuiElement> settingElement;

    public ModuleListElement(List<AbstractModule> list, float f, int n, int n2, int n3, int n4) {
        super(f, n, n2, n3, n4);
        this.isStaffModules = list == CheatBreaker.getInstance().getModuleManager().staffModules;
        this.colour = -12418828;
        this.globalSettings = new GlobalSettingsElement(this, this.colour, f);
        this.moduleSetting = new ArrayList<>();
        for (AbstractModule object : list) {
            if (object.isStaffModule() && !object.isStaffEnabledModule()) continue;
            this.moduleSetting.add(new ModuleSettingsElement(this, this.colour, object, f));
        }
        this.backButton = new ModulesGuiButtonElement(null, "arrow-64.png", this.x + 2, this.y + 4, 28, 28, -12418828, f);
        this.module = null;
        this.moduleElementListMap = new HashMap<>();

        for (AbstractModule object : list) {
            if (object.isStaffModule() && !object.isStaffEnabledModule()) continue;
            ArrayList<AbstractModulesGuiElement> object2 = new ArrayList<>();
            for (Setting cBSetting : object.getSettingsList()) {
                switch (cBSetting.getType()) {
                    case BOOLEAN: {
                        object2.add(new ToggleElement(cBSetting, f));
                        break;
                    }
                    case DOUBLE:
                    case INTEGER:
                    case FLOAT: {
                        if (object.isStaffModule() && cBSetting == ((StaffModule)object).getKeybindSetting() || object.isStaffModule() && cBSetting == object.scale) break;
                        if (cBSetting.getType().equals(Setting.Type.INTEGER) && cBSetting.getLabel().toLowerCase().contains("color")) {
                            object2.add(new ColorPickerElement(cBSetting, f));
                            break;
                        }
                        object2.add(new SliderElement(cBSetting, f));
                        break;
                    }
                    case STRING_ARRAY: {
                        object2.add(new ChoiceElement(cBSetting, f));
                        break;
                    }
                    case STRING: {
                        if (!cBSetting.getLabel().equalsIgnoreCase("label")) break;
                        object2.add(new LabelElement(cBSetting, f));
                    }
                }
            }
//            if (object.isStaffModule()) {
//                object2.add(new KeybindElement(((StaffModule)object).getKeybindSetting(), f));
//                if (object == CheatBreaker.getInstance().getModuleManager().xray) {
//                    object2.add(new XRayOptionsElement(CheatBreaker.getInstance().getModuleManager().xray.lIllIllIlIIllIllIlIlIIlIl(), "Blocks", f));
//                }
//            }
            this.moduleElementListMap.put(object, object2);
        }
        this.settingElement = new ArrayList<>();
        for (Setting object : CheatBreaker.getInstance().getGlobalSettings().settingsList) {
            switch (object.getType()) {
                case BOOLEAN: {
                    if (object == CheatBreaker.getInstance().getGlobalSettings().clearGlass) continue;
                    this.settingElement.add(new ToggleElement(object, f));
                    break;
                }
                case DOUBLE:
                case INTEGER:
                case FLOAT: {
                    if (object.getType().equals(Setting.Type.INTEGER) && object.getLabel().toLowerCase().contains("color")) {
                        this.settingElement.add(new ColorPickerElement(object, f));
                        break;
                    }
//                    if (object.getLabel().equals("World Time")) {
//                        this.settingElement.add(new WorldTimeElement(object, f));
//                        break;
//                    }
                    this.settingElement.add(new SliderElement(object, f));
                    break;
                }
                case STRING_ARRAY: {
                    if (object == CheatBreaker.getInstance().getGlobalSettings().clearGlass) continue;
                    this.settingElement.add(new ChoiceElement(object, f));
                    break;
                }
                case STRING: {
                    if (!object.getLabel().equalsIgnoreCase("label")) break;
                    this.settingElement.add(new LabelElement(object, f));
//                    if (!CheatBreaker.getInstance().getGlobalSettings().getCrosshairSettingsLabel().getValue().equals(object.getValue())) break;
//                    this.settingElement.add(new CrosshairElement(f));
                }
            }
        }
        int n5 = 25;
        for (AbstractModulesGuiElement object2 : this.settingElement) {
            n5 += object2.getHeight();
        }
//        this.applyToAllTextButton = new ModulesGuiButtonElement(CheatBreaker.getInstance().playBold18px, null, "Apply to all text", this.x + n3 - 120, this.y + n5 + 4, 110, 28, -12418828, f);
        this.applyToAllTextButton = new ModulesGuiButtonElement(Fonts.playBold18, null, "Apply to all text", this.x + n3 - 120, this.y + n5 + 4, 110, 28, -12418828, f);
    }

    @Override
    public boolean hasSettings(AbstractModule module) {
        return !module.getSettingsList().isEmpty() || module.getName().contains("Zans");
    }

    @Override
    public void handleModuleMouseClick(AbstractModule module) {
        CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
        this.scrollAmount = 0;
        this.startPosition = 0.0;
        this.module = module;
        this.yOffset = 0;
        this.scrollable = null;
    }

    @Override
    public void onClick(int mouseX, int my, int button) {
        int mouseY = my;

        if (this.module == null && !this.resetColor) {
            if (this.globalSettings.isMouseInside(mouseX, mouseY, true) && !this.isStaffModules) {
                CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
                this.resetColor = true;
                this.scrollAmount = 0;
                this.startPosition = 0.0;
                this.yOffset = 0;
            } else {
                for (ModuleSettingsElement moduleSetting : this.moduleSetting) {
                    if (moduleSetting.isMouseInside(mouseX, mouseY, true)) {
                        if (!this.hasSettings(moduleSetting.module)) {
                            continue;
                        }
                        moduleSetting.onClick(mouseX, mouseY, button);
                    }
                }
            }
        } else if (!this.backButton.isMouseInside(mouseX, mouseY, true)) {
            if (this.module != null && this.moduleElementListMap.containsKey(this.module)) {
                for (AbstractModulesGuiElement abstractModulesGuiElement : this.moduleElementListMap.get(this.module)) {
                    if (!abstractModulesGuiElement.isMouseInside(mouseX, mouseY, true)) {
                        continue;
                    }
                    abstractModulesGuiElement.onClick(mouseX, mouseY, button);
                }
            } else if (this.resetColor) {
                if (this.applyToAllTextButton.isMouseInside(mouseX, mouseY, true)) {
                    for (AbstractModule module : CheatBreaker.getInstance().getModuleManager().modules) {
                        for (Setting setting : module.getSettingsList()) {
                            if (setting.getType() == Setting.Type.INTEGER && setting.getLabel().toLowerCase().contains("color")) {
                                if (setting.getLabel().toLowerCase().contains("background")) {
                                    continue;
                                }
                                CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
                                setting.setValue(CheatBreaker.getInstance().getGlobalSettings().defaultColor.getColorValue());
                            }
                        }
                    }
                } else {
                    for (AbstractModulesGuiElement settingElement : this.settingElement) {
                        if (!settingElement.isMouseInside(mouseX, mouseY, true)) {
                            continue;
                        }
                        settingElement.onClick(mouseX, mouseY, button);
                    }
                }
            }
        } else {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            this.module = null;
            this.resetColor = false;
            if (this.scrollable != null) {
                CBModulesGui.instance.currentScrollableElement = this.scrollable;
            }
        }
        double d = this.height - 10;
        double d2 = this.scrollHeight;
        double d3 = d / d2 * (double)100;
        double d4 = d / (double)100 * d3;
        double d5 = (double)this.scrollAmount / 100.0 * d3;
        boolean bl4 = (float)mouseX > (float)(this.x + this.width - 9) * this.scale && (float)mouseX < (float)(this.x + this.width - 3) * this.scale && (double)mouseY > ((double)(this.y + 11) - d5) * (double)this.scale && (double)mouseY < ((double)(this.y + 8) + d4 - d5) * (double)this.scale;
        boolean bl3 = (float)mouseX > (float)(this.x + this.width - 9) * this.scale && (float)mouseX < (float)(this.x + this.width - 3) * this.scale && (float)mouseY > (float)(this.y + 11) * this.scale && (double)mouseY < ((double)(this.y + 6) + d - (double)3) * (double)this.scale;
        if (button == 0 && bl3 || bl4) {
            this.hovering = true;
        }
    }

    @Override
    public void handleDrawElement(GuiGraphicsExtractor gui, int mouseX, int my, float partialTicks) {
        int mouseY = my;

        RenderUtil.drawRoundedRect(gui, this.x, this.y, this.x + this.width, this.y + this.height + 2, (double)8, -657931);
        this.preDraw(gui, mouseX, mouseY);

        if (this.module == null && !this.resetColor) {
            this.scrollHeight = 72;
            if (!this.isStaffModules) {
                this.globalSettings.setDimensions(this.x + 4, this.y + 4, this.width - 12, 18);
                this.globalSettings.yOffset = this.scrollAmount;
                this.globalSettings.handleDrawElement(gui, mouseX, mouseY, partialTicks);
                this.scrollHeight += globalSettings.getHeight();
            }

            for (int i = 0; i < this.moduleSetting.size(); ++i) {
                ModuleSettingsElement moduleSettingsElement = this.moduleSetting.get(i);
                moduleSettingsElement.setDimensions(this.x + 4, this.y + (this.isStaffModules ? 4 : 24) + i * 20, this.width - 12, 18);
                moduleSettingsElement.yOffset = this.scrollAmount;
                moduleSettingsElement.handleDrawElement(gui, mouseX, mouseY, partialTicks);
                this.scrollHeight += moduleSettingsElement.getHeight();
            }
        } else if (this.resetColor && !this.isStaffModules) {
            RenderUtil.drawRect(gui, this.x + 32, this.y + 4, this.x + 33, this.y + Math.max(this.height, this.scrollHeight) - 4, 0x2F2F2F2F);
            this.scrollHeight = 25;
            this.backButton.setDimensions(this.x + 2, this.y + 2, 28, 28);
            this.backButton.yOffset = this.scrollAmount;
            this.backButton.handleDrawElement(gui, mouseX, mouseY, partialTicks);
            RenderUtil.drawString(gui, Fonts.ubuntuMedium16, "CheatBreaker Settings".toUpperCase(), this.x + 38, this.y + 6, -1358954496);
            RenderUtil.drawRect(gui, this.x + 38, this.y + 17, this.x + this.width - 6, this.y + 18, 0x2F2F2F2F);
            int n3 = 0;
            for (AbstractModulesGuiElement settingElement : this.settingElement) {
                settingElement.setDimensions(this.x + 38, this.y + 22 + n3, this.width - 40, settingElement.getHeight());
                settingElement.yOffset = this.scrollAmount;
                settingElement.handleDrawElement(gui, mouseX, mouseY, partialTicks);
                n3 += 2 + settingElement.getHeight();
                this.scrollHeight += 2 + settingElement.getHeight();
            }
            this.applyToAllTextButton.yOffset = this.scrollAmount;
            this.applyToAllTextButton.setDimensions(this.x + this.width - 118, this.y + this.scrollHeight, 100, 20);
            this.applyToAllTextButton.handleDrawElement(gui, mouseX, mouseY, partialTicks);
            this.scrollHeight += 24;
        } else {
            RenderUtil.drawRect(gui, this.x + 32, this.y + 4, this.x + 33, this.y + Math.max(this.height, this.scrollHeight) - 4, 0x2F2F2F2F);
            this.scrollHeight = 37;
            this.backButton.setDimensions(this.x + 2, this.y + 2, 28, 28);
            this.backButton.yOffset = this.scrollAmount;
            this.backButton.handleDrawElement(gui, mouseX, mouseY, partialTicks);

            RenderUtil.drawString(gui, Fonts.ubuntuMedium16, (this.module.getName() + " Settings").toUpperCase(), this.x + 38, this.y + 6, -1358954496);
            RenderUtil.drawRect(gui, this.x + 38, this.y + 17, this.x + this.width - 6, this.y + 18, 0x2F2F2F2F);

            if (this.module.getSettingsList().isEmpty()) {
                RenderUtil.drawString(gui, Fonts.ubuntuMedium16, (this.module.getName().toUpperCase() + " DOES NOT HAVE ANY OPTIONS.").toUpperCase(), this.x + 38, this.y + 22, -1895825408);
            }

            int n4 = 0;
            if (this.module != null && moduleElementListMap.containsKey(this.module)) {
                for (AbstractModulesGuiElement abstractElement : this.moduleElementListMap.get(this.module)) {
                    abstractElement.setDimensions(this.x + 38, this.y + 22 + n4, this.width - 40, abstractElement.getHeight());
                    abstractElement.yOffset = this.scrollAmount;
                    abstractElement.handleDrawElement(gui, mouseX, mouseY, partialTicks);
                    n4 += abstractElement.getHeight();
                    this.scrollHeight += abstractElement.getHeight();
                }
            }
        }

        this.postDraw(gui, mouseX, mouseY);
    }
}
