package cc.vops.cheatbreaker.client.ui.element.type;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.ui.element.AbstractModulesGuiElement;
import cc.vops.cheatbreaker.client.ui.element.module.ModulesGuiButtonElement;
import cc.vops.cheatbreaker.client.ui.overlay.element.InputFieldElement;
import cc.vops.cheatbreaker.client.util.Keyboard;
import cc.vops.cheatbreaker.client.util.Mouse;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;

public class TextFieldElement extends AbstractModulesGuiElement {
    private final ModulesGuiButtonElement keybindButton;
    private final Setting setting;
    private final InputFieldElement textInputBar;
    private boolean awaitingKeybind = false;

    public TextFieldElement(Setting setting, float scaleFactor) {
        super(scaleFactor);
        this.setting = setting;
        this.textInputBar = new InputFieldElement(Fonts.playBold18, this.setting.getValue().toString(), 0, 0);
        this.textInputBar.setMaxStringLength(256);
        this.keybindButton = new ModulesGuiButtonElement(Fonts.playBold18, null, getKeybindText(), this.x + this.width - 100, this.y, 50, 18, -1895825408, scale);
        this.height = 18;
        this.textInputBar.setFontColor(-1895825408);
    }

    public String getKeybindText() {
        return setting.isAllowMouseKeybinding() ? "MB" + (setting.getKeyCode() + 1) : Keyboard.getKeyName(setting.getKeyCode());
    }

    @Override
    public void setDimensions(int x, int y, int width, int height) {
        super.setDimensions(x, y, width, height);
        this.textInputBar.setElementSize(
                this.x + (setting.isHasKeycode() ? 132 : 150),
                y + height - 9.5f,
                166 - (setting.isHasKeycode() ? 22 : 0),
                13.0f
        );
    }

    @Override
    public void handleDrawElement(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTicks) {
        boolean showKeybind = this.setting.isHasKeycode();

        if (textInputBar.isFocused()) {
            if (Keyboard.takeCharacterEvent() instanceof CharacterEvent(int codepoint)) {
                textInputBar.handleCharInput((char) codepoint, codepoint);
            } else {
                if (Keyboard.takeKeyEvent() instanceof KeyEvent(int key, int scancode, int modifiers)) {
                    if (key == GLFW.GLFW_KEY_ENTER) { // confirm
                        this.setting.setValue(textInputBar.getText());
                        textInputBar.setFocused(false);
                        CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
                        return;
                    }

                    textInputBar.handleElementKeyTyped((char) key, scancode, modifiers);
                }
            }

            textInputBar.handleElementUpdate();
        } else if (awaitingKeybind) {
            if (Keyboard.takeKeyEvent() instanceof KeyEvent(int key, int scancode, int modifiers)) {
                if (key != GLFW.GLFW_KEY_ESCAPE && key != GLFW.GLFW_KEY_DELETE && key != GLFW.GLFW_KEY_BACKSPACE) {
                    this.setting.setKeyCode(key);
                } else {
                    this.setting.setKeyCode(0);
                }
                this.awaitingKeybind = false;
                CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            } else if (Keyboard.takeCharacterEvent() instanceof CharacterEvent(int codepoint)) {
                this.setting.setKeyCode(-codepoint);
                this.awaitingKeybind = false;
                CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            }

            if (!keybindButton.isMouseInside(mouseX, mouseY, true)) {
                if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) {
                    this.awaitingKeybind = false;
                }
            }

            if (!awaitingKeybind) {
                keybindButton.displayString = getKeybindText();
            }
        }


        if (showKeybind) {
            this.keybindButton.yOffset = this.yOffset;
            this.keybindButton.setDimensions(this.x + this.width - 44 - 4, this.y + 4, 44, 14);
            this.keybindButton.handleDrawElement(gui, mouseX, mouseY, partialTicks);
        }

        RenderUtil.drawString(
                gui,
                Fonts.ubuntuMedium16,
                this.setting.getDisplayName().toUpperCase(),
                this.x + 10,
                (float) (this.y + 6),
                -1895825408
        );


        RenderUtil.drawRect(gui, this.x + (showKeybind ? 130 : 148), this.y + 4, this.x + this.width - (showKeybind ? 54 : 16), this.y + 17, 0x08000000);
        RenderUtil.drawRect(gui, this.x + (showKeybind ? 130 : 148), this.y + 16, this.x + this.width - (showKeybind ? 54 : 16), this.y + 17, -1895825408);

        this.textInputBar.setEnableBackgroundDrawing(false);
        this.textInputBar.drawElement(gui, mouseX, mouseY, true);
    }

    @Override
    public void onClick(int mouseX, int mouseY, int button) {
        // TODO: fix scroll height
        this.textInputBar.handleElementMouseClicked(mouseX, mouseY, button, true);

        if (setting.isHasKeycode() && this.keybindButton.isMouseInside(mouseX, mouseY, true)) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            keybindButton.displayString = "...";
            this.awaitingKeybind = true;
        }
    }
}
