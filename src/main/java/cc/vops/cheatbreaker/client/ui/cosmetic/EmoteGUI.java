package cc.vops.cheatbreaker.client.ui.cosmetic;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.util.cosmetic.Emote;
import cc.vops.cheatbreaker.client.websocket.shared.WSPacketEmote;
import net.minecraft.client.Minecraft;

import java.util.Objects;
import java.util.stream.Collectors;

public class EmoteGUI extends WheelGUI {
    public static EmoteGUI INSTANCE;

    public EmoteGUI(int n) {
        super(n, CheatBreaker.getInstance().getEmoteManager().getEmotes().stream()
                .map(CheatBreaker.getInstance().getEmoteManager()::getEmoteById).filter(Objects::nonNull).limit(8L)
                .map(emote -> new IconButton(emote.getClass(), emote.getName(), emote.getResourceLocation())).collect(Collectors.toList()));

        this.consumer = (iconButton -> {
            if (Minecraft.getInstance().player == null) return;
            Emote emote = CheatBreaker.getInstance().getEmoteManager().getEmote((Class<? extends Emote>) iconButton.getObject());
            CheatBreaker.getInstance().getEmoteManager().playEmote(Minecraft.getInstance().player.getUUID(), emote);
            CheatBreaker.getInstance().getAssetsWebSocket().send(new WSPacketEmote(Minecraft.getInstance().player.getUUID(), CheatBreaker.getInstance().getEmoteManager().getEmoteId(emote)));
        });

        INSTANCE = this;
    }

}
