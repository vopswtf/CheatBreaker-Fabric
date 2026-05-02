package cc.vops.cheatbreaker.client.websocket.client;

import cc.vops.cheatbreaker.client.util.ByteBufWrapper;
import cc.vops.cheatbreaker.client.websocket.AssetsWebSocket;
import cc.vops.cheatbreaker.client.websocket.WSPacket;

import java.io.IOException;

public class WSPacketClientPlayerJoin
        extends WSPacket {
    private String lIIIIlIIllIIlIIlIIIlIIllI;

    public WSPacketClientPlayerJoin() {
    }

    public WSPacketClientPlayerJoin(String string) {
        this.lIIIIlIIllIIlIIlIIIlIIllI = string;
    }

    @Override
    public void write(ByteBufWrapper lIlIllllllllIlIIIllIIllII2) {
        lIlIllllllllIlIIIllIIllII2.writeString(this.lIIIIlIIllIIlIIlIIIlIIllI);
    }

    @Override
    public void read(ByteBufWrapper lIlIllllllllIlIIIllIIllII2) throws IOException {
        this.lIIIIlIIllIIlIIlIIIlIIllI = lIlIllllllllIlIIIllIIllII2.readStringFromBuffer(52);
    }

    @Override
    public void handle(AssetsWebSocket lIIlllIIlllIlIllIIlIIIIll2) {
    }

    public String lIIIIlIIllIIlIIlIIIlIIllI() {
        return this.lIIIIlIIllIIlIIlIIIlIIllI;
    }
}
