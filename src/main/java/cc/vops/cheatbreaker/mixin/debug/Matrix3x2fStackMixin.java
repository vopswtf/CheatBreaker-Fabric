package cc.vops.cheatbreaker.mixin.debug;

import cc.vops.cheatbreaker.client.util.Matrix3x2fStackDebug;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Matrix3x2fStack.class)
public class Matrix3x2fStackMixin implements Matrix3x2fStackDebug {

    private final StackTraceElement[] cb$pushOrigins = new StackTraceElement[64];

    @Inject(method = "pushMatrix", at = @At("TAIL"))
    private void onPushMatrix(CallbackInfoReturnable<Matrix3x2fStack> cir) {
        Matrix3x2fStack self = (Matrix3x2fStack) (Object) this;
        int curr = ((Matrix3x2fStackAccessor) self).getCurr();
        int index = curr - 1;
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        this.cb$setPushOrigin(index, caller);
    }

    @Override
    public StackTraceElement cb$getPushOrigin(int index) {
        return cb$pushOrigins[index];
    }

    @Override
    public void cb$setPushOrigin(int index, StackTraceElement e) {
        cb$pushOrigins[index] = e;
    }
}