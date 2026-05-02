package cc.vops.cheatbreaker.client.util.cosmetic.emote;

import cc.vops.cheatbreaker.client.ui.fading.MinMaxFade;
import cc.vops.cheatbreaker.client.util.cosmetic.Emote;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.player.Player;

public class DabEmote extends Emote {
    private final MinMaxFade name = new MinMaxFade(250L);
    private final MinMaxFade resourceLoc = new MinMaxFade(250L);

    public DabEmote() {
        super("Dab", new MinMaxFade(1500L));
    }

    @Override
    public void tickEmote(Player player, PlayerModel model, AvatarRenderState state, float partialTicks) {
        if (!this.name.hasStartTime()) {
            this.name.reset();
        }

        float var4 = 1.0F;
        if (this.name.getDuration() > this.duration.getTimeElapsed()) {
            var4 = this.name.getCurrentValue();
        } else if (this.duration.getRemainingTime() <= this.resourceLoc.getDuration()) {
            if (!this.resourceLoc.hasStartTime()) {
                this.resourceLoc.reset();
            }

            var4 = 1.0F - this.resourceLoc.getCurrentValue();
        }

        model.rightArm.xRot = (float) Math.toRadians(-90.0F * var4);
        model.rightArm.yRot = (float) Math.toRadians(-35.0F * var4);
        model.leftArm.xRot = (float) Math.toRadians(15.0F * var4);
        model.leftArm.yRot = (float) Math.toRadians(15.0F * var4);
        model.leftArm.zRot = (float) Math.toRadians(-110.0F * var4);

        float var5 = player.getXRot();
        float var6 = player.yBodyRotO - player.yBodyRot;

        model.head.xRot = (float) Math.toRadians(-var5 * var4) + (float) Math.toRadians(45.0F * var4 + var5);
        model.head.yRot = (float) Math.toRadians(var6 * var4) + (float) Math.toRadians(35.0F * var4 - var6);
    }
}
