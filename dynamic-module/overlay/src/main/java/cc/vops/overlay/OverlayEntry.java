package cc.vops.overlay;

import cc.vops.cheatbreaker.client.module.ModuleManager;
import net.fabricmc.api.ModInitializer;

public class OverlayEntry implements ModInitializer {

    @Override
    public void onInitialize() {
        ModuleManager.registerModule(OverlayModule.class);
    }
}
