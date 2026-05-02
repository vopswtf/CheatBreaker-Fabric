package cc.vops.cheatbreaker.client;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class CheatBreakerPreLaunch implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            System.setProperty("devauth.enabled", "true");
            System.setProperty("devauth.account", "main");
        }
    }
}
