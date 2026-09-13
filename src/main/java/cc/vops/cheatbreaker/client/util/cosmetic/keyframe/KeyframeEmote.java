package cc.vops.cheatbreaker.client.util.cosmetic.keyframe;

import cc.vops.cheatbreaker.client.ui.fading.FloatFade;
import cc.vops.cheatbreaker.client.util.cosmetic.Emote;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public abstract class KeyframeEmote extends Emote {
    protected KeyframeEmoteData keyframeData;
    protected Map<String, ModelPart> boneMap;

    public KeyframeEmote(KeyframeEmoteData keyframeEmoteData) {
        super(keyframeEmoteData.name, new FloatFade(keyframeEmoteData.duration));
        this.keyframeData = keyframeEmoteData;
        this.boneMap = new HashMap<>();
    }

    protected void mapBones(PlayerModel model) {
        boneMap.put("rightArm", model.rightArm);
        boneMap.put("leftArm", model.leftArm);
        boneMap.put("rightLeg", model.rightLeg);
        boneMap.put("leftLeg", model.leftLeg);
        boneMap.put("body", model.body);
        boneMap.put("head", model.head);
    }

    @Override
    public void tickEmote(Player player, PlayerModel model, AvatarRenderState state, float partialTicks) {
        if (boneMap.isEmpty()) {
            mapBones(model);
        }

        long elapsedTime = duration.getTimeElapsed();

        for (String boneName : keyframeData.bones.keySet()) {
            BoneAnimation boneAnim = keyframeData.bones.get(boneName);
            ModelPart bone = boneMap.get(boneName);

            if (bone != null && !boneAnim.keyframes.isEmpty()) {
                BoneAnimation.RotationValues values = boneAnim.interpolate(elapsedTime);
                applyRotations(bone, values);
            }
        }
    }

    private void applyRotations(ModelPart bone, BoneAnimation.RotationValues values) {
        if (values.xRot != null) {
            bone.xRot = (float) Math.toRadians(values.xRot);
        }
        if (values.yRot != null) {
            bone.yRot = (float) Math.toRadians(values.yRot);
        }
        if (values.zRot != null) {
            bone.zRot = (float) Math.toRadians(values.zRot);
        }
        if (values.offsetX != null || values.offsetY != null || values.offsetZ != null) {
            bone.offsetPos(new Vector3f(
                    values.offsetX != null ? values.offsetX : 0,
                    values.offsetY != null ? values.offsetY : 0,
                    values.offsetZ != null ? values.offsetZ : 0
            ));
        }
    }
}