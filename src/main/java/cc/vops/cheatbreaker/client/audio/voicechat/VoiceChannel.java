package cc.vops.cheatbreaker.client.audio.voicechat;

import cc.vops.cheatbreaker.CheatBreaker;
import lombok.Getter;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class VoiceChannel {
    @Getter
    private final UUID uuid;
    @Getter
    private final String channelName;
    @Getter
    private final List<VoiceUser> voiceUsers = new ArrayList<>();
    @Getter
    private final List<UUID> listeningList = new ArrayList<>();

    public VoiceChannel(UUID uuid, String channelName) {
        this.uuid = uuid;
        this.channelName = channelName;
        addPlaceholders();
    }

    public void addPlaceholders() {
        for (int i = 0; i < new Random().nextInt(4, 12); i++) {
            UUID placeholderUUID = UUID.randomUUID();
            this.voiceUsers.add(new VoiceUser(placeholderUUID, "User" + new Random().nextInt(1000)));
            if (new Random().nextBoolean()) {
                this.listeningList.add(placeholderUUID);
            }
        }
    }

    public VoiceUser getOrCreateVoiceUser(UUID uuid, String string) {
        VoiceUser voiceUser = null;
        if (!this.isInChannel(uuid)) {
            CheatBreaker.LOGGER.info("[CB Voice] Created the user client side (" + uuid.toString() + ", " + string + ").");
            voiceUser = new VoiceUser(uuid, ChatFormatting.stripFormatting(string));
            this.listeningList.add(voiceUser.getUUID());
            this.voiceUsers.add(voiceUser);
        }
        return voiceUser;
    }

    public void removeUser(UUID uuid) {
        this.voiceUsers.removeIf(voiceUser -> voiceUser.getUUID().equals(uuid));
    }

    public void addToListening(UUID uuid) {
        if (this.isInChannel(uuid)) {
            this.listeningList.add(uuid);
        }
    }

    public void removeListener(UUID uuid) {
        this.listeningList.removeIf(voiceUser -> voiceUser.equals(uuid));
    }

    public boolean isListening(UUID uuid) {
        return this.listeningList.stream().anyMatch(voiceUser -> voiceUser.equals(uuid));
    }

    public boolean isInChannel(UUID uuid) {
        return this.voiceUsers.stream().anyMatch(voiceUser -> voiceUser.getUUID().equals(uuid));
    }

    public List<VoiceUser> getUsers() {
        return this.voiceUsers;
    }
}