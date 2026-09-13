package cc.vops.cheatbreaker.client.util.cosmetic.keyframe;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Keyframe {
    public float time;
    public Float xRot;
    public Float yRot;
    public Float zRot;
    public String easing;
    public Float offsetX;
    public Float offsetY;
    public Float offsetZ;

    public Keyframe(float time, Float xRot, Float yRot, Float zRot, String easing) {
        this.time = time;
        this.xRot = xRot;
        this.yRot = yRot;
        this.zRot = zRot;
        this.easing = easing != null ? easing : "linear";
    }
}