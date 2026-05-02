package cc.vops.cheatbreaker.client.util.cosmetic.emote;

import cc.vops.cheatbreaker.client.ui.fading.CosineFade;
import cc.vops.cheatbreaker.client.ui.fading.ExponentialFade;
import cc.vops.cheatbreaker.client.ui.fading.FloatFade;
import cc.vops.cheatbreaker.client.util.cosmetic.Emote;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

public class FlossEmote extends Emote {
    private final ExponentialFade swapTransitionTime = new ExponentialFade(375L);
    private final CosineFade swingTransitionTime = new CosineFade(375L);
    private final CosineFade bodyRotationTransitionTime = new CosineFade(250L);
    boolean swap = false;
    private FlossStage flossStage;

    public FlossEmote() {
        super("Floss", new FloatFade(7500L));
        this.flossStage = FlossStage.LEFT_TO_RIGHT;
    }

    @Override
    public void tickEmote(Player player, PlayerModel model, AvatarRenderState state, float partialTicks) {
        if (!this.bodyRotationTransitionTime.hasStartTime()) {
            this.bodyRotationTransitionTime.reset();
        }

        if (!this.swapTransitionTime.hasStartTime()) {
            if ((double) this.bodyRotationTransitionTime.getCurrentValue() >= 0.5) {
                this.swapTransitionTime.reset();
                this.swingTransitionTime.reset();
            }
        } else if (this.swapTransitionTime.isExpired()) {
            this.swapTransitionTime.reset();
            this.swingTransitionTime.reset();
            this.flossStage = this.getFlossStage();
        }
        if (this.bodyRotationTransitionTime.isExpired()) {
            this.swap = !this.swap;
            this.bodyRotationTransitionTime.reset();
        }

        float f2 = this.swapTransitionTime.getCurrentValue();
        float f3 = this.swingTransitionTime.getCurrentValue();

        // Forward/back motion
        model.rightArm.xRot = (float) Math.toRadians((float) (this.flossStage == FlossStage.RIGHT_TO_BACK ? 45 : -45) * f3);
        model.leftArm.xRot = (float) Math.toRadians((float) (this.flossStage == FlossStage.LEFT_TO_BACK ? 45 : -45) * f3);

        float f4 = 150.0f;
        float f5 = f4 / 2.0f;

        // Side-to-side swing motion
        switch (this.flossStage) {
            case LEFT_TO_RIGHT:
                model.rightArm.zRot = (float) Math.toRadians(f4 * f2 - f5);
                model.leftArm.zRot = (float) Math.toRadians(f4 * f2 - f5);
                break;

            case RIGHT_TO_BACK:
                model.rightArm.zRot = (float) Math.toRadians(f5 - f5 * f3);
                model.leftArm.zRot = (float) Math.toRadians(f5 - f5 * f3);
                break;

            case RIGHT_TO_LEFT:
                model.rightArm.zRot = (float) Math.toRadians(-f4 * f2 + f5);
                model.leftArm.zRot = (float) Math.toRadians(-f4 * f2 + f5);
                break;

            case LEFT_TO_BACK:
                model.rightArm.zRot = (float) Math.toRadians(f5 * f3 - f5);
                model.leftArm.zRot = (float) Math.toRadians(f5 * f3 - f5);
                break;
        }

        // Body rotation
        f3 = this.bodyRotationTransitionTime.getCurrentValue();
        if (this.swap) {
            model.body.zRot = (float) Math.toRadians(-15.0f * f3);
            model.rightLeg.zRot = (float) Math.toRadians(15.0f * f3);
            model.leftLeg.zRot = (float) Math.toRadians(15.0f * f3);
            model.leftLeg.offsetPos(new Vector3f(2f * f3, 0.0f, 0f));
            model.rightLeg.offsetPos(new Vector3f(2f * f3, 0.0f, 0f));
        } else {
            model.body.zRot = (float) Math.toRadians(15.0f * f3);
            model.rightLeg.zRot = (float) Math.toRadians(-15.0f * f3);
            model.leftLeg.zRot = (float) Math.toRadians(-15.0f * f3);
            model.leftLeg.offsetPos(new Vector3f(-2f * f3, 0, 0f));
            model.rightLeg.offsetPos(new Vector3f(-2f * f3, 0, 0f));
        }
    }

    private FlossStage getFlossStage() {
        switch (this.flossStage) {
            case LEFT_TO_RIGHT:
                return FlossStage.RIGHT_TO_BACK;
            case RIGHT_TO_BACK:
                return FlossStage.RIGHT_TO_LEFT;
            case RIGHT_TO_LEFT:
                return FlossStage.LEFT_TO_BACK;
            case LEFT_TO_BACK:
        }
        return FlossStage.LEFT_TO_RIGHT;
    }

    enum FlossStage {
        LEFT_TO_RIGHT, RIGHT_TO_LEFT, RIGHT_TO_BACK, LEFT_TO_BACK
    }
}