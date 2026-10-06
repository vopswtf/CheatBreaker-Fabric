package cc.vops.cheatbreaker.mixin.module.overlay;

import cc.vops.cheatbreaker.client.module.type.OverlayModule;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if >=26.2 {
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
@Mixin(ItemFeatureRenderer.class)
//? } else {
/*import net.minecraft.client.renderer.SubmitNodeStorage;
@Mixin(SubmitNodeStorage.ItemSubmit.class)
*///? }

public class HideItemGlintMixin {


    //? if >=26.3 {
    /*@ModifyVariable(
            method = "prepareMainSubmit",
            at = @At(
                    value = "STORE",
                    ordinal = 0
            ),
            name = "foilType"
    )
    private ItemStackRenderState.FoilType forceFoilTypeNone(ItemStackRenderState.FoilType foilType) {
        if (OverlayModule.getInstance() != null && OverlayModule.getInstance().isEnabled() && OverlayModule.getInstance().getEnchantmentGlint().getAsString().equals("Hide")) {
            return ItemStackRenderState.FoilType.NONE;
        }

        return foilType;
    }
    *///? } else if >=26.2 {
    /*@ModifyVariable(
            method = "prepareSubmit",
            at = @At("HEAD"),
            argsOnly = true,
            index = 2
    )*/
    private boolean forceFoilFalse(boolean foil) {
        if (OverlayModule.getInstance() != null && OverlayModule.getInstance().isEnabled() && OverlayModule.getInstance().getEnchantmentGlint().getAsString().equals("Hide")) {
            return false;
        }

        return foil;
    }
    //? } else {
    /*@Inject(method = "foilType", at = @At("HEAD"), cancellable = true)
    public void foilType(CallbackInfoReturnable<ItemStackRenderState.FoilType> cir) {
        if (OverlayModule.getInstance() != null && OverlayModule.getInstance().isEnabled() && OverlayModule.getInstance().getEnchantmentGlint().getAsString().equals("Hide")) {
            cir.setReturnValue(ItemStackRenderState.FoilType.NONE);
        }
    }
    *///? }
}
