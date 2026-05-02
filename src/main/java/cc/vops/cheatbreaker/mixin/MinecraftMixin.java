package cc.vops.cheatbreaker.mixin;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.event.type.GameTickEvent;
import cc.vops.cheatbreaker.client.event.type.WindowTickEvent;
import cc.vops.cheatbreaker.client.ui.mainmenu.MainMenu;
import cc.vops.cheatbreaker.client.ui.overlay.Alert;
import cc.vops.cheatbreaker.client.ui.overlay.SocialOverlayScreen;
import cc.vops.cheatbreaker.client.util.PauseUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow public abstract void setScreen(@Nullable Screen screen);

    @Shadow @Nullable public ClientLevel level;

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void onSetScreen(Screen screen, CallbackInfo ci) {
        if (screen instanceof TitleScreen || (screen == null && this.level == null)) {
            ci.cancel();
            this.setScreen(new MainMenu());
        }

        if (screen instanceof PauseScreen) {
            PauseUtil.fade.reset();
        }
    }
    @Inject(method = "createTitle", at = @At("RETURN"), cancellable = true)
    private void modifyWindowTitle(CallbackInfoReturnable<String> cir) {
        String originalTitle = cir.getReturnValue();
        if (originalTitle != null && originalTitle.startsWith("Minecraft")) {
            cir.setReturnValue(originalTitle.replaceFirst("Minecraft", "CheatBreaker"));
        }
    }

    @Inject(method = "<init>", at = @At("TAIL") )
    private void onInit(CallbackInfo ci) {
        CheatBreaker.getInstance().onLoad();

    }

    @Inject(method = "runTick", at = @At("TAIL"))
    private void onRunTick(CallbackInfo ci) {
        CheatBreaker.getInstance().getEventBus().callEvent(new WindowTickEvent());
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        SocialOverlayScreen.getInstanceOpt().ifPresent(SocialOverlayScreen::pollNotifications);
        CheatBreaker.getInstance().getEventBus().callEvent(new GameTickEvent());
    }

    @Inject(method = "resizeGui", at = @At("TAIL"))
    private void onResizeDisplay(CallbackInfo ci) {
        SocialOverlayScreen.getInstanceOpt().ifPresent(s -> {
            for (Alert alert : s.getAlertList()) {
                alert.resize();
            }

            s.resize(Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight());
        });
    }
}
