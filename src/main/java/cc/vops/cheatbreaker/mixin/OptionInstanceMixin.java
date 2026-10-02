package cc.vops.cheatbreaker.mixin;

import cc.vops.cheatbreaker.CheatBreaker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OptionInstance.class)
public class OptionInstanceMixin<T> {
    @Shadow
    @Final
    private Component caption;

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    public void getGamma(CallbackInfoReturnable<Double> info) {
        if (CheatBreaker.getInstance() == null) return;
        if (CheatBreaker.getInstance().getGlobalSettings() == null) return;
        if (Minecraft.getInstance().level == null) return;
        if (CheatBreaker.getInstance().getGlobalSettings().fullBright.getAsBoolean() && caption.getContents() instanceof TranslatableContents translatableTextContent && translatableTextContent.getKey().equals("options.gamma")) {
            info.setReturnValue(10000000D);
        }
    }
}
