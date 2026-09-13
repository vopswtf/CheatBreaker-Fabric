package cc.vops.overlay.mixin;

import cc.vops.overlay.OverlayModule;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public class FireHeightMixin {
    @Inject(method = "renderFire", at = @At("HEAD"))
    private static void renderFire(PoseStack pose, MultiBufferSource bufferSource, TextureAtlasSprite sprite, CallbackInfo ci) {
        if (OverlayModule.instance != null && OverlayModule.instance.isEnabled() && OverlayModule.instance.getFireHeight().getAsFloat() != 1f) {
            pose.translate(0f, OverlayModule.instance.getFireHeight().getAsFloat() - 1f, 0f);
        }
    }
}
