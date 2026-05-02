package cc.vops.cheatbreaker.client.websocket.client;

import cc.vops.cheatbreaker.client.util.ByteBufWrapper;
import cc.vops.cheatbreaker.client.websocket.AssetsWebSocket;
import cc.vops.cheatbreaker.client.websocket.WSPacket;
import net.minecraft.world.entity.player.ProfileKeyPair;
import net.minecraft.world.entity.player.ProfilePublicKey;

import java.time.Instant;

public class WSPacketHello extends WSPacket {
    private final Instant expiresAt;
    private final byte[] publicKey;
    private final byte[] keySignature;

    public WSPacketHello(ProfileKeyPair pair) {
        ProfilePublicKey.Data data = pair.publicKey().data();
        this.expiresAt = data.expiresAt();
        this.publicKey = data.key().getEncoded();
        this.keySignature = data.keySignature();
    }

    @Override
    public void write(ByteBufWrapper out) {
        out.writeLong(expiresAt.toEpochMilli());
        out.writeBytes(publicKey);
        out.writeBytes(keySignature);
    }

    @Override
    public void read(ByteBufWrapper in) {}

    @Override
    public void handle(AssetsWebSocket socket) {}
}
