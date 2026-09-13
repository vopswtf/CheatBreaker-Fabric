package cc.vops.cheatbreaker.mixin.nametag;

import cc.vops.cheatbreaker.CheatBreaker;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static cc.vops.cheatbreaker.client.nethandler.apollo.ApolloNetHandler.parseComponent;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin {
    @Unique
    private static final int LINE_SPACING = 10;

    @Inject(method = "submitNameDisplay*", at = @At("HEAD"), cancellable = true)
    public void submitNameTagInject(AvatarRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, CallbackInfo ci) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;
        var entity = level.getEntity(renderState.id);
        var apolloNetHandler = CheatBreaker.getInstance().getApolloNetHandler();
        if (entity == null || apolloNetHandler == null) return;

        if (apolloNetHandler.getAdventureNametagOverrides().containsKey(entity.getUUID())) {
            ci.cancel();
            renderAdventureNametags(renderState, poseStack, submitNodeCollector, cameraRenderState, apolloNetHandler.getAdventureNametagOverrides().get(entity.getUUID()));
        }
    }

    @Unique
    private void renderAdventureNametags(AvatarRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, List<Component> nametagLines) {
        poseStack.pushPose();
        int yOffset = renderState.showExtraEars ? -LINE_SPACING : 0;

        boolean crouching = renderState.isCrouching;
        int last = nametagLines.size() - 1;
        for (int i = last; i >= 0; i--) {
            if (crouching && i != last) continue;

            Component component = nametagLines.get(i);
            submitNodeCollector.submitNameTag(
                    poseStack, renderState.nameTagAttachment, yOffset, component, !renderState.isDiscrete, renderState.lightCoords, renderState.distanceToCameraSq, cameraRenderState
            );
            yOffset -= LINE_SPACING;
        }

        poseStack.popPose();
    }
}
