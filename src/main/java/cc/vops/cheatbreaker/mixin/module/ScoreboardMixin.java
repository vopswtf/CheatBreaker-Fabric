package cc.vops.cheatbreaker.mixin.module;

import cc.vops.cheatbreaker.CheatBreaker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class ScoreboardMixin {
    @Inject(method = "displayScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void onDisplayScoreboardSidebar(GuiGraphicsExtractor GuiGraphicsExtractor, Objective objective, CallbackInfo ci) {
//        if (CheatBreaker.getInstance().getModuleManager().scoreboard.isEnabled()) {
//            ci.cancel();
//        }
        ci.cancel();
    }
}
