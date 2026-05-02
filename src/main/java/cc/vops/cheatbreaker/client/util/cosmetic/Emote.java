package cc.vops.cheatbreaker.client.util.cosmetic;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.fading.AbstractFade;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

@Getter
public abstract class Emote {
    private final String name;
    protected final AbstractFade duration;
    private final Identifier resourceLocation;

    public Emote(String name, AbstractFade duration) {
        this.duration = duration;
        this.name = name;
        this.resourceLocation = CheatBreaker.asset("emote/" + name.toLowerCase().replace("-", "").replace(" ", "") + ".png");
    }

    public abstract void tickEmote(Player player, PlayerModel model, AvatarRenderState state, float partialTicks);

    protected void endEmote(AbstractClientPlayer player) {
        if (player == null) return;
        if (Minecraft.getInstance().player != null && player.getUUID().equals(Minecraft.getInstance().player.getUUID())) {
            if (CheatBreaker.getInstance().getEmoteManager().isDoingEmote()) {
                Minecraft.getInstance().options.setCameraType(CameraType.FIRST_PERSON);
                CheatBreaker.getInstance().getEmoteManager().setDoingEmote(false);
                CheatBreaker.getInstance().getEmoteManager().setSendingEmote(false);
            }
        }
    }

    public boolean isEmoteOver() {
        return this.duration.hasStartTime() && this.duration.isExpired();
    }
}
