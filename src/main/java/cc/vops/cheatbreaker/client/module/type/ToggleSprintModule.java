package cc.vops.cheatbreaker.client.module.type;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.event.type.GuiDrawEvent;
import cc.vops.cheatbreaker.client.event.type.RenderPreviewEvent;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.ui.module.GuiAnchor;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.player.LocalPlayer;

import java.text.DecimalFormat;

public class ToggleSprintModule extends AbstractModule {
    public static Setting toggleSprint;
    public static Setting toggleSneak;
    public static Setting toggleSneakInMenus;
    public static Setting showHudText;
    private Setting textColor;
    public static Setting doubleTap;
    public static Setting flyBoost;
    public static Setting showWhileTyping;
    private Setting flyBoostLabel;
    public static Setting flyBoostAmount;
    public Setting flyBoostString;
    public Setting flyString;

    public Setting ridingString;
    public Setting decendString;
    public Setting dismountString;
    public Setting sneakHeldString;
    public Setting sprintHeldString;
    public Setting sprintVanillaString;
    public Setting sprintToggledString;
    public Setting sneakToggledString;

    public ToggleSprintModule() {
        super("ToggleSprint");
        this.setDefaultAnchor(GuiAnchor.LEFT_TOP);
        this.setDefaultTranslations(0.0f, 10);
        this.setDefaultState(false);
        toggleSprint = new Setting(this, "Toggle Sprint").setValue(true);
        toggleSneak = new Setting(this, "Toggle Sneak").setValue(false);
        toggleSneakInMenus = new Setting(this, "Force Sneak In Menus").setValue(false);
        showHudText = new Setting(this, "Show HUD Text").setValue(true);
        this.textColor = new Setting(this, "Text Color").setValue(-1).setMinMax(Integer.MIN_VALUE, Integer.MAX_VALUE);
        doubleTap = new Setting(this, "Double Tap").setValue(false);
        showWhileTyping = new Setting(this, "Show While Typing").setValue(true);
        this.flyBoostLabel = new Setting(this, "label").setValue("Fly Boost");
        flyBoost = new Setting(this, "Fly Boost").setValue(true);
        flyBoostAmount = new Setting(this, "Fly Boost Amount").setValue(4).setMinMax(2, 8).setParent(flyBoost);

        this.flyBoostString = new Setting(this, "Fly Boost String").setValue("[Flying (%BOOST%x boost)]");
        this.flyString = new Setting(this, "Fly String").setValue("[Flying]");
        this.ridingString = new Setting(this, "Riding String").setValue("[Riding]");
        this.decendString = new Setting(this, "Descend String").setValue("[Descending]");
        this.dismountString = new Setting(this, "Dismount String").setValue("[Dismounting]");
        this.sneakHeldString = new Setting(this, "Sneaking String").setValue("[Sneaking (Key Held)]");
        this.sprintHeldString = new Setting(this, "Sprinting Held String").setValue("[Sprinting (Key Held)]");
        this.sprintVanillaString = new Setting(this, "Sprinting Vanilla String").setValue("[Sprinting (Vanilla)]");
        this.sprintToggledString = new Setting(this, "Sprinting Toggle String").setValue("[Sprinting (Toggled)]");
        this.sneakToggledString = new Setting(this, "Sneaking Toggle String").setValue("[Sneaking (Toggled)]");
        this.setPreviewLabel("[Sprinting (Toggled)]", 1.0f);
        this.addEvent(GuiDrawEvent.class, this::renderReal);
        this.addEvent(RenderPreviewEvent.class, this::renderPreview);

        ClientTickEvents.START_CLIENT_TICK.register(this::onTick);
    }

    private void renderPreview(RenderPreviewEvent renderPreviewEvent) {
        if (!this.isRenderHud()) {
            return;
        }
        if (!getDisplayString().isEmpty()) return;

        GuiGraphicsExtractor gfx = renderPreviewEvent.getGraphics();

        gfx.pose().pushMatrix();
        gfx.pose().scale(CheatBreaker.getScaleFactor(), CheatBreaker.getScaleFactor());

        String displayString = sprintToggledString.getValue().toString();
        int n = this.minecraft.font.width(displayString);
        this.setDimensions(n, 18);
        this.scaleAndTranslate(gfx);
        RenderUtil.drawStringWithShadow(gfx, this.minecraft.font, displayString, 0.0f, 0.0f, this.textColor.getColorValue());

        gfx.pose().popMatrix();
    }

    private void renderReal(GuiDrawEvent guiDrawEvent) {
        if (!this.isRenderHud()) {
            return;
        }
        if ((Boolean) showHudText.getValue() && ((Boolean) showWhileTyping.getValue() || !(this.minecraft.screen instanceof ChatScreen))) {
            GuiGraphicsExtractor gfx = guiDrawEvent.getGraphics();

            gfx.pose().pushMatrix();
            gfx.pose().scale(CheatBreaker.getScaleFactor(), CheatBreaker.getScaleFactor());

            String displayString = getDisplayString();
            if (displayString.isEmpty()) {
                gfx.pose().popMatrix();
                return;
            }

            int n = this.minecraft.font.width(displayString);
            this.setDimensions(n, 18);
            this.scaleAndTranslate(gfx);
            RenderUtil.drawStringWithShadow(gfx, this.minecraft.font, displayString, 0.0f, 0.0f, this.textColor.getColorValue());

            gfx.pose().popMatrix();
        }
    }

    private void onTick(Minecraft client) {
        if (!this.isEnabled()) return;
        if (minecraft.level == null) {
            sprintToggled = false;
            sneakToggled = false;
            wasSprintPressed = false;
            wasSneakPressed = false;
            return;
        }

        if ((Boolean) toggleSprint.getValue()) {
            tickSprint(client);
        }

        if ((Boolean) toggleSneak.getValue()) {
            tickSneak(client);
        }
    }

    private KeyMapping getSprintKey(Minecraft client) {
        return client.options.keySprint;
    }

    private KeyMapping getSneakKey(Minecraft client) {
        return client.options.keyShift;
    }

    public static boolean sprintToggled = false;
    public static boolean sneakToggled = false;
    private boolean wasSprintPressed = false;
    private boolean wasSneakPressed = false;

    private void tickSprint(Minecraft client) {
        KeyMapping sprintKey = getSprintKey(client);
        if (sprintKey.isDown() && !wasSprintPressed) {
            wasSprintPressed = true;
            sprintToggled = !sprintToggled;
        } else if (!sprintKey.isDown()) {
            wasSprintPressed = false;
        }
    }

    private void tickSneak(Minecraft client) {
        if (client.screen != null && !(Boolean) toggleSneakInMenus.getValue()) {
            sneakToggled = false;
            wasSneakPressed = false;
            return;
        }

        KeyMapping sneakKey = getSneakKey(client);
        if (sneakKey.isDown() && !wasSneakPressed) {
            wasSneakPressed = true;
            sneakToggled = !sneakToggled;
        } else if (!sneakKey.isDown()) {
            wasSneakPressed = false;
        }
    }

    private String getDisplayString() {
        if (!this.isEnabled()) return "";
        Minecraft minecraft = this.minecraft;
        if (minecraft.level == null)return "";

        LocalPlayer entityPlayerSP = minecraft.player;
        if (entityPlayerSP == null) return "";

        KeyMapping sprintKey = getSprintKey(minecraft);
        KeyMapping sneakKey = getSneakKey(minecraft);

        String string = "";
        boolean flying = entityPlayerSP.getAbilities().flying;
        boolean riding = entityPlayerSP.getVehicle() != null;
        boolean sneakHeld = sneakKey.isDown();
        boolean sprintHeld = sprintKey.isDown();

        if (flying) {
            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            if ((Boolean) flyBoost.getValue() && sprintHeld && entityPlayerSP.getAbilities().instabuild) {
                string += this.flyBoostString.getValue().toString().replaceAll("%BOOST%", decimalFormat.format(flyBoostAmount.getValue()));
            } else {
                string += this.flyString.getValue().toString();
            }
        }

        if (riding) {
            string = string + this.ridingString.getValue().toString();
        }

        if (sneakToggled) {
            string = flying ? this.decendString.getValue().toString() :
                    (riding ? this.dismountString.getValue().toString() :
                            (sneakHeld ? string + this.sneakHeldString.getValue() :
                                    string + this.sneakToggledString.getValue()));
        } else if ((sprintToggled || entityPlayerSP.isSprinting()) && !flying && !riding) {
            //             string = sprintHeld ? string + CheatBreaker.getInstance().getModuleManager().toggleSprint.sprintHeldString.getValue() : (bl5 ? string + CheatBreaker.getInstance().getModuleManager().toggleSprint.sprintVanillaString.getValue() : string + CheatBreaker.getInstance().getModuleManager().toggleSprint.sprintToggledString.getValue());
            boolean isVanillaSprinting = entityPlayerSP.isSprinting() && !sprintToggled;
            if (sprintHeld) {
                string += this.sprintHeldString.getValue().toString();
            } else if (isVanillaSprinting) {
                string += this.sprintVanillaString.getValue().toString();
            } else {
                string += this.sprintToggledString.getValue().toString();
            }
        }

        return string;
    }

}
