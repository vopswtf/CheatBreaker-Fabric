package cc.vops.cheatbreaker.mixin;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.event.type.ClickEvent;
import cc.vops.cheatbreaker.client.event.type.KeyboardEvent;
import cc.vops.cheatbreaker.client.ui.AbstractGui;
import cc.vops.cheatbreaker.client.ui.module.CBModulesGui;
import cc.vops.cheatbreaker.client.util.Mouse;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonInfo;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "onButton", at = @At("HEAD"))
    private void onButton(long window, MouseButtonInfo mouseButtonInfo, int action, CallbackInfo callbackInfo) {
        if (window == Minecraft.getInstance().getWindow().handle()) {
            if (action == GLFW.GLFW_PRESS) {
                CheatBreaker.getInstance().getEventBus().callEvent(new ClickEvent(mouseButtonInfo.button()));
            }

            switch (mouseButtonInfo.button()) {
                case 0 -> Mouse.mouseLeftDown = action == GLFW.GLFW_PRESS;
                case 1 -> Mouse.mouseRightDown = action == GLFW.GLFW_PRESS;
                case 2 -> Mouse.mouseMiddleDown = action == GLFW.GLFW_PRESS;
                case 3 -> Mouse.mouseSideButton1Down = action == GLFW.GLFW_PRESS;
                case 4 -> Mouse.mouseSideButton2Down = action == GLFW.GLFW_PRESS;
            }
        }
    }

    @Inject(method = "onScroll", at = @At("HEAD"))
    private void keyPress(long window, double a, double yDelta, CallbackInfo callbackInfo) {
        if (window == Minecraft.getInstance().getWindow().handle()) {
            if (minecraft.screen instanceof AbstractGui) {
                ((AbstractGui) minecraft.screen).onMouseScroll(yDelta);
            }
        }
    }
}
