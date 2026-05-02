package cc.vops.cheatbreaker.client.ui.mainmenu.dev;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.mainmenu.MainMenu;
import cc.vops.cheatbreaker.client.ui.mainmenu.MainMenuBase;
import cc.vops.cheatbreaker.client.ui.mainmenu.element.GradientTextButton;
import cc.vops.cheatbreaker.client.util.font.CBFontRenderer;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.sounds.SoundEvents;

public class GuiRenderTest extends MainMenuBase {
    private final GradientTextButton backButton = new GradientTextButton("BACK");

    @Override
    public void drawMenu(GuiGraphicsExtractor gfx, float f, float f2, float delta) {
        super.drawMenu(gfx, f, f2, delta);

        float startY = 40.0f;

        for (CBFontRenderer font : Fonts.getFonts().values()) {
            RenderUtil.drawString(gfx, font, font.getName() + " - The quick brown fox jumps over the lazy dog 0123456789", 20.0f, startY, -1);
            startY += font.height() + 4.0f;
        }

        this.backButton.setElementSize(this.getScaledWidth() / 2.0f - (float)30, this.getScaledHeight() / 2.0f + (float)105, (float)60, 12);
        this.backButton.drawElement(gfx, f, f2, true);
    }


    @Override
    public boolean onMouseClicked(double f, double f2, int n) {
        super.onMouseClicked(f, f2, n);
        if (this.backButton.isMouseInside(f, f2)) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            this.mc.setScreen(new MainMenu());
        }

        return true;
    }
}
