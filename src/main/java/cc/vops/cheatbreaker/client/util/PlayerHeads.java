package cc.vops.cheatbreaker.client.util;

import cc.vops.cheatbreaker.CheatBreaker;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerHeads {
    private final static Map<String, Identifier> playerSkins = new HashMap<>();

    public static Identifier getLocalPlayerHead() {
        return getHeadLocation(Minecraft.getInstance().getGameProfile().name(), Minecraft.getInstance().getGameProfile().id());
    }

    public static Identifier getHeadLocation(String displayName, UUID uuid) {
        Identifier playerSkin = playerSkins.getOrDefault(displayName, CheatBreaker.asset("heads/" + uuid));
        if (!playerSkins.containsKey(displayName)) {
            playerSkins.put(displayName, playerSkin);

            new ThreadDownloadPlayerHead(
                    displayName,
                    "https://minotar.net/helm/" + displayName + "/32.png",
                    playerSkin,
                    CheatBreaker.asset("defaults/steve.png")
            );
        }
        return playerSkin;
    }

    public static class ThreadDownloadPlayerHead {
        public ThreadDownloadPlayerHead(String username, String imageUrl, Identifier headLocation, Identifier defaultLocation) {
            try {
                InputStream defaultStream = Minecraft.getInstance().getResourceManager().open(defaultLocation);
                Minecraft.getInstance().getTextureManager().register(headLocation, new DynamicTexture(() -> username + " Head", NativeImage.read(defaultStream)));

                Thread t = new Thread(() -> {
                    try (InputStream imageStream = URI.create(imageUrl).toURL().openStream()) {
                        NativeImage image = NativeImage.read(imageStream);
                        Minecraft.getInstance().execute(() -> Minecraft.getInstance().getTextureManager().register(headLocation, new DynamicTexture(() -> username + " Head", image)));
                    } catch (Exception e) {
                        CheatBreaker.LOGGER.info("Error loading: " + imageUrl + " - " + e);
                    }
                });
                t.setDaemon(true);
                t.start();
            } catch (Exception e) {
                CheatBreaker.LOGGER.info("Error loading default head image: " + e.toString());
            }
        }
    }

}
