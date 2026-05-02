package cc.vops.cheatbreaker.client.websocket.client;

import cc.vops.cheatbreaker.client.util.ByteBufWrapper;
import cc.vops.cheatbreaker.client.util.cosmetic.Cosmetic;
import cc.vops.cheatbreaker.client.websocket.AssetsWebSocket;
import cc.vops.cheatbreaker.client.websocket.WSPacket;
import lombok.Getter;

import java.io.IOException;
import java.util.List;

@Getter
public class WSPacketClientCosmetics extends WSPacket {
    private List<Cosmetic> cosmetics;

    public WSPacketClientCosmetics() {
    }

    public WSPacketClientCosmetics(List<Cosmetic> list) {
        this.cosmetics = list;
    }

    @Override
    public void write(ByteBufWrapper buf) {
        buf.buf().writeInt(this.cosmetics.size());
        for (Cosmetic cosmetic : this.cosmetics) {
            buf.buf().writeLong(cosmetic.getLastUpdate());
            buf.buf().writeBoolean(cosmetic.isEquipped());
            buf.writeString(cosmetic.getName());
            buf.writeString(cosmetic.getType().getTypeName());
            buf.buf().writeFloat(cosmetic.getScale());
            buf.writeString(cosmetic.getLocation().toString().replaceFirst("minecraft:", ""));
        }
    }

    @Override
    public void read(ByteBufWrapper buf) throws IOException {
    }

    @Override
    public void handle(AssetsWebSocket handler) {
    }

}
