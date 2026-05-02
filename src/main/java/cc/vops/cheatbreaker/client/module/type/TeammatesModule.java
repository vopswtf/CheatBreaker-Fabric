package cc.vops.cheatbreaker.client.module.type;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.nethandler.apollo.ApolloNetHandler;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import com.lunarclient.apollo.team.v1.TeamMember;
import com.lunarclient.apollo.team.v1.UpdateTeamMembersMessage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.UUID;

public class TeammatesModule {
    public TeammatesModule() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            CheatBreaker.getInstance().getApolloNetHandler().setCurrentTeam(null);
            CheatBreaker.getInstance().getApolloNetHandler().getAdventureNametagOverrides().clear();
        });

        LevelRenderEvents.END_MAIN.register((context) -> {
            if (!CheatBreaker.getInstance().getApolloNetHandler().hasTeam()) return;
            UpdateTeamMembersMessage team = CheatBreaker.getInstance().getApolloNetHandler().getCurrentTeam();

            var mc = Minecraft.getInstance();
            if (mc.level == null || mc.player == null) return;
            var camera = mc.gameRenderer.getGameRenderState().levelRenderState.cameraRenderState;
            var poseStack = context.poseStack();
            var consumers = context.bufferSource();

            if (consumers == null) return;

            for (TeamMember member : team.getMembersList()) {
                if (!member.getLocation().getWorld().equals(mc.level.dimension().toString())) continue;

                UUID uuid = ApolloNetHandler.convertApolloUUID(member.getPlayerUuid());
                if (uuid.equals(mc.player.getUUID())) {
                    if (mc.options.getCameraType().isFirstPerson()) continue;
                    if (!CheatBreaker.getInstance().getGlobalSettings().showSelfNametag.getAsBoolean()) continue;
                }

                renderAbovePlayer(mc.level.getPlayerByUUID(uuid), member, camera, poseStack, consumers);
            }
        });
    }

    public RenderType render = RenderUtil.icon(CheatBreaker.asset("icons/down-arrow-16.png"));

    private void renderAbovePlayer(Player player, TeamMember member, CameraRenderState camera, PoseStack poseStack, MultiBufferSource consumers) {
        float tickDelta = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);

        double x, y, z;

        if (player != null) {
            double px = Mth.lerp(tickDelta, player.xo, player.getX());
            double py = Mth.lerp(tickDelta, player.yo, player.getY());
            double pz = Mth.lerp(tickDelta, player.zo, player.getZ());
            x = px - camera.pos.x;
            y = py - camera.pos.y + (player.getBbHeight() + 0.35);
            z = pz - camera.pos.z;
        } else {
            x = member.getLocation().getX() - camera.pos.x;
            y = member.getLocation().getY() + 2.5 - camera.pos.y;
            z = member.getLocation().getZ() - camera.pos.z;
        }

        poseStack.pushPose();
        poseStack.translate(x, y, z);

        poseStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());

        double distSq = x * x + y * y + z * z;
        double dist = Math.sqrt(distSq);

        float constantSize = 0.03f;
        float scale = (float)(constantSize * dist);

        poseStack.scale(scale, scale, scale);

        Matrix4f matrix = poseStack.last().pose();
        var buffer = consumers.getBuffer(render);

        Color color = new Color(member.getMarkerColor().getColor());
        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;

        buffer.addVertex(matrix, -0.5f, 0, 0).setUv(0,1).setColor(r, g, b, 1f);
        buffer.addVertex(matrix,  0.5f, 0, 0).setUv(1,1).setColor(r, g, b, 1f);
        buffer.addVertex(matrix,  0.5f, 1, 0).setUv(1,0).setColor(r, g, b, 1f);
        buffer.addVertex(matrix, -0.5f, 1, 0).setUv(0,0).setColor(r, g, b, 1f);

        poseStack.popPose();
    }
}
