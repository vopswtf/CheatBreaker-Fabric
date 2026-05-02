package cc.vops.cheatbreaker.client.util.cosmetic.emote;

import cc.vops.cheatbreaker.client.ui.fading.ExponentialFade;
import cc.vops.cheatbreaker.client.ui.fading.MinMaxFade;
import cc.vops.cheatbreaker.client.util.cosmetic.Emote;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.player.Player;

public class TPoseEmote extends Emote {
    private final ExponentialFade startTransitionTime = new ExponentialFade(200L);
    private final MinMaxFade endTransitionTime = new MinMaxFade(200L);

    public TPoseEmote() {
        super("T-Pose", new MinMaxFade(5000L));
    }

    @Override
    public void tickEmote(Player player, PlayerModel model, AvatarRenderState state, float partialTicks) {
        if (!this.startTransitionTime.hasStartTime()) {
            this.startTransitionTime.reset();
        }

        model.rightArm.xRot *= this.endTransitionTime.getCurrentValue();
        model.leftArm.xRot *= this.endTransitionTime.getCurrentValue();

        if (this.startTransitionTime.getDuration() > this.duration.getTimeElapsed()) {
            model.rightArm.zRot = (float) Math.toRadians(90.0F * this.startTransitionTime.getCurrentValue());
            model.leftArm.zRot = (float) Math.toRadians(-90.0F * this.startTransitionTime.getCurrentValue());
        } else if (this.duration.getRemainingTime() <= this.endTransitionTime.getDuration()) {
            if (!this.endTransitionTime.hasStartTime()) {
                this.endTransitionTime.reset();
            }

            model.leftArm.zRot = Math.min((float) Math.toRadians(-90.0F + 90.0F * this.endTransitionTime.getCurrentValue()), model.leftArm.zRot);
            model.rightArm.zRot = Math.max((float) Math.toRadians(90.0F - 90.0F * this.endTransitionTime.getCurrentValue()), model.rightArm.zRot);
        } else {
            model.rightArm.zRot = (float) Math.toRadians(90.0);
            model.leftArm.zRot = (float) Math.toRadians(-90.0);
        }
    }
}
