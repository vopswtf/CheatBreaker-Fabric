package cc.vops.cheatbreaker.client.ui.overlay.friend;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.mainmenu.AbstractElement;
import cc.vops.cheatbreaker.client.ui.overlay.SocialOverlayScreen;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.PlayerHeads;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import cc.vops.cheatbreaker.client.websocket.shared.WSPacketClientFriendRequestUpdate;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;

public class FriendRequestElement extends AbstractElement {
    private final FriendRequest friendRequest;

    public FriendRequestElement(FriendRequest friendRequest) {
        this.friendRequest = friendRequest;
    }

    @Override
    public void handleElementDraw(GuiGraphicsExtractor gfx, float mouseX, float mouseY, boolean bl) {
        if (bl && this.isMouseInside(mouseX, mouseY)) {
            RenderUtil.drawRect(gfx, this.x, this.y, this.x + this.width, this.y + this.height, -13750738);
        }
        gfx.pose().pushMatrix();
        RenderUtil.drawRect(gfx, this.x, this.y - 1.2982457f * 0.3851351f, this.x + this.width, this.y, -1357572843);
        RenderUtil.drawRect(gfx, this.x, this.y + this.height, this.x + this.width, this.y + this.height + 0.5f, -1357572843);
        RenderUtil.drawRect(gfx, this.x + (float)4, this.y + (float)3, this.x + (float)20, this.y + (float)19, -16747106);
//        CheatBreaker.getInstance().playRegular16px.drawString(this.friendRequest.getUsername(), this.x + (float)24, this.y + 2.0f, -1);
        RenderUtil.drawString(gfx, Fonts.playRegular16, this.friendRequest.getUsername(), this.x + (float)24, this.y + 2.0f, -1);
        if (this.friendRequest.isFriend()) {
            boolean cancelHovered = mouseX > this.x + (float)24 && mouseX < this.x + (float)52 && mouseY < this.y + this.height && mouseY > this.y + (float)10 && bl;
//            CheatBreaker.getInstance().playRegular14px.drawString("CANCEL", this.x + (float)24, this.y + (float)11, cancelHovered ? -52429 : 0x7FFF3333);
            RenderUtil.drawString(gfx, Fonts.playRegular14, "CANCEL", this.x + (float)24, this.y + (float)11, cancelHovered ? -52429 : 0x7FFF3333);
        } else {
            boolean acceptHovered = mouseX > this.x + (float)24 && mouseX < this.x + (float)52 && mouseY < this.y + this.height && mouseY > this.y + (float)10 && bl;
            boolean denyHovered = mouseX > this.x + (float)52 && mouseX < this.x + (float)84 && mouseY < this.y + this.height && mouseY > this.y + (float)10 && bl;
//            CheatBreaker.getInstance().playRegular16px.drawString("ACCEPT", this.x + (float)24, this.y + (float)11, acceptHovered ? -13369549 : 0x7F33FF33);
//            CheatBreaker.getInstance().playRegular16px.drawString("DENY", this.x + (float)56, this.y + (float)11, denyHovered ? -52429 : 0x7FFF3333);
            RenderUtil.drawString(gfx, Fonts.playRegular16, "ACCEPT", this.x + (float)24, this.y + (float)11, acceptHovered ? -13369549 : 0x7F33FF33);
            RenderUtil.drawString(gfx, Fonts.playRegular16, "DENY", this.x + (float)56, this.y + (float)11, denyHovered ? -52429 : 0x7FFF3333);
        }

        Identifier Identifier = PlayerHeads.getHeadLocation(ChatFormatting.stripFormatting(this.friendRequest.getUsername()), this.friendRequest.getPlayerUUID());
        RenderUtil.drawIcon(gfx, Identifier, (float)7, this.x + (float)5, this.y + (float)4);
        gfx.pose().popMatrix();
    }

    @Override
    public boolean handleElementMouseClicked(float f, float f2, int n, boolean bl) {
        if (!bl) {
            return false;
        }
        if (this.friendRequest.isFriend()) {
            boolean cancelHovered  = f > this.x + (float) 24 && f < this.x + (float) 52 && f2 < this.y + this.height && f2 > this.y + (float) 10;
            if (cancelHovered) {
                CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
                CheatBreaker.getInstance().getAssetsWebSocket().send(new WSPacketClientFriendRequestUpdate(false, this.friendRequest.getPlayerId()));
                SocialOverlayScreen.getInstance().getFriendRequestsElement().getFrientRequestElementList().add(this);
            }
        } else {
            boolean acceptHovered = f > this.x + (float)24 && f < this.x + (float)52 && f2 < this.y + this.height && f2 > this.y + (float)10;
            boolean denyHovered = f > this.x + (float) 52 && f < this.x + (float) 84 && f2 < this.y + this.height && f2 > this.y + (float) 10;
            if (acceptHovered) {
                CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
                CheatBreaker.getInstance().getAssetsWebSocket().send(new WSPacketClientFriendRequestUpdate(true, this.friendRequest.getPlayerId()));
                SocialOverlayScreen.getInstance().getFriendRequestsElement().getFrientRequestElementList().add(this);
            } else if (denyHovered) {
                CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
                CheatBreaker.getInstance().getAssetsWebSocket().send(new WSPacketClientFriendRequestUpdate(false, this.friendRequest.getPlayerId()));
                SocialOverlayScreen.getInstance().getFriendRequestsElement().getFrientRequestElementList().add(this);
            }
        }
        return super.handleElementMouseClicked(f, f2, n, true);
    }

    public FriendRequest getFriendRequest() {
        return this.friendRequest;
    }
}
