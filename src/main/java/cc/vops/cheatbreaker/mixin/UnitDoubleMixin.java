package cc.vops.cheatbreaker.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.Optional;

@Mixin(OptionInstance.UnitDouble.class)
public abstract class UnitDoubleMixin {

    @Inject(
            method = "validateValue",
            at = @At("HEAD"),
            cancellable = true
    )
    private void removeValidation(Double input, CallbackInfoReturnable<Optional<Double>> cir) {
        cir.setReturnValue(Optional.of(input));
    }
}