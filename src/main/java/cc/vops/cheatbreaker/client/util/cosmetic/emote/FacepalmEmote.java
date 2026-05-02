package cc.vops.cheatbreaker.client.util.cosmetic.emote;

import cc.vops.cheatbreaker.client.ui.fading.*;
import cc.vops.cheatbreaker.client.util.cosmetic.Emote;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.player.Player;

public class FacepalmEmote extends Emote {
    private final ExponentialFade startTransitionTime = new ExponentialFade(150L);
    private final ExponentialFade endTransitionTime = new ExponentialFade(200L);
    private final CosineFade headShakeTime = new CosineFade(300L);
    private final float headXRotationAngle = (float) Math.toRadians(45.0);
    private final float rightArmYRotationAngle = (float) Math.toRadians(-30.0);
    private final float rightArmXRotationAngle = (float) Math.toRadians(-100.0);

    public FacepalmEmote() {
        super("Facepalm", new FloatFade(2000L));
    }

    @Override
    public void tickEmote(Player player, PlayerModel model, AvatarRenderState state, float partialTicks) {
        float fadeAmount = this.startTransitionTime.getCurrentValue();
        if (!this.startTransitionTime.hasStartTime() && this.duration.getTimeElapsed() >= 150L) {
            this.startTransitionTime.reset();
        }

        if (this.startTransitionTime.hasStartTime()) {
            if (this.startTransitionTime.isExpired() && !this.headShakeTime.isFadeOngoing() && !this.endTransitionTime.hasStartTime()) {
                this.headShakeTime.reset();
            }

            float var5 = model.head.xRot;
            float var6 = model.head.yRot;

            model.head.zRot = -((float) Math.toRadians(10.0F * this.headShakeTime.getCurrentValue()));
            model.head.yRot = (float) Math.toRadians(10.0) * fadeAmount - (float) Math.toRadians(10.0F * this.headShakeTime.getCurrentValue());
            model.head.xRot = this.headXRotationAngle * fadeAmount;

            model.rightArm.yRot = this.rightArmYRotationAngle * fadeAmount - (this.endTransitionTime.hasStartTime() ? 0.0F : (float) Math.toRadians(10.0F * this.headShakeTime.getCurrentValue()));
            model.rightArm.xRot = this.rightArmXRotationAngle * fadeAmount;

            if (!this.endTransitionTime.hasStartTime() && this.duration.getRemainingTime() <= this.endTransitionTime.getDuration()) {
                this.endTransitionTime.reset();
            }

            if (this.endTransitionTime.hasStartTime()) {
                fadeAmount = this.endTransitionTime.getCurrentValue();
                model.head.yRot = var6 * fadeAmount;
                model.head.zRot = 0.0F;
                model.head.xRot -= (this.headXRotationAngle - var5) * fadeAmount;

                model.rightArm.yRot -= this.rightArmYRotationAngle * fadeAmount;
                model.rightArm.xRot -= this.rightArmXRotationAngle * fadeAmount;
            }

        }
    }
}
