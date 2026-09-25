package cc.vops.cheatbreaker.mixin;


import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.module.type.AutoHotKeyModule;
import cc.vops.cheatbreaker.client.util.bridge.GuiBridge;
import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.GlobalSettings;
import cc.vops.cheatbreaker.client.event.type.KeyboardEvent;
import cc.vops.cheatbreaker.client.ui.cosmetic.EmoteGUI;
import cc.vops.cheatbreaker.client.ui.module.CBModulesGui;
import cc.vops.cheatbreaker.client.ui.overlay.SocialOverlayScreen;
import cc.vops.cheatbreaker.client.ui.overlay.VoiceChatScreen;
import cc.vops.cheatbreaker.client.util.Keyboard;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void keyPress(long window, int action, KeyEvent event, CallbackInfo callbackInfo) {
        if (window == Minecraft.getInstance().getWindow().handle()) {
            if (action == InputConstants.PRESS && !Keyboard.isKeyDown(InputConstants.KEY_F3)) {
                Keyboard.setKeyEvent(event);
                CheatBreaker.getInstance().getEventBus().callEvent(new KeyboardEvent(event));

                if (event.key() == GlobalSettings.getKeyCode(CheatBreaker.getInstance().getGlobalSettings().openVoiceMenu)) {
                    if (minecraft.level != null && GuiBridge.getScreen() == null) {
                        GuiBridge.setScreen(new VoiceChatScreen());
                        return;
                    }
                }

                boolean shiftTab = event.hasShiftDown() && event.key() == InputConstants.KEY_TAB;

                if (GuiBridge.getScreen() instanceof SocialOverlayScreen) {
                    if (event.key() == InputConstants.KEY_ESCAPE || shiftTab) {
                        GuiBridge.setScreen(SocialOverlayScreen.previousScreen);
                        callbackInfo.cancel();
                        return;
                    }
                } else if (shiftTab) {
                    SocialOverlayScreen.previousScreen = GuiBridge.getScreen();
                    GuiBridge.setScreen(SocialOverlayScreen.getInstance());
                    return;
                }

                if (event.key() == GlobalSettings.getKeyCode(CheatBreaker.getInstance().getGlobalSettings().openMenu)) {
                    if (GuiBridge.getScreen() instanceof CBModulesGui) {
                        GuiBridge.setScreen(null);
                        return;
                    }

                    if (GuiBridge.getScreen() == null) {
                        GuiBridge.setScreen(new CBModulesGui());
                    }
                }

                if (event.key() == GlobalSettings.getKeyCode(CheatBreaker.getInstance().getGlobalSettings().emoteMenu)) { // hitboxes lol
                    if (GuiBridge.getScreen() == null) {
                        GuiBridge.setScreen(EmoteGUI.INSTANCE != null ? EmoteGUI.INSTANCE : new EmoteGUI(event.key()));
                    }
                }
            } else if (action == InputConstants.RELEASE) {
                Keyboard.setCharEvent(null);
                Keyboard.setKeyEvent(null);
            }
        }
    }

    @Inject(method = "keyPress", at = @At("HEAD"))
    private void ahk(long window, int action, KeyEvent event, CallbackInfo callbackInfo) {
        if (AutoHotKeyModule.getInstance() == null) return;
        if (minecraft.level == null || GuiBridge.getScreen() != null || action != InputConstants.PRESS || window != Minecraft.getInstance().getWindow().handle()) return;
        if (minecraft.player == null) return;
        if (!AutoHotKeyModule.getInstance().isEnabled()) return;

        AutoHotKeyModule.getInstance().keyPress(event.key());
    }

    @Inject(method = "charTyped", at = @At("HEAD"))
    private void charTyped(long handle, CharacterEvent event, CallbackInfo ci) {
        if (handle == Minecraft.getInstance().getWindow().handle()) {
            Keyboard.setCharEvent(event);
        }
    }
}
