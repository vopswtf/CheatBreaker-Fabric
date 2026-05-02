package cc.vops.cheatbreaker.mixin.module;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.module.type.ToggleSprintModule;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.entity.player.Input;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Input.class)
public class ToggleSprintMixin {
    @Mutable
    @Shadow @Final private boolean sprint;

    @Mutable
    @Shadow @Final private boolean shift;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void forceSprintTrue(boolean forward, boolean backward, boolean left, boolean right, boolean jump, boolean shift, boolean sprint, CallbackInfo ci) {
        ToggleSprintModule toggleSprintModule = CheatBreaker.getInstance().getModuleManager().toggleSprint;
        if (!toggleSprintModule.isEnabled()) return;

        if (ToggleSprintModule.sprintToggled) {
            this.sprint = true;
        }

        if (ToggleSprintModule.sneakToggled) {
            this.shift = true;
        }
    }
}
