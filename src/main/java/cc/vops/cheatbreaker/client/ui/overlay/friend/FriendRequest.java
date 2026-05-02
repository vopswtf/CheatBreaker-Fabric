package cc.vops.cheatbreaker.client.ui.overlay.friend;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class FriendRequest {
    private final String username;
    private final String playerId;
    @Setter
    private boolean friend;

    public FriendRequest(String username, String playerId) {
        this.username = username;
        this.playerId = playerId;
    }

    public UUID getPlayerUUID() {
        if (this.playerId == null || this.playerId.isEmpty()) return null;
        return UUID.fromString(this.playerId);
    }
}