package cc.vops.cheatbreaker.client.ui.overlay.element;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.fading.CosineFade;
import cc.vops.cheatbreaker.client.ui.mainmenu.element.ScrollableElement;
import cc.vops.cheatbreaker.client.ui.overlay.SocialOverlayScreen;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import cc.vops.cheatbreaker.client.util.friend.Friend;
import cc.vops.cheatbreaker.client.util.thread.AliasesThread;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.sounds.SoundEvents;

import java.util.ArrayList;
import java.util.List;

public class AliasesElement extends DraggableElement {
    private final ScrollableElement scrollContainer;
    private final Friend friend;
    private final FlatButtonElement closeButton;
    private final CosineFade cosineFade;
    private List<String> aliases = new ArrayList<>();

    public AliasesElement(Friend friend) {
        this.scrollContainer = new ScrollableElement(this);
        this.friend = friend;
        this.closeButton = new FlatButtonElement("X");
        this.cosineFade = new CosineFade(1500L);
        this.cosineFade.reset();
        this.cosineFade.enableShouldResetOnceCalled();
        new AliasesThread(this).start();
    }

    private float IlIlllIIIIllIllllIllIIlIl() {
        return this.cosineFade.getCurrentValue() * 2.0f - 1.0f;
    }

    @Override
    public void setElementSize(float x, float y, float width, float height) {
        super.setElementSize(x, y, width, height);
        this.scrollContainer.setElementSize(x + width - (float)4, y, (float)4, height);
        this.scrollContainer.setScrollAmount(height);
        this.closeButton.setElementSize(x + width - (float)12, y + 2.0f, (float)10, 10);
    }

    @Override
    protected void handleElementDraw(GuiGraphicsExtractor gfx, float f, float f2, boolean bl) {
        this.drag(f, f2);
        this.scrollContainer.drawScrollable(gfx, f, f2, bl);
        RenderUtil.drawBoxWithOutLine(gfx, this.x, this.y, this.x + this.width, this.y + this.height, 0.06666667f * 7.5f, -16777216, -14869219);
//        CheatBreaker.getInstance().playRegular16px.drawString(this.friend.getName(), this.x + (float)4, this.y + (float)4, -1);
        RenderUtil.drawString(gfx, Fonts.playRegular16, this.friend.getName(), this.x + (float)4, this.y + (float)4, -1);
        RenderUtil.drawRect(gfx, this.x + (float)3, this.y + (float)15, this.x + this.width - (float)3, this.y + 0.9791667f * 15.829787f, 0x2FFFFFFF);
        if (this.aliases.isEmpty()) {
            RenderUtil.drawRect(gfx, this.x + (float)4, this.y + this.height - (float)9, this.x + this.width - (float)4, this.y + this.height - (float)5, -13158601);
            float f3 = this.x + this.width / 2.0f - (float)10 + (this.width - (float)28) * this.IlIlllIIIIllIllllIllIIlIl() / 2.0f;
            RenderUtil.drawRect(gfx, f3, this.y + this.height - (float)9, f3 + (float)20, this.y + this.height - (float)5, -4180940);
        }
        int n = 0;
        for (String string : this.aliases) {
//            CheatBreaker.getInstance().playRegular16px.drawString(string, this.x + (float)4, this.y + (float)18 + (float)(n * 10), -1);
            RenderUtil.drawString(gfx, Fonts.playRegular16, string, this.x + (float)4, this.y + (float)18 + (float)(n * 10), -1);
            ++n;
        }
        this.scrollContainer.handleElementDraw(gfx, f, f2, bl);
        this.closeButton.handleElementDraw(gfx, f, f2, bl);
        gfx.pose().popMatrix();
    }

    @Override
    public boolean handleElementMouseClicked(float f, float f2, int n, boolean bl) {
        if (!bl) {
            return false;
        }
        this.scrollContainer.handleElementMouseClicked(f, f2, n, bl);
        this.closeButton.handleElementMouseClicked(f, f2, n, bl);
        if (this.closeButton.isMouseInside(f, f2)) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            SocialOverlayScreen.getInstance().removeElements(this);
            return true;
        }
        if (this.isMouseInside(f, f2)) {
            this.updateDraggingPosition(f, f2);
        }
        return false;
    }

    public Friend getFriend() {
        return this.friend;
    }

    public List<String> getAliases() {
        return this.aliases;
    }
}
