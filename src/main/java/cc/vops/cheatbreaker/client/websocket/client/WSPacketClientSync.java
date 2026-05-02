package cc.vops.cheatbreaker.client.websocket.client;

import cc.vops.cheatbreaker.client.util.ByteBufWrapper;
import cc.vops.cheatbreaker.client.websocket.AssetsWebSocket;
import cc.vops.cheatbreaker.client.websocket.WSPacket;

import java.beans.ConstructorProperties;

public class WSPacketClientSync extends WSPacket {
    private int id;
    private double value;

    @Override
    public void write(ByteBufWrapper buf) {
        buf.buf().writeInt(this.id);
        buf.buf().writeDouble(this.value);
    }

    @Override
    public void read(ByteBufWrapper buf) {
        this.id = buf.buf().readInt();
        this.value = buf.buf().readDouble();
    }

    @Override
    public void handle(AssetsWebSocket lIIlllIIlllIlIllIIlIIIIll2) {
    }

    public WSPacketClientSync() {
    }

    @ConstructorProperties(value={"id", "value"})
    public WSPacketClientSync(int n, double d) {
        this.id = n;
        this.value = d;
    }
}
