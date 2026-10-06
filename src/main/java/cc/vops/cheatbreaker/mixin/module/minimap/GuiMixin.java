package cc.vops.cheatbreaker.mixin.module.minimap;

import cc.vops.cheatbreaker.CheatBreaker;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=26.2 {
import net.minecraft.client.gui.Hud;
@Mixin(Hud.class)
//? } else {
/*import net.minecraft.client.gui.Gui;
@Mixin(Gui.class)
*///? }

public class GuiMixin {
    @Inject(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;getSelectedItem()Lnet/minecraft/world/item/ItemStack;"))
    private void updateMinimap(CallbackInfo ci) {
        if (Minecraft.getInstance().level == null) return;
        if (CheatBreaker.getInstance().getModuleManager().minmap.isEnabled()) {
            CheatBreaker.getInstance().getModuleManager().minmap.minimap.updateMapView();
        }
    }


    @Inject(method = "displayScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void onDisplayScoreboardSidebar(GuiGraphicsExtractor GuiGraphicsExtractor, Objective objective, CallbackInfo ci) {
//        if (CheatBreaker.getInstance().getModuleManager().scoreboard.isEnabled()) {
//            ci.cancel();
//        }
        ci.cancel();
    }
}