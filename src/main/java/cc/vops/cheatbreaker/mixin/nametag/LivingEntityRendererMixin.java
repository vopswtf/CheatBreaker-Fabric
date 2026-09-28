package cc.vops.cheatbreaker.mixin.nametag;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.util.bridge.GameRendererBridge;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


//? if >=26.2 {
/*import net.minecraft.world.entity.EntityTypes;
*///? }

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Inject(
            method = "shouldShowName*",
            at = @At("HEAD"),
            cancellable = true
    )
    private void shouldShowName(LivingEntity entity, double squaredDistanceToCamera, CallbackInfoReturnable<Boolean> cir) {

        //? if >=26.2 {
        /*if (entity.getType() != EntityTypes.PLAYER) return;
        *///? } else {
        if (entity.getType() != EntityType.PLAYER) return;
        //? }

        if (CheatBreaker.getInstance().getGlobalSettings().showSelfNametag.getAsBoolean() && entity == Minecraft.getInstance().player) {
            //? if >=26.2 {
            /*boolean hasDisplayRiding = !entity.getPassengers().isEmpty() && entity.getPassengers().stream().anyMatch(passenger -> passenger.getType() == EntityTypes.TEXT_DISPLAY);
            *///? } else {
            boolean hasDisplayRiding = !entity.getPassengers().isEmpty() && entity.getPassengers().stream().anyMatch(passenger -> passenger.getType() == EntityType.TEXT_DISPLAY);
            //? }

            if (!hasDisplayRiding) cir.setReturnValue(true);
        }
    }
}
