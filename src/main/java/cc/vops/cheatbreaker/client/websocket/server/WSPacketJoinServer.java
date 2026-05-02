package cc.vops.cheatbreaker.client.websocket.server;

import cc.vops.cheatbreaker.client.util.ByteBufWrapper;
import cc.vops.cheatbreaker.client.websocket.AssetsWebSocket;
import cc.vops.cheatbreaker.client.websocket.WSPacket;
//import net.minecraft.util.CryptManager;

import java.security.PublicKey;

public class WSPacketJoinServer extends WSPacket {
    private PublicKey publicKey;
    private byte[] bytes;

    @Override
    public void write(ByteBufWrapper lIlIllllllllIlIIIllIIllII2) {
    }

    @Override
    public void read(ByteBufWrapper lIlIllllllllIlIIIllIIllII2) {
//        this.publicKey = CryptManager.decodePublicKey(this.readKey(lIlIllllllllIlIIIllIIllII2.buf()));
        this.bytes = this.readKey(lIlIllllllllIlIIIllIIllII2.buf());
    }

    @Override
    public void handle(AssetsWebSocket socket) {
        socket.handleJoinServer(this);
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public byte[] lIIIIIIIIIlIllIIllIlIIlIl() {
        return this.bytes;
    }
}

