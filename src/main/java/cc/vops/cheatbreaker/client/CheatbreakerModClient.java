package cc.vops.cheatbreaker.client;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jspecify.annotations.NonNull;

public class CheatbreakerModClient implements ClientModInitializer {
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(CheatBreaker.asset("bindings"));

    public static KeyMapping openMenu;
    public static KeyMapping openVoiceMenu;
    public static KeyMapping pushToTalk;
    public static KeyMapping dragLook;
    public static KeyMapping hideNames;
    public static KeyMapping emoteMenu;

    @Override
    public void onInitializeClient() {
        pushToTalk = KeyMappingHelper.registerKeyMapping(new KeyMapping("Voice Chat", InputConstants.KEY_V, CATEGORY));
        openMenu = KeyMappingHelper.registerKeyMapping(new KeyMapping("Open Menu", InputConstants.KEY_RSHIFT, CATEGORY));
        openVoiceMenu = KeyMappingHelper.registerKeyMapping(new KeyMapping("Open Voice Menu", InputConstants.KEY_P, CATEGORY));
        dragLook = KeyMappingHelper.registerKeyMapping(new KeyMapping("Drag to Look", InputConstants.KEY_LALT, CATEGORY));
        hideNames = KeyMappingHelper.registerKeyMapping(new KeyMapping("Hide Name Plates", InputConstants.KEY_H, CATEGORY));
        emoteMenu = KeyMappingHelper.registerKeyMapping(new KeyMapping("Emote Menu", InputConstants.KEY_B, CATEGORY));

        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(CheatBreaker.asset("reload"), new SimpleSynchronousResourceReloadListener() {
            @Override
            public void onResourceManagerReload(@NonNull ResourceManager resourceManager) {
                Fonts.reloadFonts();
            }

            @Override public @NonNull Identifier getFabricId() {
                return CheatBreaker.asset("reload");
            }
        });
    }
}
