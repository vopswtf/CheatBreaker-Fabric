package cc.vops.cheatbreaker.client.ui.module;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.ConfigManager;
import cc.vops.cheatbreaker.client.config.Profile;
import cc.vops.cheatbreaker.client.ui.AbstractGui;
import cc.vops.cheatbreaker.client.ui.element.profile.ProfileElement;
import cc.vops.cheatbreaker.client.ui.element.profile.ProfilesListElement;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.nio.file.Files;

public class CBProfileCreateGui extends AbstractGui {
    private final CBModulesGui previousScreen;
    private final float scale;
    private final int highlightColor;
    private final ProfilesListElement parent;
    private EditBox textField = null;
    private String errorString = "";
    private boolean showGui = false;
    private Profile profile;

    public CBProfileCreateGui(Profile profile, CBModulesGui guiScreen, ProfilesListElement parent, int n, float f) {
        this(guiScreen, parent, n, f);
        this.profile = profile;
    }

    public CBProfileCreateGui(CBModulesGui guiScreen, ProfilesListElement parent, int n, float f) {
        this.previousScreen = guiScreen;
        this.scale = f;
        this.parent = parent;
        this.highlightColor = n;
        this.showGui = true;
    }

    @Override
    protected void initMenu() {
        if (!this.showGui) {
            this.mc.setScreen(this.previousScreen);
            this.previousScreen.currentScrollableElement = this.previousScreen.profilesElement;
        } else {
            this.showGui = false;
            this.textField = new EditBox(this.mc.font, this.width / 2 - 70, this.height / 2 - 6, 140, 10, Component.empty());
            if (this.profile != null) {
                this.textField.setValue(this.profile.getName());
            }
            this.textField.setFocused(true);
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int key = event.key();

        if (key == GLFW.GLFW_KEY_ENTER) {
            if (this.textField.getValue().length() < 3) {
                this.errorString = ChatFormatting.RED + "Name must be at least 3 characters long.";
                return false;
            }
            if (this.textField.getValue().equalsIgnoreCase("default")) {
                this.errorString = ChatFormatting.RED + "That name is already in use.";
                return false;
            }
            if (!this.textField.getValue().matches("([a-zA-Z0-9-_ \\]\\[]+)")) {
                this.errorString = ChatFormatting.RED + "Illegal characters in name.";
                return false;
            }

            if (this.profile != null && !this.profile.isEditable()) {
                File baseProfile = ConfigManager.getConfigFile(this.profile.getName());
                File file2 = ConfigManager.getConfigFile(this.textField.getValue() + ".json");
                if (!baseProfile.exists()) return false;
                try {
                    Files.copy(baseProfile.toPath(), file2.toPath());
                    Files.delete(baseProfile.toPath());
                    this.profile.setName(this.textField.getValue());
                    this.mc.setScreen(this.previousScreen);
                    this.previousScreen.currentScrollableElement = this.previousScreen.profilesElement;
                } catch (Exception exception) {
                    this.errorString = ChatFormatting.RED + "Could not save profile.";
                    exception.printStackTrace();
                }
                System.out.println("Renamed profile " + this.profile.getName() + " to " + this.textField.getValue());
                return true;
            }
            Profile profile = null;
            for (Profile profile2 : CheatBreaker.getInstance().getProfiles()) {
                if (!profile2.getName().toLowerCase().equalsIgnoreCase(this.textField.getValue())) continue;
                profile = profile2;
                break;
            }
            if (profile == null) {
                System.out.println("Created new profile " + this.textField.getValue());
                CheatBreaker.getInstance().getConfigManager().writeProfile(CheatBreaker.getInstance().getActiveProfile().getName());
                Profile profile3 = new Profile(this.textField.getValue(), false);
                CheatBreaker.getInstance().getProfiles().add(profile3);
                CheatBreaker.getInstance().setActiveProfile(profile3);
                this.parent.profileElementList.add(new ProfileElement(this.parent, this.highlightColor, profile3, this.scale));
                CheatBreaker.getInstance().getConfigManager().writeProfile(CheatBreaker.getInstance().getActiveProfile().getName());
                this.mc.setScreen(this.previousScreen);
                this.previousScreen.currentScrollableElement = this.previousScreen.profilesElement;
                return true;
            }
            this.errorString = ChatFormatting.RED + "That name is already in use.";
        }


        if (this.textField.keyPressed(event)) {
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (this.textField.charTyped(event)) {
            return true;
        }
        return super.charTyped(event);
    }

    @Override
    protected void drawMenu(GuiGraphicsExtractor gfx, float mouseX, float mouseY, float delta) {
        this.previousScreen.drawMenu(gfx, mouseX, mouseY, delta);

        gfx.pose().pushMatrix();

        float scale = 1 / CheatBreaker.getScaleFactor();
        gfx.pose().scale(scale, scale);

        RenderUtil.drawRect(gfx, this.width / 2f - 73, this.height / 2f - 19, this.width / 2f + 73, this.height / 2f + 8, -11250604);
        RenderUtil.drawRect(gfx, this.width / 2f - 72, this.height / 2f - 18, this.width / 2f + 72, this.height / 2f + 7, -3881788);
        this.textField.extractWidgetRenderState(gfx, (int) mouseX, (int) mouseY, delta);

        gfx.pose().scale(this.scale, this.scale);

        int n3 = (int) ((float) this.width / this.scale);
        int n4 = (int) ((float) this.height / this.scale);

        RenderUtil.drawString(gfx, Fonts.ubuntuMedium16, "Profile Name: ", (float) (n3 / 2) - (float) 70 / this.scale, (float) (n4 / 2) - (float) 17 / this.scale, 0x6F000000);
        RenderUtil.drawString(gfx, Fonts.ubuntuMedium16, this.errorString, (float) (n3 / 2) - (float) 72 / this.scale, (float) (n4 / 2) + (float) 8 / this.scale, -1358954496);

        gfx.pose().popMatrix();
    }

    @Override
    protected boolean onMouseClicked(double mx, double my, int button) {
        this.textField.mouseClicked(new MouseButtonEvent(mx, my, new MouseButtonInfo(button, 0)), true);
        return false;
    }

    @Override
    protected void onMouseReleased(double mx, double my, int button) {

    }
}
