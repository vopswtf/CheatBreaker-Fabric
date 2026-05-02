package cc.vops.cheatbreaker.mixin;

import cc.vops.cheatbreaker.CheatBreaker;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ClientPacketListener.class)
public abstract class ClientPlayNetworkHandlerMixin implements ClientGamePacketListener {
//    @Inject(method = "handlePlayerInfoUpdate", at = @At("RETURN"))
//    public void onPlayerAdd(ClientboundPlayerInfoUpdatePacket packet, CallbackInfo ci) {
//        if (packet.actions().contains(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER)) {
//            for (ClientboundPlayerInfoUpdatePacket.Entry entry : packet.entries()) {
//                CheatBreaker.getInstance().getAssetsWebSocket().sendPlayerJoin(entry.profileId());
//            }
//        }
//    }
//
//    @Inject(method = "handlePlayerInfoRemove", at = @At("RETURN"))
//    public void onPlayerRemove(ClientboundPlayerInfoRemovePacket packet, CallbackInfo ci) {
//        for (UUID entry : packet.profileIds()) {
//            CheatBreaker.getInstance().getAssetsWebSocket().playerLeave(entry);
//        }
//    }
}
