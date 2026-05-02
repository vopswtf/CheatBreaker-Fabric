package cc.vops.cheatbreaker.client.ui.overlay.friend;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.fading.CosineFade;
import cc.vops.cheatbreaker.client.ui.fading.FloatFade;
import cc.vops.cheatbreaker.client.ui.mainmenu.AbstractElement;
import cc.vops.cheatbreaker.client.ui.overlay.SocialOverlayScreen;
import cc.vops.cheatbreaker.client.util.ChatColor;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.PlayerHeads;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import cc.vops.cheatbreaker.client.util.friend.Friend;
import cc.vops.cheatbreaker.client.util.friend.FriendsManager;
import cc.vops.cheatbreaker.client.websocket.client.WSPacketClientFriendRemove;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;

import java.awt.*;
import java.util.List;

public class FriendElement extends AbstractElement {
    private final Friend friend;
    private final CosineFade fade;
    private final FloatFade removeFade;
    private static final Identifier removeIcon = CheatBreaker.asset("icons/garbage-26.png");
    private static final Identifier cheatBreakerIcon = CheatBreaker.asset("logo_26.png");

    public FriendElement(Friend friend) {
        this.friend = friend;
        this.fade = new CosineFade(1500L);
        this.removeFade = new FloatFade(200L);
        this.fade.enableShouldResetOnceCalled();
    }

    @Override
    public void handleElementDraw(GuiGraphicsExtractor gfx, float f, float f2, boolean bl) {
        List<?> object;
        if (bl && this.isMouseInside(f, f2)) {
            RenderUtil.drawRect(gfx, this.x, this.y, this.x + this.width, this.y + this.height, -13750738);
        }
        gfx.pose().pushMatrix();
        FriendsManager friendsManager = CheatBreaker.getInstance().getFriendsManager();
        if (friendsManager.getUnreadMessages().containsKey(this.friend.getPlayerId())) {
            object = friendsManager.getUnreadMessages().get(this.friend.getPlayerId());
            if (object != null && !object.isEmpty()) {
                if (!this.fade.hasStartTime()) {
                    this.fade.reset();
                }
                RenderUtil.drawRect(gfx, this.x, this.y, this.x + this.width, this.y + this.height, new Color(0.20185566f * 4.409091f, 0.45f * 1.2f, 0.044696968f * 1.1186441f, 0.8933333f * 0.7276119f * (0.315f * 0.4888889f + this.fade.getCurrentValue())).getRGB());
                RenderUtil.drawString(gfx, Fonts.playRegular16, object.size() + "", this.x + this.width - (float)15, this.y + (float)6, -1);
            } else if (this.fade.hasStartTime() && this.fade.isExpired()) {
                this.fade.IlIlIIIlllIIIlIlllIlIllIl();
            }
        }
        RenderUtil.drawRect(gfx, this.x, this.y - 0.5f, this.x + this.width, this.y, -1357572843);
        RenderUtil.drawRect(gfx, this.x, this.y + this.height, this.x + this.width, this.y + this.height + 9.9f * 0.050505053f, -1357572843);
        RenderUtil.drawRect(gfx, this.x + (float)4, this.y + (float)3, this.x + (float)20, this.y + (float)19, this.friend.isOnline() ? Friend.getStatusColor(this.friend.getOnlineStatus()) : -13158601);
        if (this.friend.getName().startsWith(ChatColor.RED)) {
            RenderUtil.drawIcon(gfx, cheatBreakerIcon, 6.5f, this.x + (float)24, this.y + (float)4);
            RenderUtil.drawString(gfx, Fonts.playRegular16, this.friend.getName(), this.x + (float)40, this.y + 2.0f, -1);
            RenderUtil.drawString(gfx, Fonts.playRegular16, this.friend.getStatusString(), this.x + (float)40, this.y + (float)11, -5460820);
        } else {
            RenderUtil.drawString(gfx, Fonts.playRegular16, this.friend.getName(), this.x + (float)24, this.y + 2.0f, -1);
            RenderUtil.drawString(gfx, Fonts.playRegular16, this.friend.getStatusString(), this.x + (float)24, this.y + (float)11, -5460820);
        }
//        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        Identifier headLocation = PlayerHeads.getHeadLocation(ChatFormatting.stripFormatting(this.friend.getName()), this.friend.getPlayerUUID());
        RenderUtil.drawIcon(gfx, headLocation, (float)7, this.x + (float)5, this.y + (float)4);
        boolean bl2 = bl && this.isMouseInside(f, f2) && f > this.x + this.width - (float)20;
        float f3 = this.removeFade.inOutFade(bl2);
        float f4 = this.x + this.width - 20.5f * f3;
        RenderUtil.drawRect(gfx, f4, this.y, this.x + this.width, this.y + this.height, -52429);
        RenderUtil.drawIcon(gfx, removeIcon, f4 + (float)4, this.y + (float)5, (float)12, 12, CheatBreaker.getColor(1.0f, 1.0f, 1.0f, 1.4470588f * 0.6219512f));
        gfx.pose().popMatrix();
    }

    @Override
    public boolean handleElementMouseClicked(float f, float f2, int n, boolean bl) {
        if (!bl) {
            return false;
        }
        boolean bl2 = this.isMouseInside(f, f2) && f > this.x + this.width - (float) 20;
        if (bl2 && this.removeFade.isExpired()) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            CheatBreaker.getInstance().getAssetsWebSocket().send(new WSPacketClientFriendRemove(this.friend.getPlayerId()));
            SocialOverlayScreen.getInstance().getFriendsListElement().getFriendElements().add(this);
            CheatBreaker.getInstance().getFriendsManager().getFriends().remove(this.friend.getPlayerId());
            return true;
        }
        if (!bl2 && this.isMouseInside(f, f2)) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            SocialOverlayScreen.getInstance().setMessages(this.friend);
            CheatBreaker.getInstance().getFriendsManager().readMessages(this.friend.getPlayerId());
            return true;
        }
        return super.handleElementMouseClicked(f, f2, n, true);
    }

    public Friend getFriend() {
        return this.friend;
    }
}
