package cc.vops.cheatbreaker.client.util.cosmetic.keyframe;

public class EasingFunctions {
    public static float applyEasing(float progress, String easingName) {
        return switch (easingName.toLowerCase()) {
            case "easeinquad" -> easeInQuad(progress);
            case "easeoutquad" -> easeOutQuad(progress);
            case "easeinoutquad" -> easeInOutQuad(progress);
            case "easeincubic" -> easeInCubic(progress);
            case "easeoutcubic" -> easeOutCubic(progress);
            case "easeinoutcubic" -> easeInOutCubic(progress);
            case "cosinefade" -> easeCosineFade(progress);
            default -> linear(progress);
        };
    }

    public static float linear(float t) { return t; }

    public static float easeCosineFade(float t) {
        return (float)(1 - Math.cos(t * Math.PI / 2));
    }

    public static float easeInQuad(float t) { return t * t; }
    public static float easeOutQuad(float t) { return 1 - (1 - t) * (1 - t); }
    public static float easeInOutQuad(float t) { return t < 0.5f ? 2 * t * t : 1 - (float)Math.pow(-2 * t + 2, 2) / 2; }

    public static float easeInCubic(float t) { return t * t * t; }
    public static float easeOutCubic(float t) { return 1 - (float)Math.pow(1 - t, 3); }
    public static float easeInOutCubic(float t) { return t < 0.5f ? 4 * t * t * t : 1 - (float)Math.pow(-2 * t + 2, 3) / 2; }
}
