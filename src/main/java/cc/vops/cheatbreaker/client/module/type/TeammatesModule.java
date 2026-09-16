package cc.vops.cheatbreaker.client.module.type;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.nethandler.apollo.ApolloNetHandler;
import cc.vops.cheatbreaker.client.util.PlayerHeads;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import com.lunarclient.apollo.team.v1.TeamMember;
import com.lunarclient.apollo.team.v1.UpdateTeamMembersMessage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
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

            for (TeamMember member : team.getMembersList()) {
                if (!member.getLocation().getWorld().equals(CheatBreaker.getInstance().getApolloNetHandler().getWorldName())) {
                    continue;
                }

                UUID uuid = ApolloNetHandler.convertApolloUUID(member.getPlayerUuid());
                if (uuid.equals(mc.player.getUUID())) {
                    if (mc.options.getCameraType().isFirstPerson()) continue;
                    if (!CheatBreaker.getInstance().getGlobalSettings().showSelfNametag.getAsBoolean()) continue;
                }

                renderAbovePlayer(mc.level.getPlayerByUUID(uuid), member, camera, poseStack, consumers);
            }

            consumers.endBatch();
        });
    }

    private void renderAbovePlayer(Player player, TeamMember member, CameraRenderState camera, PoseStack poseStack, MultiBufferSource.BufferSource consumers) {
        float tickDelta = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);

        double x, y, z;

        float yOffset = EntityType.PLAYER.getDimensions().height() + .6f;
        boolean showHead = CheatBreaker.getInstance().getGlobalSettings().showTeamHeads.getAsBoolean();

        if (player != null) {
            double px = Mth.lerp(tickDelta, player.xo, player.getX());
            double py = Mth.lerp(tickDelta, player.yo, player.getY());
            double pz = Mth.lerp(tickDelta, player.zo, player.getZ());
            x = px - camera.pos.x;
            y = py - camera.pos.y + yOffset;
            z = pz - camera.pos.z;
            showHead = false;
        } else {
            x = member.getLocation().getX() - camera.pos.x;
            y = member.getLocation().getY() + yOffset - camera.pos.y;
            z = member.getLocation().getZ() - camera.pos.z;
        }

        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());

        double distSq = x * x + y * y + z * z;
        double dist = Math.sqrt(distSq);

        float scale = (float) (0.03f * dist);
        poseStack.scale(scale, scale, scale);

        Matrix4f matrix = poseStack.last().pose();

        var buffer = consumers.getBuffer(RenderUtil.arrow());
        Color color = new Color(member.getMarkerColor().getColor());

        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;

        float thickness = 0.4f;

        addThickLine(buffer, matrix, -0.5f, 0.5f, 0.0f, 0.0f, thickness, r, g, b);
        addThickLine(buffer, matrix, 0.0f, 0.0f, 0.5f, 0.5f, thickness, r, g, b);

        // fix fill
        float half = thickness / 2f;
        float diag = half * 0.70710678f;
        buffer.addVertex(matrix, 0f, 0f, 0).setColor(r, g, b, 1f);
        buffer.addVertex(matrix, -diag, -diag, 0).setColor(r, g, b, 1f);
        buffer.addVertex(matrix, diag, -diag, 0).setColor(r, g, b, 1f);
        buffer.addVertex(matrix, diag, -diag, 0).setColor(r, g, b, 1f);

        if (CheatBreaker.getInstance().getGlobalSettings().showTeamHeads.getAsBoolean()) {
            // TODO
//            UUID uuid = ApolloNetHandler.convertApolloUUID(member.getPlayerUuid());
//            Identifier identifier = PlayerHeads.getHeadLocation(uuid.toString(), uuid);
//
//            RenderType renderType = RenderTypes.text(identifier);
//            VertexConsumer headBuffer = consumers.getBuffer(renderType);
//
//            float headSize = 1.0f;
//            float headHalf = headSize / 2f;
//            float headYOffset = 0.6f;
//
//            headBuffer.addVertex(matrix, -headHalf, headYOffset - headHalf, 0f).setColor(255, 255, 255, 255).setUv(0f, 0f).setLight(0xF000F0);
//            headBuffer.addVertex(matrix, -headHalf, headYOffset + headHalf, 0f).setColor(255, 255, 255, 255).setUv(0f, 1f).setLight(0xF000F0);
//            headBuffer.addVertex(matrix,  headHalf, headYOffset + headHalf, 0f).setColor(255, 255, 255, 255).setUv(1f, 1f).setLight(0xF000F0);
//            headBuffer.addVertex(matrix,  headHalf, headYOffset - headHalf, 0f).setColor(255, 255, 255, 255).setUv(1f, 0f).setLight(0xF000F0);
//
//            consumers.endBatch(renderType);
        }

        poseStack.popPose();
    }

    private void addThickLine(VertexConsumer buffer, Matrix4f matrix, float x1, float y1, float x2, float y2, float thickness, float r, float g, float b) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len < 1.0e-5f) return;

        float half = thickness / 2f;
        float nx = -dy / len * half;
        float ny = dx / len * half;

        buffer.addVertex(matrix, x1 + nx, y1 + ny, 0).setColor(r, g, b, 1f);
        buffer.addVertex(matrix, x1 - nx, y1 - ny, 0).setColor(r, g, b, 1f);
        buffer.addVertex(matrix, x2 - nx, y2 - ny, 0).setColor(r, g, b, 1f);
        buffer.addVertex(matrix, x2 + nx, y2 + ny, 0).setColor(r, g, b, 1f);
    }
}
