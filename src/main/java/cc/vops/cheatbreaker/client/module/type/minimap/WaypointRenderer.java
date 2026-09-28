package cc.vops.cheatbreaker.client.module.type.minimap;


import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.GlobalSettings;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import cc.vops.cheatbreaker.client.util.bridge.GameRendererBridge;
import cc.vops.cheatbreaker.mixin.GameRendererAccessor;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.awt.*;
import java.lang.Math;
import java.util.*;
import java.util.List;

public class WaypointRenderer {

    public WaypointRenderer() {
        LevelRenderEvents.COLLECT_SUBMITS.register((context) -> {
            try {
                renderWaypoints(context.poseStack(), context.submitNodeCollector());
            } catch (Exception e) {
                CheatBreaker.LOGGER.error("Error rendering waypoints", e);
            }
        });
    }

    public void renderWaypoints(PoseStack poseStack, OrderedSubmitNodeCollector collector) {
        MiniMapModule module = CheatBreaker.getInstance().getModuleManager().minmap;

        if (FabricLoader.getInstance().isDevelopmentEnvironment() && module.waypointStorage.getWaypoints().isEmpty()) {
            // add Rally at 0, 100, 0 for testing
            module.waypointStorage.getWaypoints().add(new Waypoint("world", 0, 100, 0, Color.RED, "Rally", false, false, true));
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        CameraRenderState camera = GameRendererBridge.getGameRenderState().levelRenderState.cameraRenderState;

        for (Waypoint waypoint : module.waypointStorage.getWaypoints()) {
            if (!waypoint.isServerWaypoint()) {
                if (!module.isEnabled()) continue;
                if (!module.showGameWaypoints.getAsBoolean()) continue;
            }

            renderWaypoint(waypoint, poseStack, collector, camera);
        }
    }

    private static final int CIRCLE_SEGMENTS = 32;
    private static final float CIRCLE_RADIUS = 1f;
    private static final float CIRCLE_THICKNESS = 0.05f;

    private void renderWaypoint(Waypoint waypoint, PoseStack poseStack, OrderedSubmitNodeCollector collector, CameraRenderState camera) {
        MiniMapModule minmap = CheatBreaker.getInstance().getModuleManager().minmap;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        double x = waypoint.x() - camera.pos.x;
        double y = waypoint.y() - camera.pos.y;
        double z = waypoint.z() - camera.pos.z;

        double distSq = x * x + y * y + z * z;
        double dist = Math.sqrt(distSq);

        float scale = computeDistanceScale(dist) * minmap.waypointNameScale.getAsFloat();
        if (scale <= 0) return;

        String text = waypoint.name();
        if (minmap.abbreviateNames.getAsBoolean()) {
            text = abbreviateText(text);
        }

        text += " [" + (int) dist + "m]";

        int textWidth = mc.font.width(text);
        int textHeight = mc.font.lineHeight;

        float extraPadX = 2f;
        float extraPadY = 2f;

        float width = textWidth + Waypoint.displayXOffset() * 2 + extraPadX * 2;
        float height = textHeight + Waypoint.displayYOffset() * 2 + extraPadY * 2;
        int color = waypoint.colorInt();

        float halfW = width / 2f;
        float halfH = height / 2f;
        float thickness = 0.3f;
        float labelOpacity;

        if (dist < 15) { // scales from 1 to 0 opacity (15m to 5m)
            labelOpacity = (float) Mth.clamp((dist - 5) / 10, 0, 1);
        } else {
            labelOpacity = 1;
        }

        // label
        if (labelOpacity > 0 && minmap.showNames.getAsBoolean()) {
            poseStack.pushPose();
            poseStack.translate(x, y + 1.4f, z);

            //? if >=26.3 {
            /*poseStack.mulPose(new Matrix4f().set(GameRendererBridge.getMainCamera().rotation()));
            *///? } else {
            poseStack.mulPose(GameRendererBridge.getMainCamera().rotation());
             //? }

            poseStack.scale(scale * 0.04F, -scale * 0.04F, scale * 0.04F);

            collector.submitCustomGeometry(poseStack, RenderUtil.arrow(), (pose, buffer) -> {
                Matrix4f matrix = pose.pose();
                int outlineColor = ARGB.color((int)(255 * labelOpacity), waypoint.colorInt());
                int bgAlpha = (int)(90 * labelOpacity);

                // outline
                outlineRect(buffer, matrix, -halfW, -halfH, halfW, halfH, 0.01f, thickness, outlineColor);

                // black background
                fillRect(buffer, matrix, -halfW, -halfH, 0.02f, halfW, halfH, ARGB.color(bgAlpha, 0, 0, 0));
            });

            float textX = -textWidth / 2f + 0.5f;
            float textY = -textHeight / 2f + 1f;
            collector.submitText(
                    poseStack,
                    textX,
                    textY,
                    FormattedCharSequence.forward(text, Style.EMPTY),
                    false,
                    Font.DisplayMode.SEE_THROUGH,
                    0x00F000F0,
                    ARGB.color((int) (255 * labelOpacity), 255, 255, 255),
                    0,
                    0
            );

            poseStack.popPose();
        }


        // beam and circle
        poseStack.pushPose();
        poseStack.translate(x, y + 0.01, z);


        // circle
        if (minmap.showCircle.getAsBoolean()) {
            collector.submitCustomGeometry(poseStack, RenderTypes.debugQuads(), (pose, buffer) -> {
                drawRingOutline(buffer, pose.pose(), CIRCLE_RADIUS - CIRCLE_THICKNESS, CIRCLE_RADIUS, CIRCLE_SEGMENTS, ARGB.color(255, color));
            });
        }

        if (minmap.showBeams.getAsBoolean() && labelOpacity > 0) {
            float yaw = (float) Math.atan2(camera.pos.x - waypoint.x(), camera.pos.z - waypoint.z());
            final float finalLabelOpacity = labelOpacity;
            collector.submitCustomGeometry(poseStack, RenderTypes.debugQuads(), (pose, buffer) -> {
                Matrix4f matrix = pose.pose();

                int alpha = (int) (100 * finalLabelOpacity);
                int c = ARGB.color(alpha, color);

                Matrix4f beamMatrix1 = new Matrix4f(matrix).rotateY(yaw);
                addBeamQuad(buffer, beamMatrix1, c);

                Matrix4f beamMatrix2 = new Matrix4f(matrix).rotateY(yaw + (float) (Math.PI / 2));
                addBeamQuad(buffer, beamMatrix2, c);
            });
        }

        poseStack.popPose();
    }

    private void addBeamQuad(VertexConsumer buffer, Matrix4f beamMatrix, int color) {
        buffer.addVertex(beamMatrix, -BEAM_WIDTH, 0, 0).setColor(color);
        buffer.addVertex(beamMatrix, BEAM_WIDTH, 0, 0).setColor(color);
        buffer.addVertex(beamMatrix, BEAM_WIDTH, BEAM_HEIGHT, 0).setColor(color);
        buffer.addVertex(beamMatrix, -BEAM_WIDTH, BEAM_HEIGHT, 0).setColor(color);
    }

    private void fillRect(VertexConsumer buffer, Matrix4f matrix, float x, float y, float z, float x2, float y2, int color) {
        buffer.addVertex(matrix, x, y, z).setColor(color);
        buffer.addVertex(matrix, x2, y, z).setColor(color);
        buffer.addVertex(matrix, x2, y2, z).setColor(color);
        buffer.addVertex(matrix, x, y2, z).setColor(color);
    }

    private void outlineRect(VertexConsumer buffer, Matrix4f matrix, float x, float y, float x2, float y2, float z, float thickness, int color) {
        fillRect(buffer, matrix, x - thickness, y - thickness, z, x2 + thickness, y, color);
        fillRect(buffer, matrix, x - thickness, y2, z, x2 + thickness, y2 + thickness, color);
        fillRect(buffer, matrix, x - thickness, y, z, x, y2, color);
        fillRect(buffer, matrix, x2, y, z, x2 + thickness, y2, color);
    }

    private static final float BEAM_HEIGHT = 255f;
    private static final float BEAM_WIDTH = 0.15f;

    private void drawRingOutline(VertexConsumer buffer, Matrix4f matrix, float innerRadius, float outerRadius, int segments, int color) {
        for (int i = 0; i < segments; i++) {
            float theta1 = (float) (2 * Math.PI * i / segments);
            float theta2 = (float) (2 * Math.PI * (i + 1) / segments);

            float innerX1 = innerRadius * Mth.cos(theta1);
            float innerZ1 = innerRadius * Mth.sin(theta1);
            float innerX2 = innerRadius * Mth.cos(theta2);
            float innerZ2 = innerRadius * Mth.sin(theta2);

            float outerX1 = outerRadius * Mth.cos(theta1);
            float outerZ1 = outerRadius * Mth.sin(theta1);
            float outerX2 = outerRadius * Mth.cos(theta2);
            float outerZ2 = outerRadius * Mth.sin(theta2);

            buffer.addVertex(matrix, innerX1, 0, innerZ1).setColor(color);
            buffer.addVertex(matrix, outerX1, 0, outerZ1).setColor(color);
            buffer.addVertex(matrix, outerX2, 0, outerZ2).setColor(color);
            buffer.addVertex(matrix, innerX2, 0, innerZ2).setColor(color);
        }
    }

    private float computeDistanceScale(double distance) {
        if (distance > 500) return 0F;
        float base = 1F;
        float growth = 0.045F;

        return (float) (base + distance * growth);
    }

    public static String abbreviateText(String text) {
        // "Hello World" -> "HW"
        return Arrays.stream(text.split(" "))
                .filter(s -> !s.isEmpty())
                .map(s -> s.substring(0, 1))
                .reduce("", String::concat);
    }
}