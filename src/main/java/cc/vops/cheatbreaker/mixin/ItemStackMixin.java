package cc.vops.cheatbreaker.mixin;

import cc.vops.cheatbreaker.CheatBreaker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "hasFoil", at = @At("HEAD"), cancellable = true)
    private void hasFoil(CallbackInfoReturnable<Boolean> cir) {
        if (CheatBreaker.getInstance().getGlobalSettings().shinyPots.getAsBoolean()) {
            if (((ItemStack) (Object) this).getItem() instanceof PotionItem) {
                cir.setReturnValue(true);
            }
        }
    }
}
