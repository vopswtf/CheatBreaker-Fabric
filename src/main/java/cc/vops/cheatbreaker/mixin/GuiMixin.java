package cc.vops.cheatbreaker.mixin;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.event.type.GuiDrawEvent;
import cc.vops.cheatbreaker.client.module.type.OverlayModule;
import cc.vops.cheatbreaker.client.ui.AbstractGui;
import cc.vops.cheatbreaker.client.ui.mainmenu.MainMenu;
import cc.vops.cheatbreaker.client.ui.module.CBModulePlaceGui;
import cc.vops.cheatbreaker.client.ui.module.CBModulesGui;
import cc.vops.cheatbreaker.client.util.PauseUtil;
import cc.vops.cheatbreaker.client.util.bridge.GuiBridge;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.border.WorldBorder;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.Gui;

//? if >=26.2 {
import net.minecraft.client.gui.Hud;
@Mixin(Hud.class)
//? } else {
/*@Mixin(Gui.class)
*///? }
public class GuiMixin {

    @Shadow @Final private static Identifier VIGNETTE_LOCATION;

    //? if >=26.2 {
    @Shadow private boolean isHidden;
    @Inject(
            method = "extractDemoOverlay",
            at = @At("HEAD")
    )
    private void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!isHidden && !(GuiBridge.getScreen() instanceof CBModulesGui) && !(GuiBridge.getScreen() instanceof CBModulePlaceGui)) {
            CheatBreaker.getInstance().getEventBus().callEvent(new GuiDrawEvent(graphics, deltaTracker));
        }
    }
    //? } else {
    /*@Inject(method = "extractRenderState", at = @At("TAIL"))
    public void render(GuiGraphicsExtractor gfx, DeltaTracker deltaTracker, CallbackInfo ci) {
        CheatBreaker.getInstance().getEventBus().callEvent(new GuiDrawEvent(gfx, deltaTracker));
    }
    *///? }


    @Inject(method = "extractVignette", at = @At("HEAD"), cancellable = true)
    private void extractVignette(final GuiGraphicsExtractor graphics, final @Nullable Entity camera, CallbackInfo ci) {
        if (GuiBridge.getScreen() instanceof AbstractGui) {
            ci.cancel();
            return;
        }

        if (Minecraft.getInstance().level == null) return;

        OverlayModule module = OverlayModule.getInstance();
        if (module == null || !module.isEnabled() || module.getVignette().getAsString().equals("Vanilla")) return;

        ci.cancel();

        if (module.getVignette().getAsString().equals("Hide")) return;

        graphics.blit(RenderPipelines.VIGNETTE, VIGNETTE_LOCATION, 0, 0, 0.0F, 0.0F, graphics.guiWidth(), graphics.guiHeight(), graphics.guiWidth(), graphics.guiHeight(), module.getVignetteColor());

    }
}
