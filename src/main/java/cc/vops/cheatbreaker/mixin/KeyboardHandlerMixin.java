package cc.vops.cheatbreaker.mixin;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.GlobalSettings;
import cc.vops.cheatbreaker.client.event.type.KeyboardEvent;
import cc.vops.cheatbreaker.client.ui.AbstractGui;
import cc.vops.cheatbreaker.client.ui.cosmetic.EmoteGUI;
import cc.vops.cheatbreaker.client.ui.module.CBModulesGui;
import cc.vops.cheatbreaker.client.ui.overlay.SocialOverlayScreen;
import cc.vops.cheatbreaker.client.ui.overlay.VoiceChatScreen;
import cc.vops.cheatbreaker.client.util.Keyboard;
import cc.vops.cheatbreaker.client.util.Sounds;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void keyPress(long window, int action, KeyEvent event, CallbackInfo callbackInfo) {
        if (window == Minecraft.getInstance().getWindow().handle()) {
            if (action == GLFW.GLFW_PRESS) {
                CheatBreaker.getInstance().getEventBus().callEvent(new KeyboardEvent(event));

                if (event.key() == GlobalSettings.getKeyCode(CheatBreaker.getInstance().getGlobalSettings().openVoiceMenu)) {
                    if (minecraft.level != null && minecraft.screen == null) {
                        minecraft.setScreen(new VoiceChatScreen());
                        return;
                    }
                }

                boolean shiftTab = event.hasShiftDown() && event.key() == GLFW.GLFW_KEY_TAB;

                if (Minecraft.getInstance().screen instanceof SocialOverlayScreen) {
                    if (event.key() == GLFW.GLFW_KEY_ESCAPE || shiftTab) {
                        Minecraft.getInstance().setScreen(SocialOverlayScreen.previousScreen);
                        callbackInfo.cancel();
                        return;
                    }
                } else if (shiftTab) {
                    SocialOverlayScreen.previousScreen = Minecraft.getInstance().screen;
                    Minecraft.getInstance().setScreen(SocialOverlayScreen.getInstance());
                    return;
                }

                if (event.key() == GlobalSettings.getKeyCode(CheatBreaker.getInstance().getGlobalSettings().openMenu)) {
                    if (minecraft.screen instanceof CBModulesGui) {
                        minecraft.setScreen(null);
                        return;
                    }

                    if (minecraft.screen == null) {
                        minecraft.setScreen(new CBModulesGui());
                    }
                }

                if (event.key() == GLFW.GLFW_KEY_B && !Keyboard.isKeyDown(GLFW.GLFW_KEY_F3)) { // hitboxes lol
                    if (minecraft.screen == null) {
                        minecraft.setScreen(EmoteGUI.INSTANCE != null ? EmoteGUI.INSTANCE : new EmoteGUI(event.key()));
                    }
                }
            } else if (action == GLFW.GLFW_RELEASE) {
//                Keyboard.eventKeyState = false;
//                Keyboard.eventKey = -1;
            }
        }
    }
}
