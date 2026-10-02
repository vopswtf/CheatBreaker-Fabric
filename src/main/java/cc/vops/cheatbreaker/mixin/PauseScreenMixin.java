package cc.vops.cheatbreaker.mixin;

import cc.vops.cheatbreaker.client.ui.mainmenu.cosmetics.GuiCosmetics;
import cc.vops.cheatbreaker.client.util.PauseUtil;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin {
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

    @Shadow
    protected abstract Button openScreenButton(Component label, Supplier<Screen> screenSupplier);

    @Inject(
            method = "createPauseMenu",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/PauseScreen;getCustomAdditions()Ljava/util/Optional;"
            ),
            require = 1
    )
    private void addCosmetics(CallbackInfo ci, @Local GridLayout.RowHelper helper) {
        helper.addChild(
                this.openScreenButton(
                        Component.literal("Cosmetics"),
                        () -> new GuiCosmetics((PauseScreen) (Object) this)
                )
        );
        helper.addChild(
                this.openScreenButton(
                        Component.literal("Multiplayer"),
                        () -> new JoinMultiplayerScreen((PauseScreen) (Object) this)
                )
        );
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    public void renderLogo(GuiGraphicsExtractor GuiGraphicsExtractor, int i, int j, float f, CallbackInfo ci) {
        PauseUtil.renderRotatingLogo((PauseScreen)(Object)this, GuiGraphicsExtractor);
    }
}
