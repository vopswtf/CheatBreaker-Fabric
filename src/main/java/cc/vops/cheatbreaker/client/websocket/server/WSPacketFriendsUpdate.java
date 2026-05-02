package cc.vops.cheatbreaker.client.websocket.server;

import cc.vops.cheatbreaker.client.util.ByteBufWrapper;
import cc.vops.cheatbreaker.client.websocket.AssetsWebSocket;
import cc.vops.cheatbreaker.client.websocket.WSPacket;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WSPacketFriendsUpdate extends WSPacket {
    private boolean consoleAllowed;
    private boolean isAcceptingFriendRequests;
    private Map<String, List<String>> onlineMap;
    private Map<String, List<String>> offlineMap;

    @Override
    public void write(ByteBufWrapper lIlIllllllllIlIIIllIIllII2) {
    }

    @Override
    public void read(ByteBufWrapper buf) throws IOException {
        int n;
        this.consoleAllowed = buf.buf().readBoolean();
        this.isAcceptingFriendRequests = buf.buf().readBoolean();
        int n2 = buf.buf().readInt();
        int n3 = buf.buf().readInt();
        this.onlineMap = new HashMap<>();
        for (n = 0; n < n2; ++n) {
            this.onlineMap.put(
                    buf.readStringFromBuffer(52),
                    ImmutableList.of(
                            buf.readStringFromBuffer(32),
                            String.valueOf(buf.buf().readInt()),
                            buf.readStringFromBuffer(256)
                    )
            );
        }
        this.offlineMap = new HashMap<>();
        for (n = 0; n < n3; ++n) {
            this.offlineMap.put(
                    buf.readStringFromBuffer(52),
                    ImmutableList.of(
                            buf.readStringFromBuffer(32),
                            String.valueOf(buf.buf().readLong())
                    )
            );
        }
    }

    @Override
    public void handle(AssetsWebSocket lIIlllIIlllIlIllIIlIIIIll2) {
        lIIlllIIlllIlIllIIlIIIIll2.handleFriendsUpdate(this);
    }

    public boolean isConsoleAllowed() {
        return this.consoleAllowed;
    }

    public boolean isAcceptingFriendRequests() {
        return this.isAcceptingFriendRequests;
    }

    public Map<String, List<String>> getOnlineMap() {
        return this.onlineMap;
    }

    public Map<String, List<String>> getOfflineMap() {
        return this.offlineMap;
    }
}
