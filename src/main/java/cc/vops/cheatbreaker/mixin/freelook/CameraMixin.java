package cc.vops.cheatbreaker.mixin.freelook;

import cc.vops.cheatbreaker.CheatBreaker;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Unique
    private boolean busy = false;

    @Shadow public abstract float xRot();
    @Shadow public abstract float yRot();

    @Inject(method = "setRotation", at = @At("HEAD"), cancellable = true)
    public void setRotation(float f, float g, CallbackInfo ci) {
        if (CheatBreaker.getInstance().getGlobalSettings().isFreeLooking && !busy) {
            ci.cancel();

            CameraAccessor accessor = (CameraAccessor) this;

            busy = true;
            try {
                accessor.setRot(yRot(), xRot());
            } finally {
                busy = false;
            }
        }
    }
}
