package cc.vops.cheatbreaker.client.websocket.client;

import cc.vops.cheatbreaker.client.util.ByteBufWrapper;
import cc.vops.cheatbreaker.client.websocket.AssetsWebSocket;
import cc.vops.cheatbreaker.client.websocket.WSPacket;

import javax.crypto.SecretKey;
import java.security.PublicKey;

public class WSPacketClientJoinServerResponse extends WSPacket {
    private byte[] secretKey = new byte[0];
    private byte[] publicKey = new byte[0];

    public WSPacketClientJoinServerResponse(SecretKey secretKey, PublicKey publicKey, byte[] arrby) {
//        this.secretKey = CryptManager.encryptData(publicKey, secretKey.getEncoded());
//        this.publicKey = CryptManager.decryptData(publicKey, arrby);
    }

    @Override
    public void write(ByteBufWrapper bufWrapper) {
        this.writeKey(bufWrapper.buf(), this.secretKey);
        this.writeKey(bufWrapper.buf(), this.publicKey);
    }

    @Override
    public void read(ByteBufWrapper bufWrapper) {
    }

    @Override
    public void handle(AssetsWebSocket socket) {
    }
}

