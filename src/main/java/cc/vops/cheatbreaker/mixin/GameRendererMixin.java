package cc.vops.cheatbreaker.mixin;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.overlay.SocialOverlayScreen;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(
            method = "extractGui",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Gui;extractDeferredSubtitles()V",
                    shift = At.Shift.AFTER
            )
    )
    public void render(DeltaTracker deltaTracker, boolean shouldRenderLevel, boolean resourcesLoaded, CallbackInfo ci) {
        CheatBreaker.getInstance().getModuleManager().minmap.waypointRenderer.renderNames(deltaTracker);
    }

    @Inject(method = "renderLevel", at = @At("HEAD"))
    public void renderLevelHead(DeltaTracker deltaTracker, CallbackInfo ci) {
        if (CheatBreaker.getInstance().getGlobalSettings().fullBright.getAsBoolean() && minecraft.options.gamma().get() <= 1.0) {
            minecraft.options.gamma().set(10000000D);
        } else if (!CheatBreaker.getInstance().getGlobalSettings().fullBright.getAsBoolean() && minecraft.options.gamma().get() > 1.0) {
            minecraft.options.gamma().set(1.0D);
        }
    }
}
