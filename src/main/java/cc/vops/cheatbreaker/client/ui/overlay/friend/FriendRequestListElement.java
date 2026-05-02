package cc.vops.cheatbreaker.client.ui.overlay.friend;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.mainmenu.element.ScrollableElement;
import cc.vops.cheatbreaker.client.ui.overlay.Alert;
import cc.vops.cheatbreaker.client.ui.overlay.SocialOverlayScreen;
import cc.vops.cheatbreaker.client.ui.overlay.element.ElementListElement;
import cc.vops.cheatbreaker.client.ui.overlay.element.FlatButtonElement;
import cc.vops.cheatbreaker.client.ui.overlay.element.InputFieldElement;
import cc.vops.cheatbreaker.client.util.ChatColor;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import cc.vops.cheatbreaker.client.websocket.client.WSPacketClientRequestsStatus;
import cc.vops.cheatbreaker.client.websocket.shared.WSPacketFriendRequest;
import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestListElement extends ElementListElement<FriendRequestElement> {
    private final InputFieldElement filter;
    private final InputFieldElement username;
    private final FlatButtonElement addButton;
    private final FlatButtonElement toggleRequests;
    private final ScrollableElement scrollableElement;
    private final List<FriendRequestElement> friendRequestElements = new ArrayList<>();

    public FriendRequestListElement(List<FriendRequestElement> list) {
        super(list);
        this.filter = new InputFieldElement(Fonts.playRegular16, "Filter", 0x2FFFFFFF, 0x6FFFFFFF);
        this.username = new InputFieldElement(Fonts.playRegular16, "Username", 0x2FFFFFFF, 0x6FFFFFFF);
        this.addButton = new FlatButtonElement("ADD");
        this.toggleRequests = new FlatButtonElement("");
        this.scrollableElement = new ScrollableElement(this);
    }

    @Override
    public void handleCharInput(char typedChar, int keyCode) {
        super.handleCharInput(typedChar, keyCode);
        this.filter.handleCharInput(typedChar, keyCode);
        this.username.handleCharInput(typedChar, keyCode);
    }

    public void resetSize() {
        this.setElementSize(this.x, this.y, this.width, this.height);
    }

    @Override
    public void setElementSize(float x, float y, float width, float height) {
        super.setElementSize(x, y, width, height);
        this.scrollableElement.setElementSize(x + width - (float)4, y, (float)4, height);
        int n = 22;
        int n2 = 0;
        for (FriendRequestElement friendRequestElement : this.elements) {
            if (!this.isFilterMatch(friendRequestElement)) continue;
            friendRequestElement.setElementSize(x, y + (float)14 + (float)(n2 * 22), width, 22);
            ++n2;
        }

        float f5 = 14 + this.elements.size() * 22 + 30;
        if (f5 < height) {
            f5 = height;
        }

        this.filter.setElementSize(0.0f, y, width, 13);
        this.username.setElementSize(0.0f, y + f5 - (float)13, width - (float)35, 13);
        this.addButton.setElementSize(width - (float)35, y + f5 - (float)13, (float)35, 13);
        this.toggleRequests.setElementSize(0.0f, y + f5 - (float)26, width, 13);
        this.scrollableElement.setScrollAmount(f5);
    }

    private boolean isFilterMatch(FriendRequestElement friendRequestElement) {
        return this.filter.getText().equals("") || ChatFormatting.stripFormatting(friendRequestElement.getFriendRequest().getUsername()).toLowerCase().startsWith(this.filter.getText().toLowerCase());
    }

    @Override
    public void handleElementDraw(GuiGraphicsExtractor gfx, float f, float f2, boolean bl) {
        if (!this.friendRequestElements.isEmpty()) {
            this.elements.removeAll(this.friendRequestElements);
            SocialOverlayScreen.getInstance().getFriendRequestsElement().resetSize();
            this.friendRequestElements.clear();
        }
        if (!CheatBreaker.getInstance().getAssetsWebSocket().isOpen()) {
//            CheatBreaker.getInstance().playRegular16px.drawCenteredString("Connection lost", this.x + this.width / 2.0f, this.y + (float)10, -1);
//            CheatBreaker.getInstance().playRegular16px.drawCenteredString("Please try again later.", this.x + this.width / 2.0f, this.y + (float)22, -1);
            RenderUtil.drawCenteredString(gfx, Fonts.playRegular16, "Connection lost", this.x + this.width / 2.0f, this.y + (float)10, -1);
            RenderUtil.drawCenteredString(gfx, Fonts.playRegular16, "Please try again later.", this.x + this.width / 2.0f, this.y + (float)22, -1);
        } else {
//            GL11.glPushMatrix();
//            GL11.glEnable((int)3089);
            gfx.pose().pushMatrix();
            SocialOverlayScreen overlayScreen = SocialOverlayScreen.getInstance();
            this.scrollableElement.drawScrollable(gfx, f, f2, bl);
            gfx.enableScissor((int)this.x, (int)this.y, (int)(this.x + this.width), (int)(this.y + this.height));
            ImmutableList<FriendRequestElement> immutableList = ImmutableList.copyOf(this.elements);
            for (FriendRequestElement friendRequestElement : immutableList) {
                if (!this.isFilterMatch(friendRequestElement)) continue;
                friendRequestElement.drawElement(gfx, f, f2 - this.scrollableElement.IllIIIIIIIlIlIllllIIllIII(), bl);
            }
            if (immutableList.isEmpty()) {
//                CheatBreaker.getInstance().playRegular16px.drawCenteredString("No friend requests", this.x + this.width / 2.0f, this.y + (float)30, -1);
                RenderUtil.drawCenteredString(gfx, Fonts.playRegular16, "No friend requests", this.x + this.width / 2.0f, this.y + (float)30, -1);
            }
            this.filter.drawElement(gfx, f, f2 - this.scrollableElement.IllIIIIIIIlIlIllllIIllIII(), true);
            this.username.drawElement(gfx, f, f2, true);
            this.addButton.drawElement(gfx, f, f2, true);
            this.toggleRequests.draw(gfx, (CheatBreaker.getInstance().isAcceptingFriendRequests() ? "Disable" : "Enable") + " incoming friend requests", f, f2, true);
//            GL11.glDisable((int)3089);
//            GL11.glPopMatrix();
            this.scrollableElement.handleElementDraw(gfx, f, f2, bl);
            gfx.disableScissor();
            gfx.pose().popMatrix();
            gfx.pose().popMatrix();
        }
    }

    @Override
    public void handleElementMouse() {
        this.scrollableElement.handleElementMouse();
    }

    @Override
    public void handleElementUpdate() {
        this.filter.handleElementUpdate();
        this.username.handleElementUpdate();
        this.toggleRequests.handleElementUpdate();
        this.addButton.handleElementUpdate();
        this.scrollableElement.handleElementUpdate();
    }

    @Override
    public void handleElementClose() {
        this.filter.handleElementClose();
        this.scrollableElement.handleElementClose();
    }

    @Override
    public void handleElementKeyTyped(int keyCode, int scanCode, int modifiers) {
        super.handleElementKeyTyped(keyCode, scanCode, modifiers);
        this.filter.handleElementKeyTyped(keyCode, scanCode, modifiers);
        this.username.handleElementKeyTyped(keyCode, scanCode, modifiers);
        this.toggleRequests.handleElementKeyTyped(keyCode, scanCode, modifiers);
        this.addButton.handleElementKeyTyped(keyCode, scanCode, modifiers);
        this.scrollableElement.handleElementKeyTyped(keyCode, scanCode, modifiers);
        if (this.username.isFocused() && keyCode == GLFW.GLFW_KEY_ENTER) {
            this.lIIlIlIllIIlIIIlIIIlllIII();
        }
        this.setElementSize(this.x, this.y, this.width, this.height);
    }

    @Override
    public boolean handleElementMouseClicked(float f, float f2, int n, boolean bl) {
        this.filter.handleElementMouseClicked(f, f2 - this.scrollableElement.IllIIIIIIIlIlIllllIIllIII(), n, bl);
        this.username.handleElementMouseClicked(f, f2 - this.scrollableElement.IllIIIIIIIlIlIllllIIllIII(), n, bl);
        if (this.filter.isFocused() && n == 1 && this.filter.getText().equals("")) {
            this.resetSize();
        }
        if (!bl) {
            return false;
        }
        this.addButton.handleElementMouseClicked(f, f2 - this.scrollableElement.IllIIIIIIIlIlIllllIIllIII(), n, bl);
        this.toggleRequests.handleElementMouseClicked(f, f2 - this.scrollableElement.IllIIIIIIIlIlIllllIIllIII(), n, bl);
        this.scrollableElement.handleElementMouseClicked(f, f2, n, bl);
        if (this.addButton.isMouseInside(f, f2 - this.scrollableElement.IllIIIIIIIlIlIllllIIllIII())) {
            this.lIIlIlIllIIlIIIlIIIlllIII();
        }
        if (this.toggleRequests.isMouseInside(f, f2 - this.scrollableElement.IllIIIIIIIlIlIllllIIllIII())) {
            CheatBreaker.getInstance().getAssetsWebSocket().send(new WSPacketClientRequestsStatus(!CheatBreaker.getInstance().isAcceptingFriendRequests()));
            CheatBreaker.getInstance().setAcceptingFriendRequests(!CheatBreaker.getInstance().isAcceptingFriendRequests());
            return false;
        }
        boolean bl2 = false;
        for (FriendRequestElement ilIIlIllIllllIIlIllllIlII : this.elements) {
            if (!this.isFilterMatch(ilIIlIllIllllIIlIllllIlII)) continue;
            if (bl2) break;
            bl2 = ilIIlIllIllllIIlIllllIlII.handleElementMouseClicked(f, f2 - this.scrollableElement.IllIIIIIIIlIlIllllIIllIII(), n, bl);
        }
        return bl2;
    }

    private void lIIlIlIllIIlIIIlIIIlllIII() {
        if (!this.username.getText().isEmpty()) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            String string = this.username.getText();
            // sanitizes the name of the friend before sending the packet.
            if (string.matches("([a-zA-Z0-9_]+)") && string.length() <= 16) {
                CheatBreaker.getInstance().getAssetsWebSocket().send(new WSPacketFriendRequest("", this.username.getText()));
                this.username.setText("");
            } else {
                Alert.displayMessage(ChatColor.RED + "Error!", "Incorrect username.");
            }
        }
    }

    @Override
    public boolean handleElementMouseRelease(float f, float f2, int n, boolean bl) {
        if (!bl) {
            return false;
        }
        this.filter.handleElementMouseRelease(f, f2 - this.scrollableElement.IllIIIIIIIlIlIllllIIllIII(), n, bl);
        this.username.handleElementMouseRelease(f, f2 - this.scrollableElement.IllIIIIIIIlIlIllllIIllIII(), n, bl);
        this.addButton.handleElementMouseRelease(f, f2 - this.scrollableElement.IllIIIIIIIlIlIllllIIllIII(), n, bl);
        this.scrollableElement.handleElementMouseRelease(f, f2 - this.scrollableElement.IllIIIIIIIlIlIllllIIllIII(), n, bl);
        boolean bl2 = false;
        for (FriendRequestElement ilIIlIllIllllIIlIllllIlII : this.elements) {
            if (!this.isFilterMatch(ilIIlIllIllllIIlIllllIlII)) continue;
            if (bl2) break;
            bl2 = ilIIlIllIllllIIlIllllIlII.handleElementMouseRelease(f, f2 - this.scrollableElement.IllIIIIIIIlIlIllllIIllIII(), n, bl);
        }
        return bl2;
    }

//    public FlatButtonElement IlllIllIlIIIIlIIlIIllIIIl() {
//        return this.addButton;
//    }
//
//    public FlatButtonElement getToggleRequestsButton() {
//        return this.toggleRequests;
//    }

    public List<FriendRequestElement> getFrientRequestElementList() {
        return this.friendRequestElements;
    }
}
