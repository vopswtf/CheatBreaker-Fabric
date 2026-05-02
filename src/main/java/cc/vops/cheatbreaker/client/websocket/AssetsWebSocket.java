package cc.vops.cheatbreaker.client.websocket;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.Profile;
import cc.vops.cheatbreaker.client.util.ByteBufWrapper;
import cc.vops.cheatbreaker.client.ui.mainmenu.AbstractElement;
import cc.vops.cheatbreaker.client.ui.overlay.Alert;
import cc.vops.cheatbreaker.client.ui.overlay.SocialOverlayScreen;
import cc.vops.cheatbreaker.client.ui.overlay.element.MessagesElement;
import cc.vops.cheatbreaker.client.ui.overlay.friend.FriendRequest;
import cc.vops.cheatbreaker.client.ui.overlay.friend.FriendRequestElement;
import cc.vops.cheatbreaker.client.util.ChatColor;
import cc.vops.cheatbreaker.client.util.cosmetic.Cosmetic;
import cc.vops.cheatbreaker.client.util.Sounds;
import cc.vops.cheatbreaker.client.util.cosmetic.Emote;
import cc.vops.cheatbreaker.client.util.friend.Friend;
import cc.vops.cheatbreaker.client.util.friend.Status;
import cc.vops.cheatbreaker.client.util.thread.WSReconnectThread;
import cc.vops.cheatbreaker.client.websocket.shared.*;
import cc.vops.cheatbreaker.client.websocket.server.*;
import cc.vops.cheatbreaker.client.websocket.client.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import javax.crypto.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AssetsWebSocket extends WebSocketClient {
    private final Minecraft minecraft = Minecraft.getInstance();
    private final List<String> playersCache = new ArrayList<>();

    public AssetsWebSocket(URI uRI, Map<String,String> map) {
        super(uRI, new Draft_6455(), map, 0);

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (minecraft.getCurrentServer() != null) {
                this.sendUpdateServer(minecraft.getCurrentServer().ip);
            } else if (minecraft.getSingleplayerServer() != null) {
                this.sendUpdateServer("local:singleplayer");
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            this.playersCache.clear();
            this.sendUpdateServer("");
        });
    }

    public void send(WSPacket packet) {
        if (!this.isOpen()) {
            return;
        }
        ByteBufWrapper buf = new ByteBufWrapper(Unpooled.buffer());
        buf.writeVarInt(WSPacket.REGISTRY.get(packet.getClass()));
        packet.write(buf);
        this.send(buf.buf().array());
    }

    public void handleIncoming(ByteBufWrapper buf) {
        int n = buf.readVarInt();
        Class<? extends WSPacket> packetClass = WSPacket.REGISTRY.inverse().get(n);
        try {
            WSPacket packet = packetClass == null ? null : packetClass.newInstance();
            if (packet == null) {
                return;
            }
            packet.read(buf);
            packet.handle(this);
        }
        catch (Exception exception) {
            CheatBreaker.LOGGER.info("Error from: " + packetClass);
            exception.printStackTrace();
        }
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        CheatBreaker.LOGGER.info("[CB] Connection established");
//        if (Objects.equals(Minecraft.getMinecraft().getSession().getUsername(), Minecraft.getMinecraft().getSession().getPlayerID())) {
//            this.close();
//        }
    }

    @Override
    public void onMessage(ByteBuffer byteBuffer) {
        this.handleIncoming(new ByteBufWrapper(Unpooled.wrappedBuffer(byteBuffer.array())));
    }

    @Override
    public void onMessage(String message) {

    }

    public void handleConsoleOutput(WSPacketConsole packetRawConsoleOutput) {
        CheatBreaker.getInstance().getConsoleLines().add(packetRawConsoleOutput.getOutput());
        CheatBreaker.LOGGER.info(packetRawConsoleOutput.getOutput());
    }

    public void handleFriendRemove(WSPacketClientFriendRemove packetFriendRemove) {
        String string = packetFriendRemove.getPlayerId();
        Friend friend = CheatBreaker.getInstance().getFriendsManager().getFriend(string);
        if (friend != null) {
            CheatBreaker.getInstance().getFriendsManager().getFriends().remove(string);
            SocialOverlayScreen.getInstance().handleFriend(friend, false);
        }
    }

    public void handleMessage(WSPacketMessage packetMessage) {
        String playerId = packetMessage.getPlayerId();
        String message = packetMessage.getMessage();
        Friend friend = CheatBreaker.getInstance().getFriendsManager().getFriends().get(playerId);
        if (friend != null) {
            CheatBreaker.getInstance().getFriendsManager().addUnreadMessage(friend.getPlayerId(), message);
            if (CheatBreaker.getInstance().getStatus() != Status.BUSY) {
                Sounds.playSound("message");
                Alert.displayMessage(ChatColor.GREEN + friend.getName() + ChatColor.RESET + " says:", message);
            }
            for (AbstractElement element : SocialOverlayScreen.getInstance().getElements()) {
                if (!(element instanceof MessagesElement) || ((MessagesElement)element).getFriend() != friend) continue;
                CheatBreaker.getInstance().getFriendsManager().readMessages(friend.getPlayerId());
            }
        }
    }

    public void sendUpdateServer(String string) {
        this.send(new WSPacketServerUpdate("", string));
    }

    public void handleServerUpdate(WSPacketServerUpdate packetServerUpdate) {
        String playerId = packetServerUpdate.getPlayerId();
        String server = packetServerUpdate.getServer();
        Friend friend = CheatBreaker.getInstance().getFriendsManager().getFriends().get(playerId);
        if (friend != null) {
            friend.setServer(server);
        }
    }

    public void handleBulkFriends(WSPacketBulkFriends packetBulkFriends) {
        CheatBreaker.getInstance().getFriendsManager().getFriendRequests().clear();
        JsonArray bulkArray = packetBulkFriends.getBulkArray();
        for (JsonElement jsonElement : bulkArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String playerId = jsonObject.get("uuid").getAsString();
            String playerName = jsonObject.get("name").getAsString();
            FriendRequest friendRequest = new FriendRequest(playerName, playerId);
            CheatBreaker.getInstance().getFriendsManager().getFriendRequests().put(playerId, friendRequest);
            SocialOverlayScreen.getInstance().handleFriendRequest(friendRequest, true);
        }

        if (this.minecraft.player != null && this.minecraft.getCurrentServer() != null) {
            this.sendUpdateServer(this.minecraft.getCurrentServer().ip);
        }
    }

    public void handleFriendRequest(WSPacket packet, boolean outgoing) {
        if (outgoing) {
            WSPacketFriendStatusUpdate friendStatus = (WSPacketFriendStatusUpdate)packet;
            FriendRequest friendRequest = new FriendRequest(friendStatus.getName(), friendStatus.getPlayerId());
            CheatBreaker.getInstance().getFriendsManager().getFriendRequests().put(friendStatus.getPlayerId(), friendRequest);
            SocialOverlayScreen.getInstance().handleFriendRequest(friendRequest, true);
            friendRequest.setFriend(friendStatus.isFriend());
//            Alert.displayMessage("Friend Request", "Request has been sent.");
        } else {
            WSPacketFriendRequest friendRequestIncoming = (WSPacketFriendRequest) packet;
            String string = friendRequestIncoming.getPlayerId();
            String string2 = friendRequestIncoming.getName();
            FriendRequest friendRequest = new FriendRequest(string2, string);
            CheatBreaker.getInstance().getFriendsManager().getFriendRequests().put(string, friendRequest);
            SocialOverlayScreen.getInstance().handleFriendRequest(friendRequest, true);
            if (CheatBreaker.getInstance().getStatus() != Status.BUSY) {
                Sounds.playSound("message");
                Alert.displayMessage("Friend Request", friendRequest.getUsername() + " wants to be your friend.");
            }
        }
    }

    public void handleFriendUpdate(WSPacketFriendUpdate packetFriendUpdate) {
        String playerId = packetFriendUpdate.getPlayerId();
        String name = packetFriendUpdate.getName();
        boolean online = packetFriendUpdate.isOnline();
        Friend friend = CheatBreaker.getInstance().getFriendsManager().getFriends().get(playerId);
        if (friend == null) {
            friend = Friend.builder()
                    .online(online)
                    .name(name)
                    .playerId(playerId)
                    .online(online)
                    .onlineStatus(Status.ONLINE).build();
            CheatBreaker.getInstance().getFriendsManager().getFriends().put(playerId, friend);
            SocialOverlayScreen.getInstance().handleFriend(friend, true);
        }
        if (packetFriendUpdate.getOfflineSince() < 10L) {
            int n = (int)packetFriendUpdate.getOfflineSince();
            Status cBStatusEnum = Status.ONLINE;
            for (Status cBStatusEnum2 : Status.values()) {
                if (cBStatusEnum2.ordinal() != n) continue;
                cBStatusEnum = cBStatusEnum2;
            }
            friend.setOnlineStatus(cBStatusEnum);
        }
        friend.setOnline(online);
        friend.setName(name);
        SocialOverlayScreen.getInstance().getFriendsListElement().updateSize();
        if (!online) {
            friend.setOfflineSince(packetFriendUpdate.getOfflineSince());
        }
    }

    public void handleFriendsUpdate(WSPacketFriendsUpdate packetFriendsUpdate) {
        String name;
        String playerId;
        CheatBreaker.getInstance().getFriendsManager().getFriends().clear();
        Map<String, List<String>> onlineMap = packetFriendsUpdate.getOnlineMap();
        Map<String, List<String>> offlineMap = packetFriendsUpdate.getOfflineMap();
//        CheatBreaker.getInstance().setConsoleAllowed(packetFriendsUpdate.isConsoleAllowed());
        CheatBreaker.getInstance().setAcceptingFriendRequests(packetFriendsUpdate.isAcceptingFriendRequests());
        for (Map.Entry<String, List<String>> online : onlineMap.entrySet()) {
            playerId = online.getKey();
            name = online.getValue().get(0);
            int statusOrdinal = Integer.parseInt(online.getValue().get(1));
            String server = online.getValue().get(2);
            Status cBStatusEnum = Status.ONLINE;
            for (Status cBStatusEnum2 : Status.values()) {
                if (cBStatusEnum2.ordinal() != statusOrdinal) continue;
                cBStatusEnum = cBStatusEnum2;
            }
            Friend object = Friend.builder()
                    .name(name)
                    .playerId(playerId)
                    .server(server)
                    .onlineStatus(cBStatusEnum)
                    .online(true)
                    .status("Online").build();
            CheatBreaker.getInstance().getFriendsManager().getFriends().put(playerId, object);
            SocialOverlayScreen.getInstance().handleFriend(object, true);
        }
        for (Map.Entry<String, List<String>> offline : offlineMap.entrySet()) {
            playerId = offline.getKey();
            name = offline.getValue().get(0);
            Friend friend = Friend.builder()
                    .name(name)
                    .playerId(playerId)
                    .server("")
                    .onlineStatus(Status.ONLINE)
                    .online(false)
                    .status("Online")
                    .offlineSince(Long.parseLong(offline.getValue().get(1))).build();
            CheatBreaker.getInstance().getFriendsManager().getFriends().put(playerId, friend);
            SocialOverlayScreen.getInstance().handleFriend(friend, true);
        }
    }

    public void handleCosmetics(WSPacketCosmetics packetCosmetics) {
        String string = packetCosmetics.getPlayerId();
        CheatBreaker.getInstance().getCosmetics().removeIf(c -> c.getPlayerId().equals(string));

        for (Cosmetic cosmetic : packetCosmetics.getCosmetics()) {
            try {
                CheatBreaker.getInstance().getCosmetics().add(cosmetic);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void onClose(int n, String string, boolean bl) {
        CheatBreaker.LOGGER.info("Close: " + string + " (" + n + ")");
        new WSReconnectThread().start();
        SocialOverlayScreen.getInstance().getFriendRequestsElement().getElements().clear();
        SocialOverlayScreen.getInstance().getFriendsListElement().getElements().clear();
        CheatBreaker.getInstance().getFriendsManager().getFriends().clear();
        CheatBreaker.getInstance().getFriendsManager().getFriendRequests().clear();
    }

    @Override
    public void onError(Exception exception) {
        CheatBreaker.LOGGER.info("Error: " + exception.getMessage());
        exception.printStackTrace();
    }

    public void handleFormattedConsoleOutput(WSPacketFormattedConsoleOutput packetFormattedConsoleOutput) {
        String string = packetFormattedConsoleOutput.getPrefix();
        String string2 = packetFormattedConsoleOutput.getContent();
        CheatBreaker.getInstance().getConsoleLines().add(ChatColor.DARK_GRAY + "[" + ChatColor.RESET + packetFormattedConsoleOutput.getPrefix() + ChatColor.DARK_GRAY + "] " + ChatColor.RESET + packetFormattedConsoleOutput.getContent());
        Alert.displayMessage(string, string2);
    }

    public void handleEmote(WSPacketEmote packetEmote) {
        if (Minecraft.getInstance().level == null) return;
//        System.out.println("Playing emote " + packetEmote.emoteId + " for player " + packetEmote.playerId);
        Player player = Minecraft.getInstance().level.getPlayerByUUID(packetEmote.playerId);
//        System.out.println("Player: " + player);
        if (player == null) return;
        Emote emote = CheatBreaker.getInstance().getEmoteManager().getEmoteById(packetEmote.emoteId);
//        System.out.println("Emote: " + emote);
        if (emote != null) {
            CheatBreaker.getInstance().getEmoteManager().playEmote(player.getUUID(), emote);
        } else {
            CheatBreaker.getInstance().getEmoteManager().stopEmote((AbstractClientPlayer) player);
        }

    }

    public void handleJoinServer(WSPacketJoinServer packetJoinServer) {
//        SecretKey secretKey = CryptManager.createNewSharedKey();
//        PublicKey publicKey = packetJoinServer.getPublicKey();
//        String string = new BigInteger(CryptManager.getServerIdHash("", publicKey, secretKey)).toString(16);
//        try {
//            this.createSessionService().joinServer(this.minecraft.getSession().func_148256_e(), this.minecraft.getSession().getToken(), string);
//        }
//        catch (AuthenticationUnavailableException authenticationUnavailableException) {
//            Alert.displayMessage("Authentication Unavailable", authenticationUnavailableException.getMessage());
//            return;
//        }
//        catch (InvalidCredentialsException invalidCredentialsException) {
//            Alert.displayMessage("Invalid Credentials", invalidCredentialsException.getMessage());
//            return;
//        }
//        catch (AuthenticationException authenticationException) {
//            Alert.displayMessage("Authentication Error", authenticationException.getMessage());
//            return;
//        }
//        catch (NullPointerException nullPointerException) {
//            this.close();
//        }
//        try {
//            ByteBufWrapper buf = new ByteBufWrapper(Unpooled.buffer());
//            WSPacketClientJoinServerResponse response = new WSPacketClientJoinServerResponse(secretKey, publicKey, packetJoinServer.lIIIIIIIIIlIllIIllIlIIlIl());
//            response.write(buf);
//            this.sentToServer(response);
//            File file = new File(Minecraft.getMinecraft().mcDataDir + File.separator + "config" + File.separator + "client" + File.separator + "profiles.txt");
//            if (file.exists()) {
//                this.sentToServer(new WSPacketClientProfilesExist());
//            }
//        }
//        catch (Exception exception) {
//            exception.printStackTrace();
//        }
    }

    @Override
    public void send(String string) {
        if (!this.isOpen()) return;
        super.send(string);
    }

    public void sendPlayerJoin(UUID uuid) {
        if (this.minecraft.player == null) return;
        String string = uuid.toString();
        if (!this.playersCache.contains(string) && !string.equals(this.minecraft.player.getUUID().toString())) {
            this.playersCache.add(string);
            this.send(new WSPacketClientPlayerJoin(string));
        }
    }

    public void playerLeave(UUID uuid) {
        if (uuid == this.minecraft.getGameProfile().id()) return;
        this.playersCache.remove(uuid.toString());
    }

    public void sendClientCosmetics() {
        System.out.println("Sending cosmetics (" + CheatBreaker.getInstance().getCosmetics().size() + ")");
        this.send(new WSPacketClientCosmetics(CheatBreaker.getInstance().getCosmetics()));
    }

    public void updateClientStatus() {
        this.send(new WSPacketFriendUpdate("", "", CheatBreaker.getInstance().getStatus().ordinal(), true));
    }

    public void handleFriendRequestUpdate(WSPacketClientFriendRequestUpdate packetFriendRequestUpdate) {
        if (!packetFriendRequestUpdate.isAdd()) {
            CheatBreaker.getInstance().getFriendsManager().getFriendRequests().remove(packetFriendRequestUpdate.getPlayerId());
            FriendRequestElement requestElement = null;
            for (FriendRequestElement friendRequestElement : SocialOverlayScreen.getInstance().getFriendRequestsElement().getElements()) {
                if (!friendRequestElement.getFriendRequest().getPlayerId().equals(packetFriendRequestUpdate.getPlayerId())) continue;
                requestElement = friendRequestElement;
            }
            if (requestElement != null) {
                SocialOverlayScreen.getInstance().getFriendRequestsElement().getFrientRequestElementList().add(requestElement);
                SocialOverlayScreen.getInstance().handleFriendRequest(requestElement.getFriendRequest(), false);
            }
        }
    }

    public void handleKeyRequest(WSPacketKeyRequest packetKeyRequest) {
        try {
            byte[] test = "a".getBytes(); // Message.i()
            byte[] data = AssetsWebSocket.getKeyResponse(packetKeyRequest.getPublicKey(), test);
            this.send(new WSPacketClientKeyResponse(data));
        }
        catch (Exception | UnsatisfiedLinkError throwable) {
            // empty catch block
        }
    }

    public static byte[] getKeyResponse(byte[] publicKey, byte[] privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException {
        PublicKey key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKey));
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(1, key);
        return cipher.doFinal(privateKey);
    }

    public void lIIIIlIIllIIlIIlIIIlIIllI(Profile profile) {
        try {
            File file = new File(Minecraft.getInstance().gameDirectory + File.separator + "cheatbreaker" + File.separator + "config" + File.separator + "profiles.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                bufferedWriter.write("################################");
                bufferedWriter.newLine();
                bufferedWriter.write("# MC_Client: PROFILES");
                bufferedWriter.newLine();
                bufferedWriter.write("################################");
                bufferedWriter.newLine();
                for (Profile ilIIlIIlIIlllIlIIIlIllIIl : CheatBreaker.getInstance().getProfiles()) {
                    bufferedWriter.write(ilIIlIIlIIlllIlIIIlIllIIl.getName() + ":" + ilIIlIIlIIlllIlIIIlIllIIl.getIndex());
                    bufferedWriter.newLine();
                }
                bufferedWriter.close();
            }
            catch (Exception exception) {}
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}
