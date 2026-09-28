package cc.vops.cheatbreaker.mixin;

import cc.vops.cheatbreaker.CheatBreaker;
import net.minecraft.client.ClientClockManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if >=26.3 {
/*@Mixin(ClientClockManager.ClientClockInstance.class)
*///? } else {
@Mixin(ClientClockManager.class)
//? }
public class ClientClockInstanceMixin {

    //? if >=26.3 {
    /*@Inject(method = "totalTicks", at = @At("HEAD"), cancellable = true)
    *///? } else {
    @Inject(method = "getTotalTicks", at = @At("HEAD"), cancellable = true)
    //? }
    private void onGetGameTime(CallbackInfoReturnable<Long> cir) {
        if (CheatBreaker.getInstance().getGlobalSettings().overrideWorldTime.getAsBoolean()) {
            cir.setReturnValue(CheatBreaker.getInstance().getGlobalSettings().worldTime.getAsInteger().longValue());
        }
    }
}
