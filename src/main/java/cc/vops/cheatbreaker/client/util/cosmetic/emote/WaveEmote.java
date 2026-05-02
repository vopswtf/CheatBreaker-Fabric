package cc.vops.cheatbreaker.client.util.cosmetic.emote;

import cc.vops.cheatbreaker.client.ui.fading.CosineFade;
import cc.vops.cheatbreaker.client.ui.fading.FloatFade;
import cc.vops.cheatbreaker.client.util.cosmetic.Emote;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.player.Player;

public class WaveEmote extends Emote {
    private final FloatFade name = new FloatFade(250L);
    private final FloatFade resourceLoc = new FloatFade(250L);
    private final CosineFade waveTransitionTime = new CosineFade(500L);

    public WaveEmote() {
        super("Wave", new FloatFade(2000L));
    }

    @Override
    public void tickEmote(Player player, PlayerModel model, AvatarRenderState state, float partialTicks) {
        if (!this.name.hasStartTime()) {
            this.name.reset();
        }

        float var4 = 1.0F;
        float var5 = 0.5F;
        if (this.name.getDuration() > this.duration.getTimeElapsed()) {
            var4 = this.name.getCurrentValue();
        } else if (this.duration.getRemainingTime() <= this.resourceLoc.getDuration()) {
            if (!this.resourceLoc.hasStartTime()) {
                this.resourceLoc.reset();
            }

            var4 = 1.0F - this.resourceLoc.getCurrentValue();
        } else {
            if (!this.waveTransitionTime.hasStartTime()) {
                this.waveTransitionTime.inOutFade(125.0F);
                this.waveTransitionTime.enableShouldResetOnceCalled();
            }

            var5 = this.waveTransitionTime.getCurrentValue();
        }

        model.leftArm.xRot = (float) Math.toRadians(-150.0F * var4);
        model.leftArm.zRot = (float) Math.toRadians(40.0F * var5 - 20.0F);
    }
}
