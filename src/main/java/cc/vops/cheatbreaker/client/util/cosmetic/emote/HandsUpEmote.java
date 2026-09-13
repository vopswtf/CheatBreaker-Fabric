package cc.vops.cheatbreaker.client.util.cosmetic.emote;

import cc.vops.cheatbreaker.client.ui.fading.FloatFade;
import cc.vops.cheatbreaker.client.ui.fading.MinMaxFade;
import cc.vops.cheatbreaker.client.util.cosmetic.Emote;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.player.Player;

public class HandsUpEmote extends Emote {
    private static final long FADE_DURATION = 250L;

    public HandsUpEmote() {
        super("Hands Up", new FloatFade(2000L));
    }

    @Override
    public void tickEmote(Player player, PlayerModel model, AvatarRenderState state, float partialTicks) {
        long elapsed = this.duration.getTimeElapsed();
        long remaining = this.duration.getRemainingTime();

        float var4 = 1.0F;

        if (elapsed < FADE_DURATION) {
            var4 = elapsed / (float) FADE_DURATION;
        } else if (remaining < FADE_DURATION) {
            var4 = remaining / (float) FADE_DURATION;
        }

        var4 = Math.max(0.0F, Math.min(1.0F, var4));

        model.leftArm.xRot = (float) Math.toRadians(-180.0F * var4);
        model.leftArm.zRot = (float) Math.toRadians(15.0F * var4);
        model.rightArm.xRot = (float) Math.toRadians(-180.0F * var4);
        model.rightArm.zRot = (float) Math.toRadians(-15.0F * var4);
    }
}
