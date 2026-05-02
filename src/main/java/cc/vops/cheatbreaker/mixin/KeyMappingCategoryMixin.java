package cc.vops.cheatbreaker.mixin;

import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(KeyMapping.Category.class)
public abstract class KeyMappingCategoryMixin {
    @Inject(method = "label", at = @At("HEAD"), cancellable = true)
    private void cheatBreaker$customLabel(CallbackInfoReturnable<Component> cir) {
        KeyMapping.Category self = (KeyMapping.Category) (Object) this;
        if (self.id().getNamespace().equalsIgnoreCase("cheatbreaker")) {
            cir.setReturnValue(Component.literal("§cCheat§fBreaker"));
        }
    }
}