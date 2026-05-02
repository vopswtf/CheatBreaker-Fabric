package cc.vops.cheatbreaker.mixin;

import cc.vops.cheatbreaker.client.util.PauseUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public class PauseScreenMixin {
    @Shadow
    @Final @Mutable
    private static Component GAME;

    @Shadow @Final
    @Mutable
    private static Component PAUSED;

    static {
        GAME = Component.empty();
        PAUSED = Component.empty();
    }

    @Inject(method = "init", at = @At("HEAD"))
    public void init(CallbackInfo ci) {
        GAME = Component.empty();
        PAUSED = Component.empty();
        PauseUtil.fade.reset();
        PauseUtil.fade.enableShouldResetOnceCalled();
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    public void renderLogo(GuiGraphicsExtractor GuiGraphicsExtractor, int i, int j, float f, CallbackInfo ci) {
        PauseUtil.renderRotatingLogo((PauseScreen)(Object)this, GuiGraphicsExtractor);
    }

}
