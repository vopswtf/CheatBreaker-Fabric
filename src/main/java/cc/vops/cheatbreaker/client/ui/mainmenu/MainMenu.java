package cc.vops.cheatbreaker.client.ui.mainmenu;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.fading.CosineFade;
import cc.vops.cheatbreaker.client.ui.fading.MinMaxFade;
import cc.vops.cheatbreaker.client.ui.mainmenu.dev.GuiRenderTest;
import cc.vops.cheatbreaker.client.ui.mainmenu.element.GradientTextButton;
import cc.vops.cheatbreaker.client.ui.overlay.SocialOverlayScreen;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import cc.vops.cheatbreaker.client.util.Sounds;
import cc.vops.cheatbreaker.client.util.friend.FriendsManager;
import com.mojang.realmsclient.RealmsMainScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;

public class MainMenu extends MainMenuBase {
    private final Identifier outerLogo = CheatBreaker.asset("logo_255_outer.png");
    private final Identifier innerLogo = CheatBreaker.asset("logo_108_inner.png");
    private final GradientTextButton singleplayerButton = new GradientTextButton("SINGLEPLAYER");
    private final GradientTextButton multiplayerButton = new GradientTextButton("MULTIPLAYER");
    private final GradientTextButton realmsButton = new GradientTextButton("REALMS");
    private final MinMaxFade logoPositionFade = new MinMaxFade(750L);
    private final CosineFade logoTurnAmount;
    private final MinMaxFade loadingScreenBackgroundFade = new MinMaxFade(400L);

    private static int loadCount;

    public MainMenu() {
        this.logoTurnAmount = new CosineFade(4000L);
    }

    @Override
    protected void initMenu() {
        super.initMenu();

        this.singleplayerButton.setElementSize(this.getScaledWidth() / 2.0f - (float)50, this.getScaledHeight() / 2.0f + (float)5, (float)100, 12);
        this.multiplayerButton.setElementSize(this.getScaledWidth() / 2.0f - (float)50, this.getScaledHeight() / 2.0f + (float)24, (float)100, 12);
        this.realmsButton.setElementSize(this.getScaledWidth() / 2.0f - (float)50, this.getScaledHeight() / 2.0f + (float)43, (float)100, 12);
        ++loadCount;
    }

    @Override
    protected void drawMenu(GuiGraphicsExtractor gfx, float mouseX, float mouseY, float delta) {
        if (this.isFirstOpened() && !this.logoPositionFade.hasStartTime()) {
            this.logoPositionFade.reset();
        }
        if (!(this.isFirstOpened() && !this.logoPositionFade.isExpired() || this.logoTurnAmount.hasStartTime())) {
            this.loadingScreenBackgroundFade.reset();
            this.logoTurnAmount.reset();
            this.logoTurnAmount.enableShouldResetOnceCalled();
        }

        super.drawMenu(gfx, mouseX, mouseY, delta);

        this.singleplayerButton.drawElement(gfx, mouseX, mouseY, true);
        this.multiplayerButton.drawElement(gfx, mouseX, mouseY, true);
        this.realmsButton.drawElement(gfx, mouseX, mouseY, true);

        GradientTextButton topButton = this.singleplayerButton;
        GradientTextButton bottomButton = this.realmsButton;

        RenderUtil.drawRect(
                gfx,
                (topButton.getX() - 20f),
                (this.getScaledHeight() / 2.0f - 80f),
                (topButton.getX() + topButton.getWidth() + 20f),
                (bottomButton.getY() + bottomButton.getHeight() + 14f),
                0x2F000000
        );

        float logoY = this.isFirstOpened() ? this.logoPositionFade.getCurrentValue() : 1.0f;
        this.drawCheatBreakerLogo(gfx, this.getScaledWidth(), this.getScaledHeight(), logoY);
    }

    private void drawCheatBreakerLogo(GuiGraphicsExtractor gfx, double dispWidth, double dispHeight, float f) {
        float halfSize = 27;
        double x = dispWidth / 2d - halfSize;
        double y = dispHeight / 2d - halfSize - (35f * f);

        gfx.pose().pushMatrix();
        gfx.pose().translate((float) x + halfSize, (float) y + halfSize);
        gfx.pose().rotate((float) Math.toRadians(180f * this.logoTurnAmount.getCurrentValue()));
        gfx.pose().translate(-halfSize, -halfSize);
        drawIcon(gfx, this.outerLogo, halfSize, 0f, 0f);
        gfx.pose().popMatrix();

        drawIcon(gfx, this.innerLogo, halfSize, (float) x, (float) y);
    }


    @Override
    protected boolean onMouseClicked(double mx, double my, int button) {
        if (super.onMouseClicked(mx, my, button)) return true;

        if (this.singleplayerButton.isMouseInside(mx, my)) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            this.minecraft.setScreen(new SelectWorldScreen(this));
            return true;
        } else if (this.multiplayerButton.isMouseInside(mx, my)) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            this.minecraft.setScreen(new JoinMultiplayerScreen(this));
            return true;
        } else if (this.realmsButton.isMouseInside(mx, my)) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            this.minecraft.setScreen(new RealmsMainScreen(this));
            return true;
        }

        // if click in bottom right in the screen and is fabric dev
        if (FabricLoader.getInstance().isDevelopmentEnvironment() && mx > this.getScaledWidth() - 50 && my > this.getScaledHeight() - 20) {
            this.minecraft.setScreen(new GuiRenderTest());
            return true;
        }

        return false;
    }

    public boolean isFirstOpened() {
        return loadCount <= 2;
    }
}
