package cc.vops.cheatbreaker.client.websocket.server;

import cc.vops.cheatbreaker.client.util.ByteBufWrapper;
import cc.vops.cheatbreaker.client.util.cosmetic.Cosmetic;
import cc.vops.cheatbreaker.client.websocket.AssetsWebSocket;
import cc.vops.cheatbreaker.client.websocket.WSPacket;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class WSPacketCosmetics extends WSPacket {
    private List<Cosmetic> cosmetics;
    private String playerId;

    public WSPacketCosmetics() {
    }

    public WSPacketCosmetics(String string, List<Cosmetic> list) {
        this.playerId = string;
        this.cosmetics = list;
    }

    @Override
    public void write(ByteBufWrapper buf) {
        buf.buf().writeInt(this.cosmetics.size());
        for (Cosmetic cosmetic : this.cosmetics) {
            buf.writeString(cosmetic.getName());
            buf.buf().writeFloat(cosmetic.getScale());
            buf.writeString(cosmetic.getLocation().toString());
            buf.buf().writeBoolean(cosmetic.isEquipped());
        }
    }

    @Override
    public void read(ByteBufWrapper buf) throws IOException {
        this.playerId = buf.readStringFromBuffer(52);

        int n = buf.buf().readInt();
        this.cosmetics = new ArrayList<>();
        for (int i = 0; i < n; ++i) {
            long time = buf.buf().readLong();
            float scale = buf.buf().readFloat();
            boolean active = buf.buf().readBoolean();
            String identifier = buf.readStringFromBuffer(512);
            String name = buf.readStringFromBuffer(128);
            Cosmetic.CosmeticType type = Cosmetic.CosmeticType.get(buf.readStringFromBuffer(128));

//            System.out.println("Cosmetic Read: " + name + ", Type: " + type + ", Time: " + time + ", Scale: " + scale + ", Active: " + active + ", identifier: " + identifier);

            assert type != null;
            if (type.getTypeName().equals("emote")) {
                this.cosmetics.add(new Cosmetic(this.playerId, Integer.parseInt(name), type));
            } else {
                this.cosmetics.add(new Cosmetic(time, this.playerId, name, type, scale, active, identifier));
            }
        }
    }

    @Override
    public void handle(AssetsWebSocket lIIlllIIlllIlIllIIlIIIIll2) {
        lIIlllIIlllIlIllIIlIIIIll2.handleCosmetics(this);
    }

}