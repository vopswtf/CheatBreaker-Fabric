package cc.vops.cheatbreaker.client.util.cosmetic;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.event.type.GameTickEvent;
import cc.vops.cheatbreaker.client.util.cosmetic.emote.*;
import com.google.common.collect.ImmutableBiMap;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class EmoteManager {
    private boolean doingEmote;
    private boolean sendingEmote;

    private final List<Integer> emotes = new ArrayList<>();
    private final Map<UUID, Emote> activeEmotes = new ConcurrentHashMap<>();


    public static final ImmutableBiMap<Integer, Class<? extends Emote>> CLIENT_EMOTES = new ImmutableBiMap.Builder<Integer, Class<? extends Emote>>()
            .put(0, WaveEmote.class)
            .put(1, HandsUpEmote.class)
            .put(2, FlossEmote.class)
            .put(3, DabEmote.class)
            .put(4, TPoseEmote.class)
            .put(5, ShrugEmote.class)
            .put(6, FacepalmEmote.class)
            .put(7, NarutoRunEmote.class)
            .build();

    public EmoteManager() {
        emotes.addAll(CLIENT_EMOTES.keySet());
        CheatBreaker.getInstance().getEventBus().addEvent(GameTickEvent.class, this::onTick);
    }

    public Emote getEmoteById(int var1) {
        if (!CLIENT_EMOTES.containsKey(var1)) {
            return null;
        } else {
            try {
                return (Emote) ((Class) CLIENT_EMOTES.get(var1)).newInstance();
            } catch (Exception var3) {
                var3.printStackTrace();
                return null;
            }
        }
    }

    public int getEmoteId(Emote var1) {
        if (!CLIENT_EMOTES.containsValue(var1.getClass())) {
            return -1;
        } else {
            return CLIENT_EMOTES.inverse().get(var1.getClass());
        }
    }

    public Emote getEmote(Class<? extends Emote> clazz) {
        if (!CLIENT_EMOTES.containsValue(clazz)) {
            return null;
        } else {
            try {
                return (Emote) clazz.newInstance();
            } catch (Exception var3) {
                var3.printStackTrace();
                return null;
            }
        }
    }


    public void playEmote(UUID player, Emote var2) {
        if (Minecraft.getInstance().player != null && player.equals(Minecraft.getInstance().player.getUUID())) {
            if (Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON || this.sendingEmote) {
                Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
                this.doingEmote = true;
            }

            this.sendingEmote = false;
        }

        this.activeEmotes.putIfAbsent(player, var2);
    }


    private void onTick(GameTickEvent event) {
        if (!this.activeEmotes.isEmpty()) {
            ArrayList<UUID> list = new ArrayList<>();
            this.activeEmotes.forEach((uuid, emote) -> {
                if (Minecraft.getInstance().level != null) {
                    Player player = (Player) Minecraft.getInstance().level.getEntity(uuid);
                    if (player != null && emote.isEmoteOver()) {
                        activeEmotes.remove(player.getUUID());
                        emote.endEmote((AbstractClientPlayer) player);
                        list.add(uuid);
                    }
                }
            });
            list.forEach(this.activeEmotes::remove);
        }
    }

    public void stopEmote(AbstractClientPlayer player) {
        if (this.activeEmotes.containsKey(player.getUUID())) {
            Emote var2 = this.activeEmotes.get(player.getUUID());
            var2.endEmote(player);
            this.activeEmotes.remove(player.getUUID());
        }
    }
}
