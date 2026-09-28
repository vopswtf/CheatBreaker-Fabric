package cc.vops.cheatbreaker.mixin.module.overlay;

import cc.vops.cheatbreaker.client.module.type.OverlayModule;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EquipmentLayerRenderer.class)
public class HideArmorGlintMixin {

    //? if >=26.3 {
    /*@ModifyVariable(
            method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V",
            at = @At("STORE"),
            name = "hasFoil"
    )
    private boolean hideArmorGlint(boolean hasFoil) {
        if (OverlayModule.getInstance() != null
                && OverlayModule.getInstance().isEnabled()
                && OverlayModule.getInstance().getEnchantmentGlint().getAsString().equals("Hide")) {
            return false;
        }

        return hasFoil;
    }
    *///? } else {
    @ModifyVariable(
            method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V",
            at = @At("STORE"),
            name = "renderFoil"
    )
    private boolean makeRenderFoilAlwaysFalse(boolean original) {
        if (OverlayModule.getInstance() != null && OverlayModule.getInstance().isEnabled() && OverlayModule.getInstance().getEnchantmentGlint().getAsString().equals("Hide")) {
            return false;
        }
        return original;
    }
    //? }
}
