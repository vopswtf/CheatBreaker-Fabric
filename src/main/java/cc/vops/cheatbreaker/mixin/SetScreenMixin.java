package cc.vops.cheatbreaker.mixin;

import cc.vops.cheatbreaker.client.ui.AbstractGui;
import cc.vops.cheatbreaker.client.ui.mainmenu.MainMenu;
import cc.vops.cheatbreaker.client.util.PauseUtil;
import cc.vops.cheatbreaker.client.util.bridge.GuiBridge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=26.2 {
import net.minecraft.client.gui.Gui;
@Mixin(Gui.class)
//? } else {
/*@Mixin(Minecraft.class)
*///? }

public class SetScreenMixin {
    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void onSetScreen(Screen screen, CallbackInfo ci) {
        if (screen instanceof TitleScreen || (screen == null && Minecraft.getInstance().level == null)) {
            ci.cancel();
            GuiBridge.setScreen(new MainMenu());
        }

        if (screen instanceof PauseScreen) {
            PauseUtil.fade.reset();
        }
    }
}
