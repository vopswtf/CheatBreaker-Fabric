package cc.vops.cheatbreaker.client.websocket.client;

import cc.vops.cheatbreaker.client.util.ByteBufWrapper;
import cc.vops.cheatbreaker.client.websocket.AssetsWebSocket;
import cc.vops.cheatbreaker.client.websocket.WSPacket;

import java.beans.ConstructorProperties;

public class WSPacketClientKeyResponse
        extends WSPacket {
    private byte[] data;

    @Override
    public void write(ByteBufWrapper buf) {
        this.writeKey(buf.buf(), this.data);
    }

    @Override
    public void read(ByteBufWrapper buf) {
        this.data = this.readKey(buf.buf());
    }

    @Override
    public void handle(AssetsWebSocket lIIlllIIlllIlIllIIlIIIIll2) {
    }

    public byte[] getData() {
        return this.data;
    }

    @ConstructorProperties(value={"data"})
    public WSPacketClientKeyResponse(byte[] data) {
        this.data = data;
    }

    public WSPacketClientKeyResponse() {
    }
}

