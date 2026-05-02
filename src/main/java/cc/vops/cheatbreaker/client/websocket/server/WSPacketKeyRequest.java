package cc.vops.cheatbreaker.client.websocket.server;

import cc.vops.cheatbreaker.client.util.ByteBufWrapper;
import cc.vops.cheatbreaker.client.websocket.AssetsWebSocket;
import cc.vops.cheatbreaker.client.websocket.WSPacket;

import java.beans.ConstructorProperties;

public class WSPacketKeyRequest extends WSPacket {
    private byte[] publicKey;

    @Override
    public void write(ByteBufWrapper buf) {
        this.writeKey(buf.buf(), this.publicKey);
    }

    @Override
    public void read(ByteBufWrapper buf) {
        this.publicKey = this.readKey(buf.buf());
    }

    @Override
    public void handle(AssetsWebSocket lIIlllIIlllIlIllIIlIIIIll2) {
        lIIlllIIlllIlIllIIlIIIIll2.handleKeyRequest(this);
    }

    public WSPacketKeyRequest() {
    }

    @ConstructorProperties(value={"publicKey"})
    public WSPacketKeyRequest(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public byte[] getPublicKey() {
        return this.publicKey;
    }
}