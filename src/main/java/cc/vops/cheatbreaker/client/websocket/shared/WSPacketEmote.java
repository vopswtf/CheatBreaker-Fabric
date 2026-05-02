package cc.vops.cheatbreaker.client.websocket.shared;

import cc.vops.cheatbreaker.client.util.ByteBufWrapper;
import cc.vops.cheatbreaker.client.websocket.AssetsWebSocket;
import cc.vops.cheatbreaker.client.websocket.WSPacket;
import cc.vops.cheatbreaker.client.websocket.client.WSPacketHello;
import lombok.Data;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.util.UUID;

public class WSPacketEmote extends WSPacket {
    public UUID playerId;
    public int emoteId;

    public WSPacketEmote() {
    }

    public WSPacketEmote(UUID playerId, int emoteId) {
        this.playerId = playerId;
        this.emoteId = emoteId;
    }

    @Override
    public void write(ByteBufWrapper var1) {
        var1.writeString(this.playerId.toString());
        var1.buf().writeInt(this.emoteId);
    }

    @Override
    public void read(ByteBufWrapper var1) throws IOException {
        this.playerId = UUID.fromString(var1.readStringFromBuffer(36));
        this.emoteId = var1.buf().readInt();
    }

    @Override
    public void handle(AssetsWebSocket socket) {
        socket.handleEmote(this);
    }
}
