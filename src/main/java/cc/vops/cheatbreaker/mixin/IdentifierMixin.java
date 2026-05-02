package cc.vops.cheatbreaker.mixin;

import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Identifier.class)
public class IdentifierMixin {
    @Inject(method = "assertValidPath", at = @At("HEAD"), cancellable = true)
    private static void injectAssertValidPath(String namespace, String path, CallbackInfoReturnable<String> cir) {
        if (namespace.equalsIgnoreCase("cheatbreaker")) {
            cir.setReturnValue(path);
        }
    }
}
