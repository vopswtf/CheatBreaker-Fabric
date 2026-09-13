package cc.vops.overlay.mixin;

import cc.vops.overlay.OverlayModule;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SubmitNodeStorage.ItemSubmit.class)
public class HideItemGlintMixin {
    @Inject(method = "foilType", at = @At("HEAD"), cancellable = true)
    public void foilType(CallbackInfoReturnable<ItemStackRenderState.FoilType> cir) {
        if (OverlayModule.getInstance() != null && OverlayModule.getInstance().isEnabled() && OverlayModule.getInstance().getEnchantmentGlint().getAsString().equals("Hide")) {
            cir.setReturnValue(ItemStackRenderState.FoilType.NONE);
        }
    }
}
