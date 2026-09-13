package cc.vops.cheatbreaker.client.websocket.server;

import cc.vops.cheatbreaker.client.util.ByteBufWrapper;
import cc.vops.cheatbreaker.client.websocket.AssetsWebSocket;
import cc.vops.cheatbreaker.client.websocket.WSPacket;
import cc.vops.cheatbreaker.client.websocket.client.WSPacketHello;
import cc.vops.cheatbreaker.mixin.MinecraftAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.AccountProfileKeyPairManager;
import net.minecraft.client.multiplayer.ProfileKeyPairManager;

import java.io.IOException;

public class WSPacketWelcome extends WSPacket {
    @Override
    public void write(ByteBufWrapper var1) {

    }

    @Override
    public void read(ByteBufWrapper var1) throws IOException {
    }

    @Override
    public void handle(AssetsWebSocket socket) {
        ProfileKeyPairManager manager = ((MinecraftAccessor) Minecraft.getInstance()).getRawKPManager();
        manager.prepareKeyPair().whenComplete((keyPair, throwable) -> {
            keyPair.ifPresent(profileKeyPair -> socket.send(new WSPacketHello(profileKeyPair)));
        });
    }
}
