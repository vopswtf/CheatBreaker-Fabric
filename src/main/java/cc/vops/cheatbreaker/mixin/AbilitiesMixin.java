package cc.vops.cheatbreaker.mixin;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.module.type.ToggleSprintModule;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Abilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Abilities.class)
public class AbilitiesMixin {
    @Shadow
    public boolean instabuild;

    @Inject(method = "getFlyingSpeed", at = @At("HEAD"), cancellable = true)
    private void onGetFlyingSpeed(CallbackInfoReturnable<Float> cir) {
        if (CheatBreaker.getInstance().getModuleManager().toggleSprint.isEnabled()) {
            if (Minecraft.getInstance().options.keySprint.isDown() && this.instabuild) {
                cir.setReturnValue(0.05f * Float.valueOf((Integer)ToggleSprintModule.flyBoostAmount.getValue()));
            }
        }
    }
}
