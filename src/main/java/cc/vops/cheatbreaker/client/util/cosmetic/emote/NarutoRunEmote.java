package cc.vops.cheatbreaker.client.util.cosmetic.emote;

import cc.vops.cheatbreaker.client.ui.fading.*;
import cc.vops.cheatbreaker.client.util.cosmetic.Emote;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.player.Player;

public class NarutoRunEmote extends Emote {
    private final ExponentialFade startTransitionTime = new ExponentialFade(300L);
    private final MinMaxFade endTransitionTime = new MinMaxFade(500L);

    public NarutoRunEmote() {
        super("Naruto Run", new MinMaxFade(10000L));
    }

    @Override
    public void tickEmote(Player player, PlayerModel model, AvatarRenderState state, float partialTicks) {
        if (!startTransitionTime.hasStartTime()) {
            startTransitionTime.reset();
        }

        model.rightArm.xRot *= this.endTransitionTime.getCurrentValue();
        model.leftArm.xRot *= this.endTransitionTime.getCurrentValue();

        if (this.startTransitionTime.isFadeOngoing()) {

            model.rightArm.xRot = (float) Math.toRadians(90.0F * this.startTransitionTime.getCurrentValue());
            model.leftArm.xRot = (float) Math.toRadians(90.0F * this.startTransitionTime.getCurrentValue());

        } else if (this.duration.getRemainingTime() <= this.endTransitionTime.getDuration()) {
            if (!this.endTransitionTime.hasStartTime()) {
                this.endTransitionTime.reset();
            }

            model.rightArm.xRot = Math.max((float) Math.toRadians(90.0F - 90.0F * this.endTransitionTime.getCurrentValue()), model.rightArm.zRot);
            model.leftArm.xRot = Math.min((float) Math.toRadians(-270.0F - 90.0F * this.endTransitionTime.getCurrentValue()), model.rightArm.zRot);
        } else {
            state.isCrouching = true;
            model.rightArm.xRot = (float) Math.toRadians(90.0);
            model.leftArm.xRot = (float) Math.toRadians(90.0);
        }

    }
}
