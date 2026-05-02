package cc.vops.cheatbreaker.client.util;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.util.cosmetic.Cosmetic;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class AssetDownloader {
    private final static Map<Cosmetic.CosmeticType, Map<String, Identifier>> assets = new HashMap<>();

    public static Identifier getAsset(Cosmetic.CosmeticType type, String id, String url) {
        // never used
        if (type == Cosmetic.CosmeticType.CAPE && url.endsWith("preview=true")) return CheatBreaker.asset("preview_cape.png");

        Map<String, Identifier> typeAssets = assets.computeIfAbsent(type, k -> new HashMap<>());
        Identifier loc = CheatBreaker.asset("downloaded/" + type.name().toLowerCase() + "/" + id + ".png");
        if (!typeAssets.containsKey(id)) {
            typeAssets.put(id, loc);
            new ThreadDownloadCape(
                    id,
                    url,
                    loc,
                    CheatBreaker.asset("asset_loading.png")
            );
        }
        return loc;
    }

    public static class ThreadDownloadCape {
        public ThreadDownloadCape(String assetId, String imageUrl, Identifier loc, Identifier defaultLocation) {
            try {
                InputStream defaultStream = Minecraft.getInstance().getResourceManager().open(defaultLocation);
                Minecraft.getInstance().execute(() -> {
                    try {
                        Minecraft.getInstance().getTextureManager().register(loc, new DynamicTexture(() -> assetId + " Asset", NativeImage.read(defaultStream)));
                    } catch (Exception e) {
                        CheatBreaker.LOGGER.info("Error loading default asset: " + e.toString());
                    }
                });

                Thread t = new Thread(() -> {
                    try (InputStream imageStream = URI.create(imageUrl).toURL().openStream()) {
                        NativeImage image = NativeImage.read(imageStream);
                        Minecraft.getInstance().execute(() -> Minecraft.getInstance().getTextureManager().register(loc, new DynamicTexture(() -> assetId + " Asset", image)));
                    } catch (Exception e) {
                        CheatBreaker.LOGGER.info("Error loading asset: " + imageUrl + " - " + e);
                    }
                });
                t.setDaemon(true);
                t.start();
            } catch (Exception e) {
                CheatBreaker.LOGGER.info("Error starting asset download thread for: " + imageUrl + " - " + e);
            }
        }
    }

}
