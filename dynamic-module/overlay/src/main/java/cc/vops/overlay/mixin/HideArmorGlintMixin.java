package cc.vops.overlay.mixin;

import cc.vops.overlay.OverlayModule;
import net.minecraft.client.renderer.SubmitNodeStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SubmitNodeStorage.ModelPartSubmit.class)
public class HideArmorGlintMixin {
    @Inject(method = "hasFoil", at = @At("HEAD"), cancellable = true)
    public void foilType(CallbackInfoReturnable<Boolean> cir) {
        if (OverlayModule.getInstance() != null && OverlayModule.getInstance().isEnabled() && OverlayModule.getInstance().getEnchantmentGlint().getAsString().equals("Hide")) {
            cir.setReturnValue(false);
        }
    }
}
