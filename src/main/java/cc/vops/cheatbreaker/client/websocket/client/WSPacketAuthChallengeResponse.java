package cc.vops.cheatbreaker.client.websocket.client;

import cc.vops.cheatbreaker.client.util.ByteBufWrapper;
import cc.vops.cheatbreaker.client.websocket.AssetsWebSocket;
import cc.vops.cheatbreaker.client.websocket.WSPacket;

import java.util.UUID;

public class WSPacketAuthChallengeResponse extends WSPacket {
    private final UUID nonce;
    private final byte[] signature;

    public WSPacketAuthChallengeResponse(UUID nonce, byte[] signature) {
        this.nonce = nonce;
        this.signature = signature;
    }

    @Override
    public void write(ByteBufWrapper out) {
        out.writeString(nonce.toString());
        out.writeBytes(signature);
    }

    @Override
    public void read(ByteBufWrapper in) {

    }

    @Override
    public void handle(AssetsWebSocket socket) {
    }
}
