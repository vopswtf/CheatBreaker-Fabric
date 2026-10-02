package cc.vops.cheatbreaker.client.ui.mainmenu;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.fading.CosineFade;
import cc.vops.cheatbreaker.client.ui.fading.MinMaxFade;
import cc.vops.cheatbreaker.client.ui.mainmenu.dev.GuiRenderTest;
import cc.vops.cheatbreaker.client.ui.mainmenu.element.GradientTextButton;
import cc.vops.cheatbreaker.client.ui.mainmenu.element.TextButtonElement;
import cc.vops.cheatbreaker.client.ui.overlay.SocialOverlayScreen;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import cc.vops.cheatbreaker.client.util.Sounds;
import cc.vops.cheatbreaker.client.util.bridge.GuiBridge;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.friend.FriendsManager;
import cc.vops.cheatbreaker.mixin.ScreenAccessor;
import com.mojang.realmsclient.RealmsMainScreen;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;

import java.util.ArrayList;
import java.util.List;

public class MainMenu extends MainMenuBase {
    private final Identifier outerLogo = CheatBreaker.asset("logo_255_outer.png");
    private final Identifier innerLogo = CheatBreaker.asset("logo_108_inner.png");
    private final GradientTextButton singleplayerButton = new GradientTextButton("SINGLEPLAYER");
    private final GradientTextButton multiplayerButton = new GradientTextButton("MULTIPLAYER");
    private final GradientTextButton realmsButton = new GradientTextButton("REALMS");
    private final MinMaxFade logoPositionFade = new MinMaxFade(750L);
    private final CosineFade logoTurnAmount;
    private final MinMaxFade loadingScreenBackgroundFade = new MinMaxFade(400L);

    // need to support the other mods!
    private final TitleScreen titleScreen;
    private final List<TitleRenderable> renderables = new ArrayList<>();

    private static int loadCount;

    public MainMenu() {
        this.logoTurnAmount = new CosineFade(4000L);
        this.titleScreen = new TitleScreen();
    }

    private int lastRenderables = -1;

    @Override
    public void tick() {
        super.tick();
        titleScreen.tick();

        ScreenAccessor accessor = (ScreenAccessor) this.titleScreen;
        if (!accessor.getRenderables().isEmpty() && accessor.getRenderables().size() != lastRenderables) {
            lastRenderables = accessor.getRenderables().size();
            renderables.clear();


            for (Renderable renderable : accessor.getRenderables()) {
                if (renderable.getClass().getName().startsWith("net.minecraft")) continue;
                renderables.add(new TitleRenderable(renderable));
            }

            float x = 5;
            float y = ((float) this.getScaledHeight() / 2) - (renderables.size() * 22f / 2f);
            for (TitleRenderable renderable : renderables) {
                renderable.getButton().setElementSize(x, y, Fonts.robotoBold14.width(renderable.getButton().getText()) + 6f, 20);
                y += 22;
            }
        }
    }

    @Override
    protected void initMenu() {
        super.initMenu();
        titleScreen.init(width, height);
        renderables.clear();
        lastRenderables = -1;

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

        for (TitleRenderable renderable : this.renderables) {
            renderable.getButton().drawElement(gfx, mouseX, mouseY, true);
        }
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
            GuiBridge.setScreen(new SelectWorldScreen(this));
            return true;
        } else if (this.multiplayerButton.isMouseInside(mx, my)) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            GuiBridge.setScreen(new JoinMultiplayerScreen(this));
            return true;
        } else if (this.realmsButton.isMouseInside(mx, my)) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            GuiBridge.setScreen(new RealmsMainScreen(this));
            return true;
        }

        for (TitleRenderable renderable : this.renderables) {
            if (renderable.getButton().isMouseInside(mx, my)) {
                CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
                if (renderable.getRenderable() instanceof Button btn) {
                    double x = btn.getX() + btn.getWidth() / 2.0;
                    double y = btn.getY() + btn.getHeight() / 2.0;

                    btn.mouseClicked(new MouseButtonEvent(x, y, new MouseButtonInfo(button, 0)), false);
                }
                return true;
            }
        }

        // if click in bottom right in the screen and is fabric dev
        if (FabricLoader.getInstance().isDevelopmentEnvironment() && mx > this.getScaledWidth() - 50 && my > this.getScaledHeight() - 20) {
            GuiBridge.setScreen(new GuiRenderTest());
            return true;
        }

        return false;
    }

    public boolean isFirstOpened() {
        return loadCount <= 2;
    }

    @Getter
    private static class TitleRenderable implements Renderable {
        private final Renderable renderable;
        private final TextButtonElement button;

        public TitleRenderable(Renderable renderable) {
            this.renderable = renderable;

            if (renderable instanceof Button btn && !btn.getMessage().getString().isEmpty()) {
                this.button = new TextButtonElement(btn.getMessage().getString().toUpperCase());
            } else {
                this.button = new TextButtonElement(renderable.getClass().getSimpleName().toUpperCase());
            }
        }

        @Override
        public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            // IGNORE OTHER MODS!!!! :100:
        }
    }
}
