package cc.vops.cheatbreaker.mixin;


import cc.vops.cheatbreaker.client.module.type.AutoHotKeyModule;
import cc.vops.cheatbreaker.client.util.bridge.GuiBridge;
import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.event.type.ClickEvent;
import cc.vops.cheatbreaker.client.ui.AbstractGui;
import cc.vops.cheatbreaker.client.util.Mouse;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Shadow @Final private Minecraft minecraft;

    @Shadow private double xpos;

    @Shadow private double ypos;

    @Inject(method = "onButton", at = @At("HEAD"))
    private void onButton(long window, MouseButtonInfo mouseButtonInfo, int action, CallbackInfo callbackInfo) {
        if (window == Minecraft.getInstance().getWindow().handle()) {
            if (action == ((KeyMappingAccessor) Minecraft.getInstance().options.keyAttack).getKey().getValue()) {
                CheatBreaker.getInstance().getEventBus().callEvent(new ClickEvent(mouseButtonInfo.button()));
            }

//            System.out.println("Mouse button: " + mouseButtonInfo.button() + ", action: " + action);
            switch (mouseButtonInfo.button()) {
                case Mouse.MOUSE_BUTTON_LEFT -> Mouse.mouseLeftDown = action == InputConstants.PRESS;
                case Mouse.MOUSE_BUTTON_RIGHT -> Mouse.mouseRightDown = action == InputConstants.PRESS;
                case Mouse.MOUSE_BUTTON_MIDDLE -> Mouse.mouseMiddleDown = action == InputConstants.PRESS;
                case Mouse.MOUSE_BUTTON_SIDE1 -> Mouse.mouseSideButton1Down = action == InputConstants.PRESS;
                case Mouse.MOUSE_BUTTON_SIDE2 -> Mouse.mouseSideButton2Down = action == InputConstants.PRESS;
            }

            if (AutoHotKeyModule.getInstance().isEnabled()) {
                AutoHotKeyModule.getInstance().keyPress(-mouseButtonInfo.button());
            }
        }
    }

    @Inject(method = "onScroll", at = @At("HEAD"))
    private void keyPress(long window, double a, double yDelta, CallbackInfo callbackInfo) {
        if (window == Minecraft.getInstance().getWindow().handle()) {
            if (GuiBridge.getScreen() instanceof AbstractGui) {
                ((AbstractGui) GuiBridge.getScreen()).onMouseScroll(yDelta);
            }
        }
    }

    @Inject(method = "onMove", at = @At("RETURN"))
    //? if >=26.3 {
    private void onMove(long handle, double xpos, double ypos, double xrel, double yrel, CallbackInfo ci) {
    //? } else {
    /*private void onMove(long window, double xPos, double yPos, CallbackInfo callbackInfo) {
    *///? }
        Mouse.mouseX = xpos;
        Mouse.mouseY = ypos;
    }
}
