package cc.vops.cheatbreaker.client.ui.overlay.friend;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.mainmenu.element.ScrollableElement;
import cc.vops.cheatbreaker.client.ui.overlay.SocialOverlayScreen;
import cc.vops.cheatbreaker.client.ui.overlay.element.ElementListElement;
import cc.vops.cheatbreaker.client.ui.overlay.element.InputFieldElement;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.List;

public class FriendsListElement extends ElementListElement<FriendElement> {
    private final InputFieldElement filterElement;
    private final ScrollableElement scrollableElement;
    private final List<FriendElement> friendElements = new ArrayList<>();

    public FriendsListElement(List<FriendElement> list) {
        super(list);
        this.filterElement = new InputFieldElement(Fonts.playRegular16, "Filter", 0x2FFFFFFF, 0x6FFFFFFF);
        this.scrollableElement = new ScrollableElement(this);
    }

    @Override
    public void handleCharInput(char typedChar, int keyCode) {
        super.handleCharInput(typedChar, keyCode);
        this.filterElement.handleCharInput(typedChar, keyCode);
    }

    public void updateSize() {
        this.setElementSize(this.x, this.y, this.width, this.height);
    }

    @Override
    public void setElementSize(float x, float y, float width, float height) {
        super.setElementSize(x, y, width, height);
        this.filterElement.setElementSize(0.0f, y, width, 13);
        this.scrollableElement.setElementSize(x + width - (float)4, y, (float)4, height);
        this.elements.sort((friendElement, friendElement2) -> {
            String string = ChatFormatting.stripFormatting(friendElement.getFriend().getName());
            String string2 = ChatFormatting.stripFormatting(friendElement2.getFriend().getName());
            if (friendElement.getFriend().getPlayerId().equals(friendElement2.getFriend().getPlayerId())) {
                return string.compareTo(string2);
            }
            return friendElement.getFriend().isOnline() ? 0 : 1;
        });
        int n = 22;
        int n2 = 0;
        for (FriendElement friendElement : this.elements) {
            if (!this.isFilterMatch(friendElement)) continue;
            friendElement.setElementSize(x, y + (float)14 + (float)(n2 * 22), width, 22);
            ++n2;
        }
        this.scrollableElement.setScrollAmount(14 + this.elements.size() * 22);
    }

    private boolean isFilterMatch(FriendElement friendElement) {
        return this.filterElement.getText().equals("") || ChatFormatting.stripFormatting(friendElement.getFriend().getName()).toLowerCase().startsWith(this.filterElement.getText().toLowerCase());
    }

    /*
     * Iterators could be improved
     */
    @Override
    public void handleElementDraw(GuiGraphicsExtractor gfx, float f, float f2, boolean bl) {
        if (!this.friendElements.isEmpty()) {
            this.elements.removeAll(this.friendElements);
            SocialOverlayScreen.getInstance().getFriendsListElement().updateSize();
        }
        if (!CheatBreaker.getInstance().getAssetsWebSocket().isOpen()) {
            RenderUtil.drawCenteredString(gfx, Fonts.playRegular16, "Connection lost", this.x + this.width / 2.0f, this.y + (float)10, -1);
            RenderUtil.drawCenteredString(gfx, Fonts.playRegular16, "Please try again later.", this.x + this.width / 2.0f, this.y + (float)22, -1);
        } else {
//            GL11.glPushMatrix();
//            GL11.glEnable((int)3089);
            gfx.pose().pushMatrix();
            this.scrollableElement.drawScrollable(gfx, f, f2, bl);
            gfx.enableScissor((int)this.x, (int)this.y, (int)(this.x + this.width), (int)(this.y + this.height));
            ImmutableList<FriendElement> immutableList = ImmutableList.copyOf(this.elements);
            for (FriendElement friendElement : immutableList) {
                if (!this.isFilterMatch(friendElement)) continue;
                friendElement.drawElement(gfx, f, f2 - this.scrollableElement.IllIIIIIIIlIlIllllIIllIII(), bl && !this.scrollableElement.isMouseInside(f, f2));
            }
            if (immutableList.isEmpty()) {
//                CheatBreaker.getInstance().playRegular16px.drawCenteredString("No friends", this.x + this.width / 2.0f, this.y + (float)30, -1);
                RenderUtil.drawCenteredString(gfx, Fonts.playRegular16, "No friends", this.x + this.width / 2.0f, this.y + (float)30, -1);
            }
            this.filterElement.drawElement(gfx, f, f2 - this.scrollableElement.IllIIIIIIIlIlIllllIIllIII(), bl);
            gfx.disableScissor();
//            GL11.glDisable((int)3089);
//            GL11.glPopMatrix();

            this.scrollableElement.handleElementDraw(gfx, f, f2, bl);
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
        this.filterElement.handleElementUpdate();
        this.scrollableElement.handleElementUpdate();
    }

    @Override
    public void handleElementClose() {
        this.filterElement.handleElementClose();
        this.scrollableElement.handleElementClose();
    }

    @Override
    public void handleElementKeyTyped(int keyCode, int scanCode, int modifiers) {
        super.handleElementKeyTyped(keyCode, scanCode, modifiers);
        this.filterElement.handleElementKeyTyped(keyCode, scanCode, modifiers);
        this.scrollableElement.handleElementKeyTyped(keyCode, scanCode, modifiers);
        this.setElementSize(this.x, this.y, this.width, this.height);
    }

    @Override
    public boolean handleElementMouseClicked(float f, float f2, int n, boolean bl) {
        this.filterElement.handleElementMouseClicked(f, f2 - this.scrollableElement.IllIIIIIIIlIlIllllIIllIII(), n, bl);
        if (this.filterElement.isFocused() && n == 1 && this.filterElement.getText().equals("")) {
            this.updateSize();
        }
        if (!bl) {
            return false;
        }
        this.scrollableElement.handleElementMouseClicked(f, f2, n, bl);
        boolean bl2 = false;
        for (FriendElement frientElement : this.elements) {
            if (!this.isFilterMatch(frientElement)) continue;
            if (bl2) break;
            bl2 = frientElement.handleElementMouseClicked(f, f2 - this.scrollableElement.IllIIIIIIIlIlIllllIIllIII(), n, bl && !this.scrollableElement.isMouseInside(f, f2));
        }
        return bl2;
    }

    @Override
    public boolean handleElementMouseRelease(float f, float f2, int n, boolean bl) {
        this.filterElement.handleElementMouseRelease(f, f2 - this.scrollableElement.IllIIIIIIIlIlIllllIIllIII(), n, bl);
        this.scrollableElement.handleElementMouseRelease(f, f2, n, bl);
        if (!bl) {
            return false;
        }
        boolean bl2 = false;
        for (FriendElement friendElement : this.elements) {
            if (!this.isFilterMatch(friendElement)) continue;
            if (bl2) break;
            bl2 = friendElement.handleElementMouseRelease(f, f2, n, bl);
        }
        return bl2;
    }

    public List<FriendElement> getFriendElements() {
        return this.friendElements;
    }
}