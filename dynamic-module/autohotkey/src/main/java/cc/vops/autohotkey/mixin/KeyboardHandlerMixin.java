package cc.vops.autohotkey.mixin;

import cc.vops.autohotkey.AutoHotKeyModule;
import cc.vops.cheatbreaker.client.config.Setting;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "keyPress", at = @At("HEAD"))
    private void keyPress(long window, int action, KeyEvent event, CallbackInfo callbackInfo) {
        if (AutoHotKeyModule.getInstance() == null) return;
        if (minecraft.level == null || minecraft.screen != null || action != 1 || window != Minecraft.getInstance().getWindow().handle()) return;
        if (minecraft.player == null) return;
        if (!AutoHotKeyModule.getInstance().isEnabled()) return;

        for (Setting hotkey : AutoHotKeyModule.getInstance().hotkeys) {
            if (event.key() == hotkey.getKeyCode()) {
                // not sure if this is reliable, maybe swap with connection.sendCommand
                if (hotkey.getAsString().startsWith("/")) {
                    minecraft.player.connection.send(new ServerboundChatCommandPacket(hotkey.getAsString().substring(1)));
                } else {
                    minecraft.player.connection.sendChat(hotkey.getAsString());
                }
            }
        }
    }
}
