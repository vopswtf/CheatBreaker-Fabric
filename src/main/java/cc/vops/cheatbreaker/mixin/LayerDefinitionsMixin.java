package cc.vops.cheatbreaker.mixin;

import cc.vops.cheatbreaker.client.util.cosmetic.CosmeticModels;
import cc.vops.cheatbreaker.client.util.cosmetic.model.WingsModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(LayerDefinitions.class)
public class LayerDefinitionsMixin {
    @Inject(method = "createRoots", at = @At("RETURN"), cancellable = true)
    private static void onCreateRoots(CallbackInfoReturnable<Map<ModelLayerLocation, LayerDefinition>> cir) {
        Map<ModelLayerLocation, LayerDefinition> roots = cir.getReturnValue();

        Map<ModelLayerLocation, LayerDefinition> newRoots = new HashMap<>(roots);
        newRoots.put(CosmeticModels.DRAGON_WINGS, WingsModel.createLayer());
        cir.setReturnValue(newRoots);
    }
}
