package cc.vops.cheatbreaker.client.ui.overlay.element;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.mainmenu.AbstractElement;
import cc.vops.cheatbreaker.client.ui.mainmenu.element.ScrollableElement;
import cc.vops.cheatbreaker.client.ui.overlay.SocialOverlayScreen;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.Mouse;
import cc.vops.cheatbreaker.client.util.PlayerHeads;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import cc.vops.cheatbreaker.client.util.friend.Friend;
import cc.vops.cheatbreaker.client.websocket.shared.WSPacketMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class MessagesElement extends DraggableElement {
    private Friend friend;
    private final InputFieldElement inputFieldElement;
    private final FlatButtonElement sendButton;
    private final ScrollableElement messageListScrollable;
    private final ScrollableElement recentsScrollable;
    private final FlatButtonElement aliasesButton;
    private final FlatButtonElement closeButton;
    private final int widthElements = 22;

    public MessagesElement(Friend friend) {
        this.friend = friend;
        this.inputFieldElement = new InputFieldElement(Fonts.playRegular16, "Message", 0x2FFFFFFF, 0x6FFFFFFF);
        this.inputFieldElement.setMaxStringLength(256);
        this.sendButton = new FlatButtonElement("SEND");
        this.messageListScrollable = new ScrollableElement(this);
        this.recentsScrollable = new ScrollableElement(this);
        this.aliasesButton = new FlatButtonElement("Invite");
        this.closeButton = new FlatButtonElement("X");
    }

    @Override
    public void handleCharInput(char typedChar, int keyCode) {
        this.inputFieldElement.handleCharInput(typedChar, keyCode);
    }

    @Override
    public void setElementSize(float x, float y, float width, float height) {
        super.setElementSize(x, y, width, height);
        this.inputFieldElement.setElementSize(x + 26f, y + height - 15f, width - 62f, 13);
        this.sendButton.setElementSize(x + width - 37f, y + height - 15f, 35f, 13);
        this.messageListScrollable.setElementSize(x + width - 6f, y + 22f, 4f, height - 39f);
        this.recentsScrollable.setElementSize(x + 2.0f, y + 2.0f, 2.0f, height - 4f);
        this.aliasesButton.setElementSize(x + width - 54f, y + 2.0f, 40f, 16);
        this.closeButton.setElementSize(x + width - 12f, y + 2.0f, 10f, 16);
    }

    public static String lIIIIlIIllIIlIIlIIIlIIllI(byte[] arrby) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(CheatBreaker.processBytesAuth, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(2, secretKeySpec);
        return new String(cipher.doFinal(arrby));
    }

    @Override
    public void handleElementDraw(GuiGraphicsExtractor gfx, float mouseX, float mouseY, boolean bl) {
        String[] restring;
        this.drag(mouseX, mouseY);
        RenderUtil.drawBoxWithOutLine(gfx, this.x, this.y, this.x + (float)23, this.y + this.height, 0.074324325f * 6.7272725f, -16777216, -14869219);
        RenderUtil.drawBoxWithOutLine(gfx, this.x + (float)23, this.y, this.x + this.width, this.y + this.height, 0.7132353f * 0.7010309f, -16777216, -15395563);
//        GL11.glPushMatrix();
        gfx.pose().pushMatrix();
        RenderUtil.drawRect(gfx, this.x + (float)25, this.y - 1.9285715f * 0.25925925f, this.x + this.width, this.y, -1357572843);
        RenderUtil.drawRect(gfx, this.x + (float)25, this.y + this.height, this.x + this.width, this.y + this.height + 0.25f * 2.0f, -1357572843);
        RenderUtil.drawRect(gfx, this.x + (float)27, this.y + (float)3, this.x + (float)43, this.y + (float)19, this.friend.isOnline() ? Friend.getStatusColor(this.friend.getOnlineStatus()) : -13158601);
//        CheatBreaker.getInstance().playRegular16px.drawString(this.friend.getName(), this.x + (float)52, this.y + 2.0f, -1);
//        CheatBreaker.getInstance().playRegular16px.drawString(this.friend.getStatusString(), this.x + (float)52, this.y + (float)11, -5460820);
        RenderUtil.drawString(gfx, Fonts.playRegular16, this.friend.getName(), this.x + 52.0f, this.y + 2.0f, -1);
        RenderUtil.drawString(gfx, Fonts.playRegular16, this.friend.getStatusString(), this.x + 52.0f, this.y + 11.0f, -5460820);


//        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        Identifier Identifier = PlayerHeads.getHeadLocation(ChatFormatting.stripFormatting(this.friend.getName()), this.friend.getPlayerUUID());
        RenderUtil.drawIcon(gfx, Identifier, (float)7, this.x + (float)28, this.y + (float)4);
        RenderUtil.drawRect(gfx, this.x + (float) 27, this.y + (float) 22, this.x + this.width - 2.0f, this.y + this.height - (float) 17, -1356783327);
        this.recentsScrollable.drawScrollable(gfx, mouseX, mouseY, bl);
//        GL11.glPushMatrix();
//        GL11.glEnable(3089);
        gfx.pose().pushMatrix();
        SocialOverlayScreen overlayGui = SocialOverlayScreen.getInstance();
        gfx.enableScissor(0, (int)(this.y + 2.0f), (int) overlayGui.getScaledWidth(), (int)(this.y + this.height - 2.0f));
        int n = 18;
        int n2 = 0;
        for (Friend friend : this.client.getFriendsManager().getFriends().values()) {
            if (friend != this.friend && !this.client.getFriendsManager().getMessages().containsKey(friend.getPlayerId()) && !friend.isOnline()) continue;
            float f3 = this.y + (float)3 + (float)n2;
            boolean bl2 = mouseX > this.x && mouseX < this.x + (float)25 && mouseY > f3 - this.recentsScrollable.IllIIIIIIIlIlIllllIIllIII() && mouseY < f3 + (float)16 - this.recentsScrollable.IllIIIIIIIlIlIllllIIllIII() && mouseY > this.y && mouseY < this.y + this.height;
            RenderUtil.drawRect(gfx, this.x + (float)3, f3, this.x + (float)19, f3 + (float)16, friend.isOnline() ? Friend.getStatusColor(friend.getOnlineStatus()) : -13158601);
//            GL11.glColor4f(1.0f, 1.0f, 1.0f, bl2 ? 1.0f : 0.6016854f * 1.4126984f);
            Identifier location = PlayerHeads.getHeadLocation(ChatFormatting.stripFormatting(friend.getName()), friend.getPlayerUUID());
            RenderUtil.drawIcon(gfx, location, (float)7, this.x + (float)4, this.y + (float)4 + (float)n2, CheatBreaker.getColor(1.0f, 1.0f, 1.0f, bl2 ? 1.0f : 0.6016854f * 1.4126984f));
            if (bl2) {
//                float f4 = this.client.playRegular16px.getStringWidth(ChatColor.getTextWithoutFormattingCodes(friend.getName()));
                float f4 = Fonts.playRegular16.width(ChatFormatting.stripFormatting(friend.getName()));
                RenderUtil.drawRoundedRect(gfx, this.x - (float)10 - f4, f3 + 2.0f, this.x - 2.0f, f3 + (float)14, (double)6, -1895825408);
//                this.client.playRegular16px.drawString(friend.getName(), this.x - (float)6 - f4, f3 + (float)4, -1);
                RenderUtil.drawString(gfx, Fonts.playRegular16, friend.getName(), this.x - 6.0f - f4, f3 + 4.0f, -1);
                if (Mouse.isButtonDown(0) && this.friend != friend) {
                    CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
                    this.friend = friend;
                }
            }
            n2 += 18;
        }
        this.recentsScrollable.setScrollAmount(n2);
//        GL11.glDisable(3089);
//        GL11.glPopMatrix();
        gfx.pose().popMatrix();
        this.recentsScrollable.handleElementDraw(gfx, mouseX, mouseY, bl);
        this.messageListScrollable.handleScrollableMouseClicked(gfx, mouseX, mouseY, bl);
        try {
            if (CheatBreaker.getInstance().getFriendsManager().getMessages().containsKey(this.friend.getPlayerId())) {
//                GL11.glPushMatrix();
//                GL11.glEnable(3089);
                gfx.pose().pushMatrix();
                gfx.enableScissor((int)(this.x + 2.0f), (int)(this.y + (float)22), (int)(this.x + this.width - 2.0f), (int)(this.y + this.height - (float)17));
                List<String> messages = CheatBreaker.getInstance().getFriendsManager().getMessages().get(this.friend.getPlayerId());
                int n3 = 0;
                for (int messageIndex = messages.size() - 1; messageIndex >= 0; --messageIndex) {
                    String message = messages.get(messageIndex);
                    restring = Fonts.playRegular16.plainSubstrByWidth(message, (int) (this.width - 25.0f)).split("\n");
                    n3 += restring.length * 10;
                    int n4 = 0;
                    for (String string2 : restring) {
//                        CheatBreaker.getInstance().playRegular16px.drawString(string2, this.x + (float)31, this.y + this.height - (float)19 - (float)n3 + (float)(n4 * 10), -1);
                        RenderUtil.drawString(gfx, Fonts.playRegular16, string2, this.x + 31.0f, this.y + this.height - 19.0f - (float)n3 + (float)(n4 * 10), -1);
                        ++n4;
                    }
                }
                this.messageListScrollable.setScrollAmount(n3 + 4);
//                GL11.glDisable(3089);
//                GL11.glPopMatrix();
                gfx.disableScissor();
                gfx.pose().popMatrix();
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        this.messageListScrollable.scrollableOnMouseClick(gfx, mouseX, mouseY, bl);
//        GL11.glPopMatrix();
        gfx.disableScissor();
        gfx.pose().popMatrix();
        gfx.pose().popMatrix();
        this.inputFieldElement.drawElement(gfx, mouseX, mouseY, bl);
        this.sendButton.drawElement(gfx, mouseX, mouseY, bl);
        this.closeButton.drawElement(gfx, mouseX, mouseY, bl);

        if (Minecraft.getInstance().getCurrentServer() != null) {
            this.aliasesButton.drawElement(gfx, mouseX, mouseY, bl);
        }
    }

    @Override
    public void handleElementUpdate() {
        this.inputFieldElement.handleElementUpdate();
        this.sendButton.handleElementUpdate();
        this.messageListScrollable.handleElementUpdate();
        this.closeButton.handleElementUpdate();

        if (Minecraft.getInstance().getCurrentServer() != null) {
            this.aliasesButton.handleElementUpdate();
        }
    }

    @Override
    public void handleElementClose() {
        this.inputFieldElement.handleElementClose();
        this.sendButton.handleElementClose();
        this.messageListScrollable.handleElementClose();
        this.aliasesButton.handleElementClose();
        this.closeButton.handleElementClose();
    }

    @Override
    public void handleElementKeyTyped(int keyCode, int scanCode, int modifiers) {
        if (this.inputFieldElement.isFocused() && !this.inputFieldElement.getText().equals("") && keyCode == GLFW.GLFW_KEY_ENTER) {
            this.sendMessage();
        }
        this.inputFieldElement.handleElementKeyTyped(keyCode, scanCode, modifiers);
        this.sendButton.handleElementKeyTyped(keyCode, scanCode, modifiers);
        this.messageListScrollable.handleElementKeyTyped(keyCode, scanCode, modifiers);
        this.closeButton.handleElementKeyTyped(keyCode, scanCode, modifiers);

        if (Minecraft.getInstance().getCurrentServer() != null) {
            this.aliasesButton.handleElementKeyTyped(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean handleMouseClickedInternal(float f, float f2, int n) {
        if (!this.inputFieldElement.isMouseInside(f, f2) && this.inputFieldElement.isFocused()) {
            this.inputFieldElement.setFocused(false);
        }
        return false;
    }

    @Override
    public boolean handleElementMouseClicked(float mouseX, float mouseY, int n, boolean bl) {
        this.inputFieldElement.handleElementMouseClicked(mouseX, mouseY, n, bl);
        if (!bl) {
            return false;
        }
        if (!this.inputFieldElement.getText().equals("") && this.sendButton.isMouseInside(mouseX, mouseY)) {
            this.sendMessage();
        }
        this.sendButton.handleElementMouseClicked(mouseX, mouseY, n, true);
        this.messageListScrollable.handleElementMouseClicked(mouseX, mouseY, n, true);

        boolean showAliasesButton = Minecraft.getInstance().getCurrentServer() != null;

        if (showAliasesButton) {
            this.aliasesButton.handleElementMouseClicked(mouseX, mouseY, n, true);
        }

        if ((!showAliasesButton || !this.aliasesButton.isMouseInside(mouseX, mouseY)) && this.isMouseInside(mouseX, mouseY) && mouseY < this.y + (float)22) {
            this.updateDraggingPosition(mouseX, mouseY);
        }


        if (this.closeButton.isMouseInside(mouseX, mouseY)) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            SocialOverlayScreen.getInstance().removeElements(this);
            return true;
        }
        if (this.aliasesButton.isMouseInside(mouseX, mouseY) && showAliasesButton) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            // TODO invite to server
//            AbstractElement[] abstractElements = new AbstractElement[1];
//            AliasesElement aliasesElement = new AliasesElement(this.friend);
//            abstractElements[0] = aliasesElement;
//            SocialOverlayScreen.getInstance().addElements(abstractElements);
//            aliasesElement.setElementSize((float)60, (float)30, (float)140, 30);
            return true;
        }
        return false;
    }

    @Override
    public void handleElementMouse() {
        this.messageListScrollable.handleElementMouse();
        this.recentsScrollable.handleElementMouse();
    }

    private void sendMessage() {
        String message = this.inputFieldElement.getText();
        CheatBreaker.getInstance().getFriendsManager().addOutgoingMessage(this.friend.getPlayerId(), message);
        CheatBreaker.getInstance().getAssetsWebSocket().send(new WSPacketMessage(this.friend.getPlayerId(), message));
        this.inputFieldElement.setText("");
        CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
    }

    @Override
    public boolean handleElementMouseRelease(float f, float f2, int n, boolean bl) {
        if (!bl) {
            return false;
        }
        this.inputFieldElement.handleElementMouseRelease(f, f2, n, true);
        this.sendButton.handleElementMouseRelease(f, f2, n, true);
        this.messageListScrollable.handleElementMouseRelease(f, f2, n, true);
        this.aliasesButton.handleElementMouseRelease(f, f2, n, true);
        this.closeButton.handleElementMouseRelease(f, f2, n, true);
        return false;
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }

    public Friend getFriend() {
        return this.friend;
    }

    public InputFieldElement getInputField() {
        return this.inputFieldElement;
    }
}
