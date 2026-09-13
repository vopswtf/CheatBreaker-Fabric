package cc.vops.cheatbreaker.mixin.nametag;

import cc.vops.cheatbreaker.CheatBreaker;
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

import java.util.List;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Inject(
            method = "shouldShowName*",
            at = @At("HEAD"),
            cancellable = true
    )
    private void shouldShowName(LivingEntity entity, double squaredDistanceToCamera, CallbackInfoReturnable<Boolean> cir) {
        if (entity.getType() != EntityType.PLAYER) return;

        if (CheatBreaker.getInstance().getGlobalSettings().showSelfNametag.getAsBoolean() && entity == Minecraft.getInstance().player) {
            boolean hasDisplayRiding = !entity.getPassengers().isEmpty() && entity.getPassengers().stream().anyMatch(passenger -> passenger.getType() == EntityType.TEXT_DISPLAY);
            if (!hasDisplayRiding) cir.setReturnValue(true);
        }
    }

    @Unique
    private void renderAdventureNametags(LivingEntity entity, double squaredDistanceToCamera, List<String> nametagLines) {
        Minecraft minecraft = Minecraft.getInstance();
        EntityRenderer<Entity, EntityRenderState> entityRenderer = (EntityRenderer<Entity, EntityRenderState>) minecraft.getEntityRenderDispatcher().getRenderer(entity);
        var renderState = entityRenderer.createRenderState();
        var cameraState = minecraft.gameRenderer.getGameRenderState().levelRenderState.cameraRenderState;
        var levelRenderer = minecraft.levelRenderer;
        var renderDispatcher = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();

        Vec3 cameraPos = cameraState.pos;
        double d0 = renderState.x - cameraPos.x();
        double d1 = renderState.y - cameraPos.y();
        double d2 = renderState.z - cameraPos.z();

        PoseStack poseStack = new PoseStack();
        Vec3 offset = entityRenderer.getRenderOffset(renderState);
        double dd2 = d0 + offset.x();
        double dd0 = d1 + offset.y();
        double dd1 = d2 + offset.z();

        poseStack.pushPose();
        poseStack.translate(dd2, dd0, dd1);

        for (int i = 0; i < nametagLines.size(); i++) {
            String nametag = nametagLines.get(i);
            poseStack.pushPose();
            poseStack.translate(0, i, 0);

            renderDispatcher.getSubmitNodeStorage().submitNameTag(
                    poseStack,
                    renderState.nameTagAttachment,
                    0,
                    Component.literal(nametag),
                    !renderState.isDiscrete,
                    renderState.lightCoords,
                    renderState.distanceToCameraSq,
                    cameraState
            );

            poseStack.popPose();
        }

        poseStack.popPose();
    }
}
