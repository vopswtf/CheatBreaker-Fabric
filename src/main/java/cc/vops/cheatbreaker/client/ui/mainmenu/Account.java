package cc.vops.cheatbreaker.client.ui.mainmenu;

import cc.vops.cheatbreaker.client.util.PlayerHeads;
import lombok.Getter;
import net.minecraft.resources.Identifier;

import java.util.UUID;

@Getter
public class Account {
    private final String username;
    private final UUID uuid;
    private final Identifier headLocation;

    public Account(String username, UUID uuid) {
        this.username = username;
        this.uuid = uuid;
        this.headLocation = PlayerHeads.getHeadLocation(username, uuid);
    }
}
