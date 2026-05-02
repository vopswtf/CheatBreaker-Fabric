package cc.vops.cheatbreaker.client.ui.mainmenu;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.fading.ColorFade;
import cc.vops.cheatbreaker.client.ui.fading.MinMaxFade;
import cc.vops.cheatbreaker.client.ui.mainmenu.element.ScrollableElement;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public class AccountList extends AbstractElement {
    private Identifier resourceLocation;
    private String username;
    private final ColorFade outline;
    private final ColorFade topGradient;
    private final ColorFade bottomGradient;
    private final MinMaxFade fadeTime;
    private float height2;
    private boolean interacting;
    private final MainMenuBase mainMenuBase;
    private final ScrollableElement scrollBar;
    private float selectionBoxHeight;


    public AccountList(MainMenuBase lIIIlIlIIllIIlllIIIlIIllI2, String string, Identifier resourceLocation) {
        this.mainMenuBase = lIIIlIlIIllIIlllIIIlIIllI2;
        this.resourceLocation = resourceLocation;
        this.username = string;
        this.scrollBar = new ScrollableElement(this);
        this.outline = new ColorFade(0x4FFFFFFF, -1353670564);
        this.topGradient = new ColorFade(444958085, 1063565678);
        this.bottomGradient = new ColorFade(444958085, 1062577506);
        this.fadeTime = new MinMaxFade(300L);
    }

    public void IllIIIIIIIlIlIllllIIllIII() {
        this.setElementSize(this.x, this.y, this.width, this.height);
    }

    @Override
    public void setElementSize(float x, float y, float width, float height) {
        super.setElementSize(x, y, width, height);
        if (this.height2 == 0.0f) {
            this.height2 = height;
        }
        this.selectionBoxHeight = Math.min(this.mainMenuBase.getAccounts().size() * 16 + 12, 120);
        this.scrollBar.setElementSize(x + width - (float)5, y + this.height2 + (float)6, (float)4, this.selectionBoxHeight - (float)7);
        this.scrollBar.setScrollAmount(this.mainMenuBase.getAccounts().size() * 16 + 4);
    }

    @Override
    public void handleElementMouse() {
        this.scrollBar.handleElementMouse();
    }

    @Override
    protected void handleElementDraw(GuiGraphicsExtractor gfx, float f, float f2, boolean bl) {
        boolean bl2 = bl && this.isMouseInside(f, f2);
        RenderUtil.drawCorneredGradientRectWithOutline(gfx, this.x, this.y, this.x + this.width, this.y + this.height2, this.outline.get(bl2).getRGB(), this.topGradient.get(bl2).getRGB(), this.bottomGradient.get(bl2).getRGB());
        float f3 = 6;
        RenderUtil.drawIcon(gfx, this.resourceLocation, f3, this.x + (float)4, this.y + this.height2 / 2.0f - f3, -1);
        RenderUtil.drawString(gfx, Fonts.robotoRegular13, this.username, this.x + (float)22, this.y + 2.06f * 2.8846154f, -1342177281);
//        float f4 = this.fadeTime.inOutFade(this.isMouseInside(f, f2) && bl);
//        if (this.fadeTime.isExpired()) {
//            this.setElementSize(this.x, this.y, this.width, this.height2 + this.selectionBoxHeight * f4);
//            this.interacting = true;
//        } else if (!this.fadeTime.isExpired() && !this.isMouseInside(f, f2)) {
//            this.interacting = false;
//        }
//        if (this.interacting) {
//            float f5 = 0.6122449f * 0.81666666f;
//            float f6 = this.y + this.height + f5;
//            float f7 = this.y + (float)5 + this.height2;
//            if (f6 > f7) {
//                RenderUtil.drawBoxWithOutLine(gfx, this.x + 1.0f, f7, this.x + this.width - 1.0f, f6, f5, 0x4FFFFFFF, 444958085);
//            }
////            GL11.glPushMatrix();
////            GL11.glEnable(0xc11);
////            gfx.pose().pushMatrix();
//            RenderUtil.drawGradientRect(
//                    gfx,
//                    (int)this.x,
//                    (int)(this.y + this.height2),
//                    (int)(this.x + this.width),
//                    (int)(this.y + this.height2 + (float)7 + (this.height - this.height2 - (float)6) * f4),
//                    ((int)(CheatBreaker.getScaleFactor() * CheatBreaker.getScaleFactor())),
//                    this.mainMenuBase.getScaledHeight()
//            );
//            this.scrollBar.drawScrollable(gfx, f, f2, bl);
//            int n = 1;
//            for (Account account : this.mainMenuBase.getAccounts()) {
//                float f8 = this.x;
//                float f9 = this.x + this.width;
//                float f10 = this.y + this.height2 + (float)(n * 16) - (float)8;
//                float f11 = f10 + (float)16;
//                boolean hovered = f > f8 && f < f9 && f2 - this.scrollBar.IllIIIIIIIlIlIllllIIllIII() > f10 && f2 - this.scrollBar.IllIIIIIIIlIlIllllIIllIII() < f11 && bl && !this.scrollBar.isMouseInside(f, f2) && !this.scrollBar.isDragClick();
//                RenderUtil.drawIcon(gfx, account.getHeadLocation(), f3, this.x + (float)4, f10 + (float)8 - f3, CheatBreaker.getColor(1.0f, 1.0f, 1.0f, hovered ? 1.0f : 0.8148148f * 0.8590909f));
//                RenderUtil.drawString(gfx, Fonts.robotoRegular13, account.getUsername(), this.x + (float)22, f10 + (float)4, CheatBreaker.getColor(1.0f, 1.0f, 1.0f, hovered ? 1.0f : 0.8148148f * 0.8590909f));
//                ++n;
//            }
//            this.scrollBar.handleElementDraw(gfx, f, f2, bl);
//            gfx.pose().popMatrix();
//        }
    }

    public float getWidth(float f) {
        return (float)22 + f + (float)10;
    }

//    @Override
//    public boolean handleElementMouseClicked(float f, float f2, int n, boolean bl) {
//        if (!bl) {
//            return false;
//        }
//        if (this.fadeTime.IllIllIIIlIIlllIIIllIllII()) {
//            this.scrollBar.handleElementMouseClicked(f, f2, n, bl);
//            int n2 = 1;
//            for (Account illIIIllIlIIlIllIIIllllIl : this.mainMenuBase.getAccounts()) {
//                boolean bl2;
//                float f3 = this.x;
//                float f4 = this.x + this.width;
//                float f5 = this.y + this.height2 + (float)(n2 * 16) - (float)8;
//                float f6 = f5 + (float)16;
//                boolean bl3 = bl2 = f > f3 && f < f4 && f2 - this.scrollBar.IllIIIIIIIlIlIllllIIllIII() > f5 && f2 - this.scrollBar.IllIIIIIIIlIlIllllIIllIII() < f6 && bl && !this.scrollBar.isMouseInside(f, f2) && !this.scrollBar.isDragClick();
//                if (bl2) {
//                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0f));
//                    this.mainMenuBase.login(illIIIllIlIIlIllIIIllllIl.getDisplayName());
//                }
//                ++n2;
//            }
//        }
//        return false;
//    }
}
