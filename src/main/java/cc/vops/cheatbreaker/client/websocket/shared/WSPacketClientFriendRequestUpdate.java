package cc.vops.cheatbreaker.client.websocket.shared;

import cc.vops.cheatbreaker.client.util.ByteBufWrapper;
import cc.vops.cheatbreaker.client.websocket.AssetsWebSocket;
import cc.vops.cheatbreaker.client.websocket.WSPacket;

import java.io.IOException;

public class WSPacketClientFriendRequestUpdate extends WSPacket {
    private boolean add;
    private String playerId;

    public WSPacketClientFriendRequestUpdate() {
    }

    public WSPacketClientFriendRequestUpdate(boolean bl, String string) {
        this.add = bl;
        this.playerId = string;
    }

    @Override
    public void write(ByteBufWrapper buf) {
        buf.buf().writeBoolean(this.add);
        buf.writeString(this.playerId);
    }

    @Override
    public void read(ByteBufWrapper buf) throws IOException {
        this.add = buf.buf().readBoolean();
        this.playerId = buf.readStringFromBuffer(52);
    }

    @Override
    public void handle(AssetsWebSocket socket) {
        socket.handleFriendRequestUpdate(this);
    }

    public boolean isAdd() {
        return this.add;
    }

    public String getPlayerId() {
        return this.playerId;
    }
}