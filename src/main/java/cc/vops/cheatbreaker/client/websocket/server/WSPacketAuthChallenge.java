package cc.vops.cheatbreaker.client.websocket.server;

import cc.vops.cheatbreaker.client.util.AuthUtil;
import cc.vops.cheatbreaker.client.util.ByteBufWrapper;
import cc.vops.cheatbreaker.client.websocket.AssetsWebSocket;
import cc.vops.cheatbreaker.client.websocket.WSPacket;
import cc.vops.cheatbreaker.client.websocket.client.WSPacketAuthChallengeResponse;
import cc.vops.cheatbreaker.mixin.MinecraftAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ProfileKeyPairManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.PrivateKey;
import java.util.UUID;

public class WSPacketAuthChallenge extends WSPacket {
    private UUID nonce;

    @Override
    public void write(ByteBufWrapper var1) {

    }

    @Override
    public void read(ByteBufWrapper var1) throws IOException {
        this.nonce = UUID.fromString(var1.readString());
    }

    @Override
    public void handle(AssetsWebSocket socket) {
        ((MinecraftAccessor) Minecraft.getInstance()).getRawKPManager().prepareKeyPair().whenComplete((optKey, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            if (optKey.isEmpty()) {
                System.err.println("No profile key pair available");
                return;
            }

            try {
                PrivateKey privateKey = optKey.get().privateKey();

                ByteBuffer buf = ByteBuffer.allocate(16);
                buf.putLong(nonce.getMostSignificantBits());
                buf.putLong(nonce.getLeastSignificantBits());
                byte[] nonceBytes = buf.array();

                byte[] signature = AuthUtil.sign(nonceBytes, privateKey);

                socket.send(new WSPacketAuthChallengeResponse(nonce, signature));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
