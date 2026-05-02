package cc.vops.cheatbreaker.client.ui.overlay;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.mainmenu.element.ScrollableElement;
import cc.vops.cheatbreaker.client.ui.overlay.element.DraggableElement;
import cc.vops.cheatbreaker.client.ui.overlay.element.FlatButtonElement;
import cc.vops.cheatbreaker.client.ui.overlay.element.InputFieldElement;
import cc.vops.cheatbreaker.client.util.ChatColor;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import cc.vops.cheatbreaker.client.websocket.shared.WSPacketConsole;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ConsoleElement extends DraggableElement {
    private final InputFieldElement textInputElement;
    private final FlatButtonElement sentButton;
    private final ScrollableElement scrollableElement;
    private final FlatButtonElement closeButton;

    public ConsoleElement() {
        this.textInputElement = new InputFieldElement(Fonts.playRegular16, "", 0x2FFFFFFF, 0x6FFFFFFF);
        this.textInputElement.setMaxStringLength(256);
        this.sentButton = new FlatButtonElement("SEND");
        this.scrollableElement = new ScrollableElement(this);
        this.closeButton = new FlatButtonElement("X");
    }

    @Override
    public void setElementSize(float x, float y, float width, float height) {
        super.setElementSize(x, y, width, height);
        this.textInputElement.setElementSize(x + 2.0f, y + height - (float)15, width - (float)40, 13);
        this.sentButton.setElementSize(x + width - (float)37, y + height - (float)15, (float)35, 13);
        this.scrollableElement.setElementSize(x + width - (float)6, y + (float)12 + (float)3, (float)4, height - (float)32);
        this.closeButton.setElementSize(x + width - (float)12, y + 2.0f, (float)10, 10);
    }

    @Override
    public void handleElementDraw(GuiGraphicsExtractor gfx, float mouseX, float mouseY, boolean bl) {
        this.drag(mouseX, mouseY);
        RenderUtil.drawBoxWithOutLine(gfx, this.x, this.y, this.x + this.width, this.y + this.height, 0.2972973f * 1.6818181f, -16777216, -15395563);
//        GL11.glPushMatrix();
        gfx.pose().pushMatrix();
        RenderUtil.drawRect(gfx, this.x, this.y - 0.25f * 2.0f, this.x + this.width, this.y, -1357572843);
        RenderUtil.drawRect(gfx, this.x, this.y + this.height, this.x + this.height, this.y + this.height + 0.8961039f * 0.557971f, -1357572843);
//        CheatBreaker.getInstance().playRegular16px.drawString("Console", this.x + (float)4, this.y + (float)3, -1);
        RenderUtil.drawString(gfx, Fonts.playRegular16, "Console", this.x + (float)4, this.y + (float)3, -1);

        RenderUtil.drawRect(gfx, this.x + 2.0f, this.y + (float)12 + (float)3, this.x + this.width - 2.0f, this.y + this.height - (float)17, -1356783327);
        try {
            if (CheatBreaker.getInstance().isConsoleAllowed()) {
//                GL11.glPushMatrix();
//                GL11.glEnable((int)3089);
                gfx.pose().pushMatrix();
                List<String> lines = CheatBreaker.getInstance().getConsoleLines();
                gfx.enableScissor(
                        (int)(this.x + 2.0f),
                        (int)(this.y + (float)12 + (float)3),
                        (int)(this.x + this.width - 2.0f),
                        (int)(this.y + this.height - (float)17)
                );
                this.scrollableElement.handleScrollableMouseClicked(gfx, mouseX, mouseY, bl);
                int totalHeight = 0;

                for (int i = lines.size() - 1; i >= 0; i--) {
                    String raw = lines.get(i);
//                    String[] wrapped = ChatColor.formatText(Fonts.playRegular16, raw, this.width - 10).split("\n");
                    String[] wrapped = raw.split("\n");

                    totalHeight += wrapped.length * 10;

                    for (int j = 0; j < wrapped.length; j++) {
                        float drawY = this.y + this.height - 19 - totalHeight + (j * 10);

                        RenderUtil.drawString(gfx, Fonts.playRegular16,
                                wrapped[j],
                                this.x + 6,
                                drawY,
                                -1);
                    }
                }
                this.scrollableElement.setScrollAmount(totalHeight + 4);
                gfx.disableScissor();
                gfx.pose().popMatrix();
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        this.scrollableElement.scrollableOnMouseClick(gfx, mouseX, mouseY, bl);
//        GL11.glPopMatrix();
        gfx.pose().popMatrix();
        this.textInputElement.drawElement(gfx, mouseX, mouseY, bl);
        this.sentButton.drawElement(gfx, mouseX, mouseY, bl);
        this.closeButton.drawElement(gfx, mouseX, mouseY, bl);
    }

    @Override
    public void handleCharInput(char typedChar, int keyCode) {
        this.textInputElement.handleCharInput(typedChar, keyCode);
    }

    @Override
    public void handleScroll(GuiGraphicsExtractor gfx, int delta) {
        this.scrollableElement.handleScroll(gfx, delta);
    }

    @Override
    public void handleElementUpdate() {
        this.textInputElement.handleElementUpdate();
        this.sentButton.handleElementUpdate();
        this.scrollableElement.handleElementUpdate();
        this.closeButton.handleElementUpdate();
    }

    @Override
    public void handleElementClose() {
        this.textInputElement.handleElementClose();
        this.sentButton.handleElementClose();
        this.scrollableElement.handleElementClose();
        this.closeButton.handleElementClose();
    }

    @Override
    public void handleElementKeyTyped(int keyCode, int scanCode, int modifiers) {
        if (this.textInputElement.isFocused() && !this.textInputElement.getText().equals("") && keyCode == GLFW.GLFW_KEY_ENTER) {
            this.sendCommandToServer();
        }
        this.textInputElement.handleElementKeyTyped(keyCode, scanCode, modifiers);
        this.sentButton.handleElementKeyTyped(keyCode, scanCode, modifiers);
        this.scrollableElement.handleElementKeyTyped(keyCode, scanCode, modifiers);
        this.closeButton.handleElementKeyTyped(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean handleMouseClickedInternal(float f, float f2, int n) {
        if (!this.textInputElement.isMouseInside(f, f2) && this.textInputElement.isFocused()) {
            this.textInputElement.setFocused(false);
        }
        return false;
    }
    @Override
    public boolean handleElementMouseClicked(float f, float f2, int n, boolean bl) {
        this.textInputElement.handleElementMouseClicked(f, f2, n, bl);
        this.scrollableElement.handleElementMouseClicked(f, f2, n, bl);
        if (!bl) {
            return false;
        }
        if (!this.textInputElement.getText().equals("") && this.sentButton.isMouseInside(f, f2)) {
            this.sendCommandToServer();
        }
        this.sentButton.handleElementMouseClicked(f, f2, n, true);
        if (this.isMouseInside(f, f2) && f2 < this.y + (float)12) {
            this.updateDraggingPosition(f, f2);
        }
        if (this.closeButton.isMouseInside(f, f2)) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            SocialOverlayScreen.getInstance().removeElements(this);
            return true;
        }
        return false;
    }

    @Override
    public void handleElementMouse() {
        this.scrollableElement.handleElementMouse();
    }

    private void sendCommandToServer() {
        String string = this.textInputElement.getText();
        if (string.equals("clear") || string.equals("cls")) {
            CheatBreaker.getInstance().getConsoleLines().clear();
        } else {
            CheatBreaker.getInstance().getConsoleLines().add(ChatColor.GRAY + "> " + string);
            CheatBreaker.getInstance().getAssetsWebSocket().send(new WSPacketConsole(string));
        }
        this.textInputElement.setText("");
        CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
    }

    @Override
    public boolean handleElementMouseRelease(float f, float f2, int n, boolean bl) {
        if (!bl) {
            return false;
        }
        this.textInputElement.handleElementMouseRelease(f, f2, n, true);
        this.sentButton.handleElementMouseRelease(f, f2, n, true);
        this.scrollableElement.handleElementMouseRelease(f, f2, n, true);
        this.closeButton.handleElementMouseRelease(f, f2, n, true);
        return false;
    }

    public InputFieldElement getTextInputElement() {
        return this.textInputElement;
    }
}
