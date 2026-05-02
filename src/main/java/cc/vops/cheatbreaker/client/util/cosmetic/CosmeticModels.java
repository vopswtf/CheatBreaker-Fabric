package cc.vops.cheatbreaker.client.util.cosmetic;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.util.cosmetic.model.WingsModel;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.model.geom.ModelLayerLocation;

public class CosmeticModels {
    public static WingsModel WINGS;

    public static final ModelLayerLocation DRAGON_WINGS = new ModelLayerLocation(
            CheatBreaker.asset("dragon_wings"), "main"
    );

    public static void bakeModels() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (CosmeticModels.WINGS == null) {
                try {
                    CosmeticModels.WINGS = new WingsModel(client.getEntityModels().bakeLayer(CosmeticModels.DRAGON_WINGS));
                } catch (IllegalArgumentException e) {
                }
            }
        });
    }
}