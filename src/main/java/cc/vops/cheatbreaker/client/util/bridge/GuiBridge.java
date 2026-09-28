package cc.vops.cheatbreaker.client.util.bridge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;

public class GuiBridge {
    public static Screen getScreen() {
        //? if >=26.2 {
        /*return Minecraft.getInstance().gui.screen();
        *///? } else {
        return Minecraft.getInstance().screen;
        //? }
    }

    public static void setScreen(Screen screen) {
        //? if >=26.2 {
        /*Minecraft.getInstance().gui.setScreen(screen);
        *///? } else {
        Minecraft.getInstance().setScreen(screen);
        //? }
    }

    public static Overlay getOverlay() {
        //? if >=26.2 {
        /*return Minecraft.getInstance().gui.overlay();
        *///? } else {
        return Minecraft.getInstance().getOverlay();
        //? }
    }

    public static void setOverlay(Overlay overlay) {
        //? if >=26.2 {
        /*Minecraft.getInstance().gui.setOverlay(overlay);
        *///? } else {
        Minecraft.getInstance().setOverlay(overlay);
        //? }
    }

    public static void extractDeferredSubtitles() {
        //? if >=26.2 {
        /*Minecraft.getInstance().gui.hud.extractDeferredSubtitles();
        *///? } else {
        Minecraft.getInstance().gui.extractDeferredSubtitles();
        //? }
    }

    public static BossHealthOverlay getBossOverlay() {
        //? if >=26.2 {
        /*return Minecraft.getInstance().gui.hud.getBossOverlay();
        *///? } else {
        return Minecraft.getInstance().gui.getBossOverlay();
        //? }
    }

    public static Identifier getMobEffectSprite(Holder<MobEffect> effectHolder) {
        //? if >=26.2 {
        /*return Minecraft.getInstance().gui.hud.getMobEffectSprite(effectHolder);
        *///? } else {
        return Minecraft.getInstance().gui.getMobEffectSprite(effectHolder);
        //? }
    }

    public static void ensureTextInput(Object owner) {
        // this will be disabled by setScreen, shouldnt need to cleanup

        //? if >=26.3 {
        /*Minecraft.getInstance().textInputManager().onTextInputFocusChange(owner, true);
        *///? }
    }


}
