package cc.vops.cheatbreaker.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabMixin {
    @Inject(method = "extractPingIcon", at = @At("HEAD"), cancellable = true)
    private void onInit(GuiGraphicsExtractor GuiGraphicsExtractor, int i, int j, int k, PlayerInfo playerInfo, CallbackInfo ci) {
        if (playerInfo.getLatency() <= 0) {
            ci.cancel();
        }
    }
}
