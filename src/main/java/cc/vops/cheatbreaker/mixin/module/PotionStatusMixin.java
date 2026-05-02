package cc.vops.cheatbreaker.mixin.module;

import cc.vops.cheatbreaker.CheatBreaker;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEffectInstance.class)
public class PotionStatusMixin {
    @Inject(method = "showIcon", at = @At("HEAD"), cancellable = true)
    private void onRenderEffects(CallbackInfoReturnable<Boolean> cir) {
        if (CheatBreaker.getInstance().getModuleManager().potionStatus.isEnabled()) {
            cir.setReturnValue(false);
        }
    }
}
