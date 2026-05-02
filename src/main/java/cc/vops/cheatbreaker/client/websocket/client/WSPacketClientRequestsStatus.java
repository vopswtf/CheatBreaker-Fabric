package cc.vops.cheatbreaker.client.websocket.client;

import cc.vops.cheatbreaker.client.util.ByteBufWrapper;
import cc.vops.cheatbreaker.client.websocket.AssetsWebSocket;
import cc.vops.cheatbreaker.client.websocket.WSPacket;

public class WSPacketClientRequestsStatus
        extends WSPacket {
    private boolean accepting;

    public WSPacketClientRequestsStatus(boolean bl) {
        this.accepting = bl;
    }

    public WSPacketClientRequestsStatus() {
    }

    @Override
    public void write(ByteBufWrapper lIlIllllllllIlIIIllIIllII2) {
        lIlIllllllllIlIIIllIIllII2.buf().writeBoolean(this.accepting);
    }

    @Override
    public void read(ByteBufWrapper lIlIllllllllIlIIIllIIllII2) {
        this.accepting = lIlIllllllllIlIIIllIIllII2.buf().readBoolean();
    }

    @Override
    public void handle(AssetsWebSocket lIIlllIIlllIlIllIIlIIIIll2) {
    }

    public boolean isAccepting() {
        return this.accepting;
    }
}

