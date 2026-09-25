package cc.vops.cheatbreaker.client.util.bridge;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.GameRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;

public class GameRendererBridge {
    public static GameRenderState getGameRenderState() {
        //? if >=26.2 {
        return Minecraft.getInstance().gameRenderer.gameRenderState();
        //? } else {
        /*return Minecraft.getInstance().gameRenderer.getGameRenderState();
        *///? }
    }

    public static GuiRenderState getGuiRenderState() {
        //? if >=26.2 {
        return Minecraft.getInstance().gameRenderer.gameRenderState().guiRenderState;
        //? } else {
        /*return getGameRenderState().guiRenderState;
        *///? }
    }

    public static Camera getMainCamera() {
        //? if >=26.2 {
        return Minecraft.getInstance().gameRenderer.mainCamera();
        //? } else {
        /*return Minecraft.getInstance().gameRenderer.getMainCamera();
        *///? }
    }

    public static FeatureRenderDispatcher getFeatureRenderDispatcher() {
        //? if >=26.2 {
        return Minecraft.getInstance().gameRenderer.featureRenderDispatcher();
        //? } else {
        /*return Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
        *///? }
    }
}
