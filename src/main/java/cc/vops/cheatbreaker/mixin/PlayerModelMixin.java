package cc.vops.cheatbreaker.mixin;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.util.cosmetic.Emote;
import cc.vops.cheatbreaker.client.util.cosmetic.emote.WaveEmote;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public class PlayerModelMixin {
    @Inject(method = "setupAnim*", at = @At("RETURN"))
    private void onSetupAnim(AvatarRenderState state, CallbackInfo ci) {
        if (Minecraft.getInstance().level == null) return;
        var level = Minecraft.getInstance().level;
        var entity = level.getEntity(state.id);
        if (!(entity instanceof Player player)) return;

        PlayerModel model = (PlayerModel) (Object) this;
        Emote emote = CheatBreaker.getInstance().getEmoteManager().getActiveEmotes().get(player.getUUID());
        if (emote == null) return;

        if (!emote.getDuration().hasStartTime()) {
            emote.getDuration().reset();
        }

        emote.tickEmote(player, model, state, state.ageInTicks);
    }
}
