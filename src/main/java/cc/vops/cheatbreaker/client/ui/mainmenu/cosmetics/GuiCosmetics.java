package cc.vops.cheatbreaker.client.ui.mainmenu.cosmetics;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.mainmenu.MainMenu;
import cc.vops.cheatbreaker.client.ui.mainmenu.MainMenuBase;
import cc.vops.cheatbreaker.client.ui.mainmenu.cosmetics.element.CosmeticListElement;
import cc.vops.cheatbreaker.client.ui.mainmenu.element.GradientTextButton;
import cc.vops.cheatbreaker.client.util.cosmetic.Cosmetic;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;

import java.util.ArrayList;
import java.util.List;

public class GuiCosmetics extends MainMenuBase {
    private final List<CosmeticListElement> cosmeticElements = new ArrayList<>();
    private Identifier leftIcon = CheatBreaker.asset("icons/left.png");
    private Identifier rightIcon = CheatBreaker.asset("icons/right.png");
    private final GradientTextButton backButton = new GradientTextButton("BACK");
    private int IllIIIIIIIlIlIllllIIllIII = 0;

    public GuiCosmetics() {
        for (Cosmetic cosmetic : CheatBreaker.getInstance().getCosmetics()) {
            this.cosmeticElements.add(new CosmeticListElement(cosmetic, 1.0f));
        }
    }

    @Override
    public void drawMenu(GuiGraphicsExtractor gfx, float f, float f2, float delta) {
        super.drawMenu(gfx, f, f2, delta);
        if (!CheatBreaker.getInstance().getAssetsWebSocket().isOpen()) {
//            CheatBreaker.getInstance().playRegular16px.drawCenteredString("Unable to connect to the server.", this.getScaledWidth() / 2.0f, this.getScaledHeight() / 2.0f - (float)10, -1);
//            CheatBreaker.getInstance().playRegular16px.drawCenteredString("Please try again later.", this.getScaledWidth() / 2.0f, this.getScaledHeight() / 2.0f + (float)4, -1);
            RenderUtil.drawCenteredString(gfx, Fonts.playRegular16, "Unable to connect to the server.", this.getScaledWidth() / 2.0f, this.getScaledHeight() / 2.0f - (float)10, -1);
            RenderUtil.drawCenteredString(gfx, Fonts.playRegular16, "Please try again later.", this.getScaledWidth() / 2.0f, this.getScaledHeight() / 2.0f + (float)4, -1);
            this.backButton.setElementSize(this.getScaledWidth() / 2.0f - (float)30, this.getScaledHeight() / 2.0f + (float)28, (float)60, 12);
            this.backButton.drawElement(gfx, f, f2, true);
        } else {
            RenderUtil.drawRect(gfx, this.getScaledWidth() / 2.0f - (float)80, this.getScaledHeight() / 2.0f - (float)78, this.getScaledWidth() / 2.0f + (float)80, this.getScaledHeight() / 2.0f + (float)100, 0x2F000000);
            this.backButton.setElementSize(this.getScaledWidth() / 2.0f - (float)30, this.getScaledHeight() / 2.0f + (float)105, (float)60, 12);
            this.backButton.drawElement(gfx, f, f2, true);
            if (this.cosmeticElements.isEmpty()) {
                RenderUtil.drawCenteredString(gfx, Fonts.playRegular16, "You don't own any cosmetics.", this.getScaledWidth() / 2.0f, this.getScaledHeight() / 2.0f + (float)4, -6381922);
            } else {
                float f3 = this.getScaledHeight() / 2.0f - (float)68;
                float f4 = this.getScaledHeight() / 2.0f + (float)92;
                float f5 = this.getScaledWidth() / 2.0f + (float)68;
                float f6 = this.getScaledWidth() / 2.0f + (float)74;
                RenderUtil.drawCenteredString(gfx, Fonts.playBold18, "Cosmetics (" + this.cosmeticElements.size() + ")", this.getScaledWidth() / 2.0f, this.getScaledHeight() / 2.0f - (float)90, -1);
                int n = 0;
                float f7 = 0.0f;
                for (CosmeticListElement cosmeticElement : this.cosmeticElements) {
                    if (++n - 1 < this.IllIIIIIIIlIlIllllIIllIII * 5 || n - 1 >= (this.IllIIIIIIIlIlIllllIIllIII + 1) * 5) continue;
                    cosmeticElement.setDimensions((int)this.getScaledWidth() / 2 - 76, (int)(this.getScaledHeight() / 2.0f - (float)72 + f7), 152, cosmeticElement.getHeight());
                    cosmeticElement.handleDrawElement(gfx, (int)f, (int)f2, 1.0f);
                    f7 += (float)cosmeticElement.getHeight();
                }
                if (this.cosmeticElements.size() > 5) {
                    boolean bl = f > this.getScaledWidth() / 2.0f - (float)40 && f < this.getScaledWidth() / 2.0f - 1.0f && f2 > this.getScaledHeight() / 2.0f + (float)80 && f2 < this.getScaledHeight() / 2.0f + (float)100;
                    int leftColor = CheatBreaker.getColor(0.0f, 0.0f, 0.0f, bl ? 0.37499997f * 1.2f : 0.8958333f * 0.27906978f);
                    RenderUtil.drawIcon(gfx, this.leftIcon, (float)4, this.getScaledWidth() / 2.0f - (float)20, this.getScaledHeight() / 2.0f + (float)84, leftColor);
                    boolean bl2 = f > this.getScaledWidth() / 2.0f + 1.0f && f < this.getScaledWidth() / 2.0f + (float)40 && f2 > this.getScaledHeight() / 2.0f + (float)80 && f2 < this.getScaledHeight() / 2.0f + (float)100;
                    int rightColor = CheatBreaker.getColor(0.0f, 0.0f, 0.0f, bl2 ? 0.012658228f * 35.55f : 0.7083333f * 0.3529412f);
                    RenderUtil.drawIcon(gfx, this.rightIcon, (float)4, this.getScaledWidth() / 2.0f + (float)10, this.getScaledHeight() / 2.0f + (float)84, rightColor);
                }
            }
        }
    }


    @Override
    public boolean onMouseClicked(double f, double f2, int n) {
        super.onMouseClicked(f, f2, n);
        if (this.backButton.isMouseInside(f, f2)) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            this.mc.setScreen(new MainMenu());
        } else {
            int n2;
            if (this.cosmeticElements.size() > 5) {
                boolean bl;
                n2 = f > this.getScaledWidth() / 2.0f - (float)40 && f < this.getScaledWidth() / 2.0f - 1.0f && f2 > this.getScaledHeight() / 2.0f + (float)80 && f2 < this.getScaledHeight() / 2.0f + (float)100 ? 1 : 0;
                boolean bl2 = bl = f > this.getScaledWidth() / 2.0f + 1.0f && f < this.getScaledWidth() / 2.0f + (float)40 && f2 > this.getScaledHeight() / 2.0f + (float)80 && f2 < this.getScaledHeight() / 2.0f + (float)100;
                if (this.IllIIIIIIIlIlIllllIIllIII > 0 && n2 != 0) {
                    --this.IllIIIIIIIlIlIllllIIllIII;
                    CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
                } else if (bl && (float)(this.IllIIIIIIIlIlIllllIIllIII + 1) < (float)this.cosmeticElements.size() / (float)5) {
                    ++this.IllIIIIIIIlIlIllllIIllIII;
                    CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
                }
            }
            n2 = 0;
            for (CosmeticListElement cosmeticElement : this.cosmeticElements) {
                if (++n2 - 1 < this.IllIIIIIIIlIlIllllIIllIII * 5 || n2 - 1 >= (this.IllIIIIIIIlIlIllllIIllIII + 1) * 5) continue;
                cosmeticElement.onClick((int)f, (int)f2, n);
            }
        }

        return true;
    }
}
