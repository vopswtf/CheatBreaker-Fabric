package cc.vops.cheatbreaker.client.ui.overlay.element;

import cc.vops.cheatbreaker.client.ui.mainmenu.AbstractElement;
import cc.vops.cheatbreaker.client.util.ChatAllowedCharacters;
import cc.vops.cheatbreaker.client.util.Keyboard;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import cc.vops.cheatbreaker.client.util.font.CBFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.lwjgl.glfw.GLFW;

public class InputFieldElement extends AbstractElement {
    private final CBFontRenderer font;
    private String text = "";
    private int maxStringLength = 32;
    private int cursorCounter;
    private boolean enableBackgroundDrawing = true;
    private boolean canLoseFocus = true;
    private boolean isFocused;
    private boolean isEnabled = true;
    private int lineScrollOffset;
    private int cursorPosition;
    private int selectionEnd;
    private int enabledColor = 0xE0E0E0;
    private int disabledColor = 0x707070;
    private boolean visible = true;
    private final int color1;
    private final int color2;
    private final String label;

    public InputFieldElement(CBFontRenderer font, String label, int color1, int color2) {
        this.font = font;
        this.label = label;
        this.color1 = color1;
        this.color2 = color2;
    }

    public void updateCursorCounter() {
        ++this.cursorCounter;
    }

    public void setText(String string) {
        this.text = string.length() > this.maxStringLength ? string.substring(0, this.maxStringLength) : string;
        this.setCursorPositionEnd();
    }

    public String getText() {
        return this.text;
    }

    public String getSelectedText() {
        int n = Math.min(this.cursorPosition, this.selectionEnd);
        int n2 = Math.max(this.cursorPosition, this.selectionEnd);
        return this.text.substring(n, n2);
    }

    public void writeText(String string) {
        int n;
        String string2 = "";
        String string3 = ChatAllowedCharacters.filerAllowedCharacters(string);
        int n2 = Math.min(this.cursorPosition, this.selectionEnd);
        int n3 = Math.max(this.cursorPosition, this.selectionEnd);
        int n4 = this.maxStringLength - this.text.length() - (n2 - this.selectionEnd);
        boolean bl = false;
        if (this.text.length() > 0) {
            string2 = string2 + this.text.substring(0, n2);
        }
        if (n4 < string3.length()) {
            string2 = string2 + string3.substring(0, n4);
            n = n4;
        } else {
            string2 = string2 + string3;
            n = string3.length();
        }
        if (this.text.length() > 0 && n3 < this.text.length()) {
            string2 = string2 + this.text.substring(n3);
        }
        this.text = string2;
        this.moveCursorBy(n2 - this.selectionEnd + n);
    }

    public void deleteWords(int n) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(n) - this.cursorPosition);
            }
        }
    }

    public void deleteFromCursor(int n) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                boolean bl = n < 0;
                int n2 = bl ? this.cursorPosition + n : this.cursorPosition;
                int n3 = bl ? this.cursorPosition : this.cursorPosition + n;
                String string = "";
                if (n2 >= 0) {
                    string = this.text.substring(0, n2);
                }
                if (n3 < this.text.length()) {
                    string = string + this.text.substring(n3);
                }
                this.text = string;
                if (bl) {
                    this.moveCursorBy(n);
                }
            }
        }
    }

    public int getNthWordFromCursor(int n) {
        return this.getNthWordFromPos(n, this.getCursorPosition());
    }

    public int getNthWordFromPos(int n, int n2) {
        return this.func_146197_a(n, this.getCursorPosition(), true);
    }

    public int func_146197_a(int n, int n2, boolean bl) {
        int n3 = n2;
        boolean bl2 = n < 0;
        int n4 = Math.abs(n);
        for (int i = 0; i < n4; ++i) {
            if (bl2) {
                while (bl && n3 > 0 && this.text.charAt(n3 - 1) == ' ') {
                    --n3;
                }
                while (n3 > 0 && this.text.charAt(n3 - 1) != ' ') {
                    --n3;
                }
                continue;
            }
            int n5 = this.text.length();
            if ((n3 = this.text.indexOf(32, n3)) == -1) {
                n3 = n5;
                continue;
            }
            while (bl && n3 < n5 && this.text.charAt(n3) == ' ') {
                ++n3;
            }
        }
        return n3;
    }

    public void moveCursorBy(int n) {
        this.setCursorPosition(this.selectionEnd + n);
    }

    public void setCursorPosition(int n) {
        this.cursorPosition = n;
        int n2 = this.text.length();
        if (this.cursorPosition < 0) {
            this.cursorPosition = 0;
        }
        if (this.cursorPosition > n2) {
            this.cursorPosition = n2;
        }
        this.setSelectionPos(this.cursorPosition);
    }

    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }

    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }
    public void textboxKeyTyped(char c, int modifiers) {
        if (!this.isFocused) {
            return;
        }

        boolean ctrl = Keyboard.isCtrlKeyDown();
        boolean shift = Keyboard.isShiftKeyDown();

        switch (Character.toLowerCase(c)) {
            case 'a':
                if (ctrl) {
                    this.setCursorPositionEnd();
                    this.setSelectionPos(0);
                    return;
                }
                break;
            case 'c':
                if (ctrl) {
                    Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
                    return;
                }
                break;
            case 'v':
                if (ctrl && this.isEnabled) {
                    this.writeText(Minecraft.getInstance().keyboardHandler.getClipboard());
                    return;
                }
                break;
            case 'x':
                if (ctrl) {
                    Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
                    if (this.isEnabled) {
                        this.writeText("");
                    }
                    return;
                }
                break;
        }

        switch (Character.toLowerCase(c)) {
            case GLFW.GLFW_KEY_BACKSPACE:
                if (this.isEnabled) {
                    if (ctrl) this.deleteWords(-1);
                    else this.deleteFromCursor(-1);
                }
                return;
            case GLFW.GLFW_KEY_HOME:
                if (shift) this.setSelectionPos(0);
                else this.setCursorPositionZero();
                return;
            case GLFW.GLFW_KEY_LEFT:
                if (shift) {
                    if (ctrl)
                        this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
                    else
                        this.setSelectionPos(this.getSelectionEnd() - 1);
                } else if (ctrl) {
                    this.setCursorPosition(this.getNthWordFromCursor(-1));
                } else {
                    this.moveCursorBy(-1);
                }
                return;
            case GLFW.GLFW_KEY_RIGHT:
                if (shift) {
                    if (ctrl)
                        this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
                    else
                        this.setSelectionPos(this.getSelectionEnd() + 1);
                } else if (ctrl) {
                    this.setCursorPosition(this.getNthWordFromCursor(1));
                } else {
                    this.moveCursorBy(1);
                }
                return;
            case GLFW.GLFW_KEY_END:
                if (shift) this.setSelectionPos(this.text.length());
                else this.setCursorPositionEnd();
                return;
            case GLFW.GLFW_KEY_DELETE:
                if (this.isEnabled) {
                    if (ctrl) this.deleteWords(1);
                    else this.  deleteFromCursor(1);
                }
                return;
        }
        if (ChatAllowedCharacters.isAllowedCharacter(c)) {
            if (this.isEnabled) {
                this.writeText(Character.toString(c));
            }
        }
    }

    @Override
    public boolean handleElementMouseClicked(float f, float f2, int n, boolean bl) {
        boolean bl2;
        if (!bl) {
            this.setFocused(false);
            return true;
        }
        if (n == 1 && this.isMouseInside(f, f2)) {
            this.setText("");
        }
        boolean bl3 = bl2 = f >= this.x && f < this.x + this.width && f2 >= this.y && f2 < this.y + this.height;

        if (this.canLoseFocus) {
            this.setFocused(bl2);
        }
        if (this.isFocused && n == 0) {
            float f3 = f - this.x;
            if (this.enableBackgroundDrawing) {
                f3 -= (float)4;
            }
            String string = font.plainSubstrByWidth(this.text.substring(this.lineScrollOffset), (int) this.IIIIIIlIlIlIllllllIlllIlI());
            this.setCursorPosition(font.plainSubstrByWidth(string, (int) f3).length() + this.lineScrollOffset);
        }
        return false;
    }

    public void drawElement(GuiGraphicsExtractor gfx) {
        String string = this.font.plainSubstrByWidth(this.text.substring(this.lineScrollOffset), (int) this.IIIIIIlIlIlIllllllIlllIlI());
        if (this.getVisible()) {
            if (this.getEnableBackgroundDrawing()) {
                RenderUtil.drawRect(gfx, this.x, this.y, this.x + this.width, this.y + this.height, this.color1);
            }
            int n = this.isEnabled ? this.enabledColor : this.disabledColor;
            int n2 = this.cursorPosition - this.lineScrollOffset;
            int n3 = this.selectionEnd - this.lineScrollOffset;
            boolean bl = n2 >= 0 && n2 <= string.length();
            boolean bl2 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && bl;
            float f = this.enableBackgroundDrawing ? this.x + (float)4 : this.x;
            float f2 = this.enableBackgroundDrawing ? this.y + (this.height - (float)8) / 2.0f : this.y;
            float f3 = f;
            if (n3 > string.length()) {
                n3 = string.length();
            }
            if (!string.isEmpty()) {
                String string2 = bl ? string.substring(0, n2) : string;
                RenderUtil.drawString(gfx, font, string2, f, f2, -1);
                f3 = font.width(string2) + f;
            } else if (!this.isFocused()) {
                RenderUtil.drawString(gfx, font, label, f, f2, -1);
            }
            boolean bl3 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            float f4 = f3;
            if (!bl) {
                f4 = n2 > 0 ? f + this.width : f;
            } else if (bl3) {
                f4 = f3 - 1.0f;
                f3 -= 1.0f;
            }
            if (!string.isEmpty() && bl && n2 < string.length()) {
                RenderUtil.drawString(gfx, font, string.substring(n2), f3, f2, n);
            }
            if (bl2) {
                if (bl3) {
                    RenderUtil.drawRect(gfx, f4, f2 - 1.0f, f4 + 1.0f, f2 + 1.0f + this.font.height(), -3092272);
                } else {
                    RenderUtil.drawString(gfx, font, "_", f4, f2, n);
                }
            }
            if (n3 != n2) {
                float f5 = f + (float)this.font.width(string.substring(0, n3));
                this.drawCursorVertical(gfx, f4, f2 - 1.0f, f5 - 1.0f, f2 + 1.0f + this.font.height() + 2.0f);
            }
        }
    }

    private void drawCursorVertical(GuiGraphicsExtractor gfx, float x1, float y1, float x2, float y2) {
        if (x1 > x2) {
            float t = x1;
            x1 = x2;
            x2 = t;
        }
        if (y1 > y2) {
            float t = y1;
            y1 = y2;
            y2 = t;
        }

        float left = this.x;
        float right = this.x + this.width;

        if (x1 < left)  x1 = left;
        if (x2 > right) x2 = right;

        int color = 0x8066A8FF;

        RenderUtil.drawRect(gfx, x1, y1, x2, y2, color);
    }

    public void setMaxStringLength(int limit) {
        this.maxStringLength = limit;
        if (this.text.length() > limit) {
            this.text = this.text.substring(0, limit);
        }
    }

    public int getMaxStringLength() {
        return this.maxStringLength;
    }

    public int getCursorPosition() {
        return this.cursorPosition;
    }

    public boolean getEnableBackgroundDrawing() {
        return this.enableBackgroundDrawing;
    }

    public void setEnableBackgroundDrawing(boolean bl) {
        this.enableBackgroundDrawing = bl;
    }

    public void setTextColor(int color) {
        this.enabledColor = color;
    }

    public void setDisabledTextColour(int color) {
        this.disabledColor = color;
    }

    public void setFocused(boolean bl) {
        if (bl && !this.isFocused) {
            this.cursorCounter = 0;
        }
        this.isFocused = bl;
    }

    public boolean isFocused() {
        return this.isFocused;
    }

    public void setEnabled(boolean bl) {
        this.isEnabled = bl;
    }

    public int getSelectionEnd() {
        return this.selectionEnd;
    }

    public float IIIIIIlIlIlIllllllIlllIlI() {
        return this.getEnableBackgroundDrawing() ? this.width - (float)8 : this.width;
    }

    public void setSelectionPos(int n) {
        int n2 = this.text.length();
        if (n > n2) {
            n = n2;
        }
        if (n < 0) {
            n = 0;
        }
        this.selectionEnd = n;
        if (this.font != null) {
            if (this.lineScrollOffset > n2) {
                this.lineScrollOffset = n2;
            }
            float f = this.IIIIIIlIlIlIllllllIlllIlI();
            String string = this.font.plainSubstrByWidth(this.text.substring(this.lineScrollOffset), (int) f);
            int n3 = string.length() + this.lineScrollOffset;
            if (n == this.lineScrollOffset) {
                this.lineScrollOffset -= this.font.plainSubstrByWidth(this.text, (int) f, true).length();
            }
            if (n > n3) {
                this.lineScrollOffset += n - n3;
            } else if (n <= this.lineScrollOffset) {
                this.lineScrollOffset -= this.lineScrollOffset - n;
            }
            if (this.lineScrollOffset < 0) {
                this.lineScrollOffset = 0;
            }
            if (this.lineScrollOffset > n2) {
                this.lineScrollOffset = n2;
            }
        }
    }

    public void setCanLoseFocus(boolean bl) {
        this.canLoseFocus = bl;
    }

    public boolean getVisible() {
        return this.visible;
    }

    public void setVisible(boolean bl) {
        this.visible = bl;
    }

    @Override
    public void handleElementUpdate() {
        this.updateCursorCounter();
    }

    @Override
    public void handleElementDraw(GuiGraphicsExtractor gfx, float mouseX, float mouseY, boolean delta) {
        this.drawElement(gfx);
    }

    @Override
    public void handleCharInput(char c, int modifiers) {
        this.textboxKeyTyped(c, modifiers);
    }

    @Override
    public void handleElementKeyTyped(int keyCode, int scanCode, int modifiers) {
        if (Keyboard.isCtrlKeyDown()) {
            this.textboxKeyTyped((char) keyCode, modifiers);
            return;
        }

        switch (keyCode) {
            case GLFW.GLFW_KEY_BACKSPACE:
            case GLFW.GLFW_KEY_DELETE:
            case GLFW.GLFW_KEY_HOME:
            case GLFW.GLFW_KEY_END:
            case GLFW.GLFW_KEY_LEFT:
            case GLFW.GLFW_KEY_RIGHT:
                this.textboxKeyTyped((char) keyCode, modifiers);
                break;
        }
    }
}
