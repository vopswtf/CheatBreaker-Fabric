package cc.vops.cheatbreaker.client;

import cc.vops.cheatbreaker.CheatBreaker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class CheatbreakerModClient implements ClientModInitializer {
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(CheatBreaker.asset("bindings"));

    public static KeyMapping openMenu;
    public static KeyMapping openVoiceMenu;
    public static KeyMapping pushToTalk;
    public static KeyMapping dragLook;
    public static KeyMapping hideNames;

    @Override
    public void onInitializeClient() {
        pushToTalk = KeyMappingHelper.registerKeyMapping(new KeyMapping("Voice Chat", GLFW.GLFW_KEY_V, CATEGORY));
        openMenu = KeyMappingHelper.registerKeyMapping(new KeyMapping("Open Menu", GLFW.GLFW_KEY_RIGHT_SHIFT, CATEGORY));
        openVoiceMenu = KeyMappingHelper.registerKeyMapping(new KeyMapping("Open Voice Menu", GLFW.GLFW_KEY_P, CATEGORY));
        dragLook = KeyMappingHelper.registerKeyMapping(new KeyMapping("Drag to Look", GLFW.GLFW_KEY_LEFT_ALT, CATEGORY));
        hideNames = KeyMappingHelper.registerKeyMapping(new KeyMapping("Hide Name Plates", GLFW.GLFW_KEY_H, CATEGORY));
    }
}
