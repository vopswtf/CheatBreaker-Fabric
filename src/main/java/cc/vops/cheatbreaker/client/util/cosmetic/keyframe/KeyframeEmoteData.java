package cc.vops.cheatbreaker.client.util.cosmetic.keyframe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class KeyframeEmoteData {
    public String name = "Empty";
    public long duration = 1000;
    public Map<String, BoneAnimation> bones;

    public KeyframeEmoteData() {
        this.bones = new HashMap<>();
    }

    public static KeyframeEmoteData get(Identifier identifier) {
        try {
            InputStream is = Minecraft.getInstance().getResourceManager().open(identifier);
            String jsonString = new String(is.readAllBytes());
            return fromJSON(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
            return new KeyframeEmoteData();
        }
    }

    private static KeyframeEmoteData fromJSON(String jsonString) throws IOException {
        JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();

        KeyframeEmoteData data = new KeyframeEmoteData();
        data.name = json.get("name").getAsString();
        data.duration = json.get("duration").getAsLong();

        JsonObject bonesJson = json.getAsJsonObject("bones");
        for (String boneName : bonesJson.keySet()) {
            BoneAnimation boneAnim = new BoneAnimation(boneName);
            JsonArray keyframesJson = bonesJson.getAsJsonObject(boneName).getAsJsonArray("keyframes");

            for (JsonElement kfElement : keyframesJson) {
                JsonObject kfJson = kfElement.getAsJsonObject();
                Keyframe kf = new Keyframe();
                kf.time = kfJson.get("time").getAsFloat();
                kf.easing = kfJson.has("easing") ? kfJson.get("easing").getAsString() : "linear";

                if (kfJson.has("xRot")) kf.xRot = kfJson.get("xRot").getAsFloat();
                if (kfJson.has("yRot")) kf.yRot = kfJson.get("yRot").getAsFloat();
                if (kfJson.has("zRot")) kf.zRot = kfJson.get("zRot").getAsFloat();
                if (kfJson.has("offsetX")) kf.offsetX = kfJson.get("offsetX").getAsFloat();
                if (kfJson.has("offsetY")) kf.offsetY = kfJson.get("offsetY").getAsFloat();
                if (kfJson.has("offsetZ")) kf.offsetZ = kfJson.get("offsetZ").getAsFloat();

                boneAnim.keyframes.add(kf);
            }

            data.bones.put(boneName, boneAnim);
        }

        return data;
    }
}
