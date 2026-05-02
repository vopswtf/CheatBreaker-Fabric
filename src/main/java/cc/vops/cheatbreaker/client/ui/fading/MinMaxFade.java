package cc.vops.cheatbreaker.client.ui.fading;

public class MinMaxFade extends FloatFade {
    public MinMaxFade(long l) {
        super(l);
    }

    @Override
    protected float getValue() {
        float value = super.getValue();
        return (double) value < 0.2558139478148095 * 1.954545497894287 ? 2.0f * value * value : (float) -1 + ((float) 4 - 2.0f * value) * value;
    }
}

