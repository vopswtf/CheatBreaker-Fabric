package cc.vops.cheatbreaker.client.ui.mainmenu;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.AbstractGui;
import cc.vops.cheatbreaker.client.ui.mainmenu.cosmetics.GuiCosmetics;
import cc.vops.cheatbreaker.client.ui.mainmenu.element.TextButtonElement;
import cc.vops.cheatbreaker.client.ui.fading.ColorFade;
import cc.vops.cheatbreaker.client.ui.mainmenu.element.IconButtonElement;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainMenuBase extends AbstractGui {
    private final ColorFade cbTextShadowFade;
    private final TextButtonElement optionsButton;
    private final TextButtonElement cosmeticsButton;
    private final IconButtonElement languageButton;
    private final IconButtonElement exitButton;
    private final Identifier logo = CheatBreaker.asset("logo_42.png");
    @Getter
    private final List<Account> accounts = new ArrayList<>();
    private final AccountList accountElement;
    private float accountButtonWidth;


    public MainMenuBase() {
        this.cbTextShadowFade = new ColorFade(0xF000000, -16777216);
        this.optionsButton = new TextButtonElement("OPTIONS");
        this.cosmeticsButton = new TextButtonElement("COSMETICS");
        this.exitButton = new IconButtonElement(CheatBreaker.asset("icons/delete-64.png"));
        this.languageButton = new IconButtonElement(6, CheatBreaker.asset("icons/globe-24.png"));

        if (accounts.isEmpty()) {
            accounts.add(new Account(Minecraft.getInstance().getUser().getName(), Minecraft.getInstance().getUser().getProfileId()));
        }

        this.accountButtonWidth = Fonts.robotoRegular13.getStringWidth(Minecraft.getInstance().getUser().getName());
        this.accountElement = new AccountList(this, accounts.get(0).getUsername(), accounts.get(0).getHeadLocation());
    }

    @Override
    public boolean doBlur() {
        return true;
    }

    @Override
    protected void initMenu() {
        this.optionsButton.setElementSize((float) 123, (float) 8, (float) 42, 20);
        this.cosmeticsButton.setElementSize((float) 166, (float) 8, (float) 48, 20);
        this.exitButton.setElementSize(this.getScaledWidth() - (float) 30, (float) 7, (float) 24, 17);
        this.languageButton.setElementSize(this.getScaledWidth() / 2.0f - (float) 13, this.getScaledHeight() - (float) 17, (float) 26, 18);
        this.accountElement.setElementSize(this.getScaledWidth() - (float) 35 - this.accountElement.getWidth(this.accountButtonWidth), (float) 7, this.accountElement.getWidth(this.accountButtonWidth), 17);

    }

    @Override
    protected void drawMenu(GuiGraphicsExtractor gfx, float mouseX, float mouseY, float delta) {
        RenderUtil.drawGradientRect(gfx, 0, 0, this.getScaledWidth(), this.getScaledHeight(), 0x33FFFFFF, 0x2FFFFFFF);
        RenderUtil.drawGradientRect(gfx, 0, 0, this.getScaledWidth(), 160, -553648128, 0);

        boolean isOverCheatBreakerText = mouseX < this.optionsButton.getX() && mouseY < 30;

        Color cheatBreakerTextColor = this.cbTextShadowFade.get(isOverCheatBreakerText);

        RenderUtil.drawString(
                gfx,
                Fonts.robotoRegular24,
                "CheatBreaker",
                36, 11,
                cheatBreakerTextColor.getRGB()
        );

        RenderUtil.drawString(
                gfx,
                Fonts.robotoRegular24,
                "CheatBreaker",
                35, 10,
                -1
        );

        RenderUtil.drawIcon(gfx, this.logo, (float) 10, (float) 8, (float) 6);

        String version = FabricLoader.getInstance().getModContainer("cheatbreaker").orElseThrow().getMetadata().getVersion().getFriendlyString();
        RenderUtil.drawStringWithShadow(
                gfx,
                Fonts.playRegular18,
                "CheatBreaker Fabric (" + version + "/fabric)",
                5,
                (this.getScaledHeight() - 14),
                -1879048193
        );

        String copyrightText = "Copyright Mojang AB. Do not distribute!";
        RenderUtil.drawStringWithShadow(
                gfx,
                Fonts.playRegular18,
                copyrightText,
                (this.getScaledWidth() - Fonts.playRegular18.width(copyrightText) - 5),
                (this.getScaledHeight() - 14),
                -1879048193
        );

        this.exitButton.drawElement(gfx, mouseX, mouseY, true);
        this.optionsButton.drawElement(gfx, mouseX, mouseY, true);
        this.cosmeticsButton.drawElement(gfx, mouseX, mouseY, true);
        this.accountElement.drawElement(gfx, mouseX, mouseY, true);
        if (mc.screen instanceof MainMenuBase) this.languageButton.drawElement(gfx, mouseX, mouseY, true);
    }

    @Override
    protected boolean onMouseClicked(double mx, double my, int button) {
        if (this.optionsButton.isMouseInside(mx, my)) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.minecraft.setScreen(new OptionsScreen(this.minecraft.screen, this.minecraft.options, false));
            return true;
        } else if (this.exitButton.isMouseInside(mx, my)) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.minecraft.stop();
            return true;
        } else if (this.languageButton.isMouseInside(mx, my)) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.minecraft.setScreen(new LanguageSelectScreen(this.minecraft.screen, this.minecraft.options, this.minecraft.getLanguageManager()));
            return true;
        } else if (this.cosmeticsButton.isMouseInside(mx, my)) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.minecraft.setScreen(new GuiCosmetics());
            return true;
        }

        return false;
    }

    @Override
    protected void onMouseReleased(double mx, double my, int button) {

    }
}
