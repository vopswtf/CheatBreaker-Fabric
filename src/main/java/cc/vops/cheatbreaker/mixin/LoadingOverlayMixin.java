package cc.vops.cheatbreaker.mixin;

import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.LogoTexture;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.IntSupplier;

@Mixin(LoadingOverlay.class)
public class LoadingOverlayMixin {
    @Shadow
    @Final
    @Mutable
    private static IntSupplier BRAND_BACKGROUND;

    @Final
    @Shadow
    @Mutable
    public static Identifier MOJANG_STUDIOS_LOGO_LOCATION;

    @Shadow
    private float currentProgress;

    @Shadow
    private long fadeInStart;

    @Shadow
    private long fadeOutStart;

    @Shadow
    @Final
    private boolean fadeIn;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private ReloadInstance reload;

    @Unique
    private boolean reloaded = false;

    @Unique
    private static int replaceAlpha(int p_169325_, int p_169326_) {
        return p_169325_ & 16777215 | p_169326_ << 24;
    }

    static {
        BRAND_BACKGROUND = () -> -1;
        MOJANG_STUDIOS_LOGO_LOCATION = RenderUtil.LOGO;
    }


    @Inject(method = "registerTextures", at = @At("HEAD"), cancellable = true)
    private static void registerTexturesInject(TextureManager textureManager, CallbackInfo ci) {
        textureManager.registerAndLoad(RenderUtil.LOGO, new LogoTexture());
        ci.cancel();
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void renderInject(GuiGraphicsExtractor p_281839_, int p_282704_, int p_283650_, float p_283394_, CallbackInfo ci) {
        ci.cancel();

        int i = p_281839_.guiWidth();
        int j = p_281839_.guiHeight();
        long k = Util.getMillis();
        if (this.fadeIn && this.fadeInStart == -1L) {
            this.fadeInStart = k;
        }

        float f = this.fadeOutStart > -1L ? (float)(k - this.fadeOutStart) / 1000.0F : -1.0F;
        float f1 = this.fadeInStart > -1L ? (float)(k - this.fadeInStart) / 500.0F : -1.0F;
        float f2;
        if (f >= 1.0F) {
            if (this.minecraft.screen != null) {
                this.minecraft.screen.extractRenderStateWithTooltipAndSubtitles(p_281839_, 0, 0, p_283394_);
            } else {
                this.minecraft.gui.extractDeferredSubtitles();
            }

            int l = Mth.ceil((1.0F - Mth.clamp(f - 1.0F, 0.0F, 1.0F)) * 255.0F);
            p_281839_.nextStratum();
            p_281839_.fill(0, 0, i, j, replaceAlpha(BRAND_BACKGROUND.getAsInt(), l));
            f2 = 1.0F - Mth.clamp(f - 1.0F, 0.0F, 1.0F);
        } else if (this.fadeIn) {
            if (this.minecraft.screen != null && f1 < 1.0F) {
                this.minecraft.screen.extractRenderStateWithTooltipAndSubtitles(p_281839_, p_282704_, p_283650_, p_283394_);
            } else {
                this.minecraft.gui.extractDeferredSubtitles();
            }

            int j2 = Mth.ceil(Mth.clamp(f1, 0.15, 1.0) * 255.0);
            p_281839_.nextStratum();
            p_281839_.fill(0, 0, i, j, replaceAlpha(BRAND_BACKGROUND.getAsInt(), j2));
            f2 = Mth.clamp(f1, 0.0F, 1.0F);
        } else {
            this.minecraft.gameRenderer.getGameRenderState().guiRenderState.clearColorOverride = BRAND_BACKGROUND.getAsInt();
            f2 = 1.0F;
        }

        int l2 = (int)(p_281839_.guiWidth() * 0.5);
        int i1 = (int)(p_281839_.guiHeight() * 0.5);
        double d0 = Math.min(p_281839_.guiWidth() * 0.75, (double)p_281839_.guiHeight()) * 0.25;
        int j1 = (int)(d0 * 0.5);
        double d1 = d0 * 4.0;

        int k1 = (int)(d1 * 0.5);
        int baseColor = -1;
        int alpha = (f <= 1.0F) ? 255 : 0;
        int l1 = (baseColor & 0x00FFFFFF) | (alpha << 24);

//        p_281839_.blit(RenderPipelines.MOJANG_LOGO, MOJANG_STUDIOS_LOGO_LOCATION, l2 - k1, i1 - j1, -0.0625F, 0.0F, k1, (int)d0, 120, 60, 120, 120, l1);
//        p_281839_.blit(RenderPipelines.MOJANG_LOGO, MOJANG_STUDIOS_LOGO_LOCATION, l2, i1 - j1, 0.0625F, 60.0F, k1, (int)d0, 120, 60, 120, 120, l1);

        int screenW = p_281839_.guiWidth();
        int screenH = p_281839_.guiHeight();

        float logoScale = 27.0f * 2.0f;
        double d3 = (double)(screenW / 2) - (double)(logoScale / 2.0f);
        double d4 = (double)(screenH / 2) - (double)(logoScale / 2.0f);

        RenderUtil.drawIcon(p_281839_, RenderUtil.LOGO, (float) d3, (float) d4, logoScale, logoScale, l1);

        int i2 = (int)(p_281839_.guiHeight() * 0.8325);
        float f3 = this.reload.getActualProgress();
        this.currentProgress = Mth.clamp(this.currentProgress * 0.95F + f3 * 0.050000012F, 0.0F, 1.0F);
        if (f < 1.0F) {
            this.drawProgressBar(p_281839_, i / 2 - k1, i2 - 5, i / 2 + k1, i2 + 5, 1.0F - Mth.clamp(f, 0.0F, 1.0F));
        } else if (!reloaded) {
            Fonts.reloadFonts();
            reloaded = true;
        }

        if (f >= 2.0F) {
            this.minecraft.setOverlay(null);
        }
    }


    @Unique
    private void drawProgressBar(GuiGraphicsExtractor p_283125_, int p_96184_, int p_96185_, int p_96186_, int p_96187_, float p_96188_) {
        int i = Mth.ceil((p_96186_ - p_96184_ - 2) * this.currentProgress);
        int j = Math.round(p_96188_ * 255.0F);
        int k = -2473389;

        // background
        RenderUtil.drawRoundedRect(p_283125_, p_96184_ + 2, p_96185_ + 2, p_96186_ - 2, p_96187_ - 2, 5, -657931);

        // foreground
        RenderUtil.drawRoundedRect(p_283125_, p_96184_ + 2, p_96185_ + 2, p_96184_ + i, p_96187_ - 2, 5, k);
    }
}
