package cc.vops.cheatbreaker.mixin.module.overlay;

import cc.vops.cheatbreaker.client.module.type.OverlayModule;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if <26.2 {
import net.minecraft.client.renderer.MultiBufferSource;
//? }

@Mixin(ScreenEffectRenderer.class)
public class FireHeightMixin {
    //? if >=26.2 {
    /*@Inject(method = "buildFireQuad", at = @At("HEAD"))
    private static void renderFire(TextureAtlasSprite sprite, VertexConsumer builder, Matrix4f pose, CallbackInfo ci) {
        if (OverlayModule.instance != null && OverlayModule.instance.isEnabled() && OverlayModule.instance.getFireHeight().getAsFloat() != 1f) {
            pose.translate(0f, OverlayModule.instance.getFireHeight().getAsFloat() - 1f, 0f);
        }
    }
    *///? } else {
    @Inject(method = "renderFire", at = @At("HEAD"))
    private static void renderFire(PoseStack pose, MultiBufferSource bufferSource, TextureAtlasSprite sprite, CallbackInfo ci) {
        if (OverlayModule.instance != null && OverlayModule.instance.isEnabled() && OverlayModule.instance.getFireHeight().getAsFloat() != 1f) {
            pose.translate(0f, OverlayModule.instance.getFireHeight().getAsFloat() - 1f, 0f);
        }
    }
    //? }
}
