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

    private void renderWaypoint(Waypoint waypoint, PoseStack poseStack, OrderedSubmitNodeCollector collector, CameraRenderState camera) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        double x = waypoint.x() - camera.pos.x;
        double y = waypoint.y() - camera.pos.y;
        double z = waypoint.z() - camera.pos.z;

        double distSq = x * x + y * y + z * z;
        double dist = Math.sqrt(distSq);

        float scale = computeDistanceScale(dist)
                * CheatBreaker.getInstance().getModuleManager().minmap.waypointNameScale.getAsFloat();

        if (scale <= 0) return;

        String text = waypoint.name();
        if (CheatBreaker.getInstance().getModuleManager().minmap.abbreviateNames.getAsBoolean()) {
            text = abbreviateText(text);
        }

        int textWidth = mc.font.width(text);
        int textHeight = mc.font.lineHeight;

        float width = textWidth + Waypoint.displayXOffset() * 2;
        float height = textHeight + Waypoint.displayYOffset() * 2;
        int color = waypoint.colorInt();

        poseStack.pushPose();
        poseStack.translate(x, y, z);

        //? if >=26.3 {
        poseStack.mulPose(new Matrix4f().set(GameRendererBridge.getMainCamera().rotation()));
         //? } else {
        /*poseStack.mulPose(GameRendererBridge.getMainCamera().rotation());
        *///? }

        poseStack.scale(scale * 0.04F, -scale * 0.04F, scale * 0.04F);

        float halfW = width / 2f;
        float halfH = height / 2f;

        collector.submitCustomGeometry(poseStack, RenderUtil.arrow(), (pose, buffer) -> {
            Matrix4f matrix = pose.pose();
            fillRect(buffer, matrix, -halfW, -halfH, 0.02f, halfW, halfH, color);
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
                0xF000F0,
                -1,
                0,
                0
        );

        poseStack.popPose();
    }

    private void fillRect(VertexConsumer buffer, Matrix4f matrix, float x, float y, float z, float x2, float y2, int color) {
        buffer.addVertex(matrix, x, y, z).setColor(color);
        buffer.addVertex(matrix, x2, y, z).setColor(color);
        buffer.addVertex(matrix, x2, y2, z).setColor(color);
        buffer.addVertex(matrix, x, y2, z).setColor(color);
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