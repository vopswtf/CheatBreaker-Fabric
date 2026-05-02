package cc.vops.cheatbreaker.mixin.module.minimap;

import cc.vops.cheatbreaker.CheatBreaker;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;getSelectedItem()Lnet/minecraft/world/item/ItemStack;"))
    private void updateMinimap(CallbackInfo ci) {
        if (Minecraft.getInstance().level == null) return;
        if (CheatBreaker.getInstance().getModuleManager().minmap.isEnabled()) {
            CheatBreaker.getInstance().getModuleManager().minmap.minimap.updateMapView();
        }
    }
}