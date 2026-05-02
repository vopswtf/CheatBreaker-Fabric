package cc.vops.cheatbreaker.mixin;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.event.type.KeepAliveEvent;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonPacketListenerImpl.class)
public class NetHandlerMixin {
    @Inject(method = "handleKeepAlive", at = @At("HEAD"))
    public void handleKeepAlive(ClientboundKeepAlivePacket packet, CallbackInfo ci) {
        CheatBreaker.getInstance().getEventBus().callEvent(new KeepAliveEvent());
    }
}
