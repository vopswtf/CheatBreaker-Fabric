package cc.vops.cheatbreaker.mixin.freelook;

import cc.vops.cheatbreaker.CheatBreaker;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class CameraMixin {
    @Inject(method = "setRotation", at = @At("HEAD"), cancellable = true)
    public void setRotation(float f, float g, CallbackInfo ci) {
        if (CheatBreaker.getInstance().getGlobalSettings().isFreeLooking) {
            ci.cancel();
        }
    }
}
