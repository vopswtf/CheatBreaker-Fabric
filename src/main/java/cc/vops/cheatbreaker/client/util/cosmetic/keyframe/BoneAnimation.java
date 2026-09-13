package cc.vops.cheatbreaker.client.util.cosmetic.keyframe;

import java.util.ArrayList;
import java.util.List;

public class BoneAnimation {
    public String boneName;
    public List<Keyframe> keyframes;

    public BoneAnimation() {
        this.keyframes = new ArrayList<>();
    }

    public BoneAnimation(String boneName) {
        this();
        this.boneName = boneName;
    }

    public Keyframe[] getSurroundingKeyframes(float currentTime) {
        Keyframe before = null;
        Keyframe after = null;

        for (Keyframe kf : keyframes) {
            if (kf.time <= currentTime) {
                before = kf;
            }
            if (kf.time >= currentTime && after == null) {
                after = kf;
            }
        }

        return new Keyframe[]{before, after};
    }

    public RotationValues interpolate(float currentTime) {
        Keyframe[] surrounding = getSurroundingKeyframes(currentTime);
        Keyframe before = surrounding[0];
        Keyframe after = surrounding[1];

        if (before == null && after == null) return new RotationValues();
        if (before == null) before = after;
        if (after == null) after = before;

        if (before == after) {
            return new RotationValues(before);
        }

        float timeDiff = after.time - before.time;
        float progress = (currentTime - before.time) / timeDiff;

        float easedProgress = EasingFunctions.applyEasing(progress, after.easing);
        return RotationValues.interpolate(before, after, easedProgress);
    }

    public static class RotationValues {
        public Float xRot;
        public Float yRot;
        public Float zRot;
        public Float offsetX;
        public Float offsetY;
        public Float offsetZ;

        public RotationValues() {}

        public RotationValues(Keyframe kf) {
            this.xRot = kf.xRot;
            this.yRot = kf.yRot;
            this.zRot = kf.zRot;
            this.offsetX = kf.offsetX;
            this.offsetY = kf.offsetY;
            this.offsetZ = kf.offsetZ;
        }

        public static RotationValues interpolate(Keyframe from, Keyframe to, float progress) {
            RotationValues result = new RotationValues();

            result.xRot = lerp(from.xRot, to.xRot, progress);
            result.yRot = lerp(from.yRot, to.yRot, progress);
            result.zRot = lerp(from.zRot, to.zRot, progress);
            result.offsetX = lerp(from.offsetX, to.offsetX, progress);
            result.offsetY = lerp(from.offsetY, to.offsetY, progress);
            result.offsetZ = lerp(from.offsetZ, to.offsetZ, progress);

            return result;
        }

        private static Float lerp(Float from, Float to, float progress) {
            if (from == null && to == null) return null;
            if (from == null) return to * progress;
            if (to == null) return from * (1 - progress);
            return from + (to - from) * progress;
        }
    }
}