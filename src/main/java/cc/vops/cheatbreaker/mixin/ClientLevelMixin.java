package cc.vops.cheatbreaker.mixin;

import cc.vops.cheatbreaker.CheatBreaker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(method = "addEntity", at = @At("HEAD"))
    public void onAddEntity(Entity entity, CallbackInfo ci) {
        if (entity instanceof Player player) {
            CheatBreaker.getInstance().getAssetsWebSocket().sendPlayerJoin(player.getUUID());
        }
    }

    @Inject(method = "removeEntity", at = @At("HEAD"))
    public void onRemoveEntity(int id, Entity.RemovalReason reason, CallbackInfo ci) {
        Entity entity = ((ClientLevel) (Object) this).getEntity(id);
        if (entity instanceof Player player) {
            CheatBreaker.getInstance().getAssetsWebSocket().playerLeave(player.getUUID());
        }
    }
}
