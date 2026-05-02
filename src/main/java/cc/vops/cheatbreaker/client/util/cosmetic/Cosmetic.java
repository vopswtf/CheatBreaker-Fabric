package cc.vops.cheatbreaker.client.util.cosmetic;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.util.AssetDownloader;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.resources.Identifier;

@Getter @Setter
public class Cosmetic {
    private String playerId;
    private String name;

    private CosmeticType type;

    private float scale;

    private int emoteId;

    private long lastUpdate;

    private boolean equipped;

    private Identifier location;
    private Identifier previewLocation;

    public Cosmetic(long time, String playerId, String name, CosmeticType type, float scale, boolean equipped, String location) {
        this.lastUpdate = time;
        this.playerId = playerId;
        this.name = name;
        this.type = type;
        this.scale = scale;
        this.equipped = equipped;
        if (location.startsWith("https://") || location.startsWith("http://")) {
            String id = name.toLowerCase().replaceAll("[^a-z0-9]", "_");
            this.location = AssetDownloader.getAsset(type, id, location);
            this.previewLocation = AssetDownloader.getAsset(type, id + "_preview", location.contains("?") ? location + "&preview=true" : location + "?preview=true");
        } else {
            this.location = CheatBreaker.asset(location);
            this.previewLocation = CheatBreaker.asset("preview/" + location);
        }

//        System.out.println("Cosmetic created: " + name + " for player " + playerId + " at " + location);
    }

    public Cosmetic(String playerId, String name, CosmeticType type, float scale, boolean equipped, String location) {
        this.playerId = playerId;
        this.name = name;
        this.type = type;
        this.scale = scale;
        this.equipped = equipped;
        if (location.startsWith("https://")) {
            String id = name.toLowerCase().replaceAll("[^a-z0-9]", "_");
            this.location = AssetDownloader.getAsset(type, id, location);
            this.previewLocation = AssetDownloader.getAsset(type, id + "_preview", location.contains("?") ? location + "&preview=true" : location + "?preview=true");
        } else {
            this.location = CheatBreaker.asset(location);
            this.previewLocation = CheatBreaker.asset("preview/" + location);
        }

//        System.out.println("Cosmetic created: " + name + " for player " + playerId + " at " + location);
    }

    public Cosmetic(String playerId, int emoteId, CosmeticType type) {
        this.playerId = playerId;
        this.emoteId = emoteId;
        this.type = type;

//        System.out.println("Emote Cosmetic created: " + emoteId + " for player " + playerId);
    }

    public enum CosmeticType {
        WINGS("dragon_wings"),
        CAPE("cape"),
        EMOTE("emote");

        @Getter
        private final String typeName;
        CosmeticType(String typeName) {
            this.typeName = typeName;
        }

        public static CosmeticType get(String name) {
            for (Cosmetic.CosmeticType cosmetic : CosmeticType.values()) {
                if (cosmetic.getTypeName().equals(name)) {
                    return cosmetic;
                }
            }
            return null;
        }
    }

}
