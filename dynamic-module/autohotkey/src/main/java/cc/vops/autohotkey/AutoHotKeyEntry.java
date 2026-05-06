package cc.vops.autohotkey;

import cc.vops.cheatbreaker.client.module.ModuleManager;
import net.fabricmc.api.ModInitializer;

public class AutoHotKeyEntry implements ModInitializer {

    @Override
    public void onInitialize() {
        // You should register any modules in onInitialize!
        ModuleManager.registerModule(AutoHotKeyModule.class);
    }
}
