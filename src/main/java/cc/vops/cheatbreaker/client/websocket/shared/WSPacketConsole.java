package cc.vops.cheatbreaker.client.websocket.shared;

import cc.vops.cheatbreaker.client.util.ByteBufWrapper;
import cc.vops.cheatbreaker.client.websocket.AssetsWebSocket;
import cc.vops.cheatbreaker.client.websocket.WSPacket;

import java.io.IOException;

public class WSPacketConsole extends WSPacket {
    private String output;

    public WSPacketConsole() {
    }

    public WSPacketConsole(String string) {
        this.output = string;
    }

    @Override
    public void write(ByteBufWrapper lIlIllllllllIlIIIllIIllII2) {
        lIlIllllllllIlIIIllIIllII2.writeString(this.output);
    }

    @Override
    public void read(ByteBufWrapper lIlIllllllllIlIIIllIIllII2) throws IOException {
        this.output = lIlIllllllllIlIIIllIIllII2.readStringFromBuffer(32767);
    }

    @Override
    public void handle(AssetsWebSocket lIIlllIIlllIlIllIIlIIIIll2) {
        lIIlllIIlllIlIllIIlIIIIll2.handleConsoleOutput(this);
    }

    public String getOutput() {
        return this.output;
    }
}
