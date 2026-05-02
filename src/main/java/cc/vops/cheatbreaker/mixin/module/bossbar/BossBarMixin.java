package cc.vops.cheatbreaker.mixin.module.bossbar;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.BossHealthOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossHealthOverlay.class)
public class BossBarMixin {
    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    public void onRender(GuiGraphicsExtractor gfx, CallbackInfo ci) {
        ci.cancel();
    }
}
