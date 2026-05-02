package cc.vops.cheatbreaker.client.util.cosmetic.emote;

import cc.vops.cheatbreaker.client.ui.fading.CosineFade;
import cc.vops.cheatbreaker.client.util.cosmetic.Emote;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

public class ShrugEmote extends Emote {
    public ShrugEmote() {
        super("Shrug", new CosineFade(500L));
    }

    @Override
    public void tickEmote(Player player, PlayerModel model, AvatarRenderState state, float partialTicks) {
        model.rightArm.offsetPos(new Vector3f(0, -2F * this.duration.getCurrentValue(), 0));
        model.leftArm.offsetPos(new Vector3f(0, -2F * this.duration.getCurrentValue(), 0));
        model.head.offsetPos(new Vector3f(0, 0.5F * this.duration.getCurrentValue(), 0));
    }
}
