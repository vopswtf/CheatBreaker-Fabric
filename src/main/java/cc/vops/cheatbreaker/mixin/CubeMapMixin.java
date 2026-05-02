package cc.vops.cheatbreaker.mixin;

import cc.vops.cheatbreaker.CheatBreaker;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// panorama
@Mixin(CubeMap.class)
public class CubeMapMixin {
    @Unique
    private static final Identifier CUSTOM_LOCATION = CheatBreaker.asset("panorama/panorama");

    @Shadow
    @Mutable
    @Final
    private Identifier location;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(Identifier original, CallbackInfo ci) {
        this.location = CUSTOM_LOCATION;
    }
}