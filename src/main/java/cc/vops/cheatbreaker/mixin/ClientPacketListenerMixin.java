package cc.vops.cheatbreaker.mixin;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.event.type.CustomPacketPayloadEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    public void handleCustomPayload(CustomPacketPayload payload, CallbackInfo ci) {
        CheatBreaker.getInstance().getEventBus().callEvent(new CustomPacketPayloadEvent(payload));
        ci.cancel();
    }
}
