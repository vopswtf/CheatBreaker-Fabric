package cc.vops.cheatbreaker.client.config;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Ported from CheatBreaker b302ec0/master
 * @author Decencies
 */
public class Setting {

    @Getter
    private final String label;
    @Getter
    private Object value;
    @Getter
    private Object defaultValue;
    @Getter
    private Number minimumValue;
    @Getter
    private Number maximumValue;
    @Getter
    private Setting parent;
    @Getter
    private String[] acceptedValues;
    private Consumer<Object> valueConsumer;
    private AbstractModule container;
    public boolean rainbow;
    public int[] colorArray;

    @Getter @Setter
    private boolean editableString = false;

    // binds
    @Getter @Setter private String displayName = "Unknown";
    @Getter private int keyCode;
    @Getter private boolean hasKeycode = false;
    @Getter @Setter private boolean allowMouseKeybinding = false;

    // slider delta for arrow keys
    @Getter private float delta = 1;

    public Setting(String label) {
        if (label.isEmpty()) throw new IllegalStateException("Label is empty.");
        this.label = label;
    }

    public Setting(AbstractModule container, String label) {
        this(label);
        this.container = container;
        container.getSettingsList().add(this);
    }

    public Setting(List<Setting> list, String label) {
        this(label);
        list.add(this);
    }

    public int getColorValue() {
        if (this.rainbow) {
            // strip the alpha channel from the color.
            int stripped = (Integer) this.value >> 24 & 0xFF;
            float hue = (float) System.nanoTime() / (1.0E10f) % 1.0f;
            return stripped << 24 | Color.HSBtoRGB(hue, 1.0f, 1.0f) & 0xFFFFFF;
        }
        return (Integer) this.value;
    }

    public Setting setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;

        if (this.container != null) {
            setValue(defaultValue, false);
        }

        return this;
    }

    public Setting setKeyCode(int keycode) {
        this.keyCode = keycode;
        this.hasKeycode = true;
        return this;
    }

    public Setting setParent(Setting parent) {
        if (parent.getType() != Type.BOOLEAN) {
            throw new IllegalStateException("Parent can only be boolean.");
        }
        this.parent = parent;
        return this;
    }

    public boolean getParentValue() {
        return this.parent != null && (Boolean) this.parent.getValue();
    }

    public boolean hasDefaultValue() {
        return this.defaultValue != null;
    }

    public Setting setValue(Object object) {
        return this.setValue(object, true);
    }

    public Setting setValue(Object object, boolean createProfile) {
        if (CheatBreaker.getInstance().getActiveProfile() != null && !CheatBreaker.getInstance().getActiveProfile().isEditable()) {
            if (createProfile) {
                CheatBreaker.getInstance().createNewProfile();
            }
        } else if (this.container != null) {
            this.container.getDefaultSettingsValues().add(object);
        }
        this.value = object;
        if (this.valueConsumer != null) {
            this.valueConsumer.accept(object);
        }
        return this;
    }

    public Setting acceptedValues(String... valueArray) {
        this.acceptedValues = valueArray;
        return this;
    }

    public Setting onChange(Consumer<Object> consumer) {
        this.valueConsumer = consumer;
        return this;
    }

    public Setting setMinMax(Number min, Number max) {
        this.minimumValue = min;
        this.maximumValue = max;
        return this;
    }

    public Setting setDelta(float delta) {
        this.delta = delta;
        return this;
    }

    public Type getType() {
        if (Boolean.class.isAssignableFrom(this.value.getClass())) {
            return Type.BOOLEAN;
        }
        if (String.class.isAssignableFrom(this.value.getClass())) {
            if (this.acceptedValues == null || this.acceptedValues.length == 0) {
                return Type.STRING;
            }
            return Type.STRING_ARRAY;
        }
        if (Float.class.isAssignableFrom(this.value.getClass())) {
            return Type.FLOAT;
        }
        if (Double.class.isAssignableFrom(this.value.getClass())) {
            return Type.DOUBLE;
        }
        if (String[].class.isAssignableFrom(this.value.getClass())) {
            return Type.STRING_ARRAY;
        }
        if (Integer.class.isAssignableFrom(this.value.getClass())) {
            return Type.INTEGER;
        }
        return null;
    }

    public Boolean getAsBoolean() {
        return (Boolean) this.value;
    }

    public String getAsString() {
        return (String) this.value;
    }

    public Integer getAsInteger() {
        return (Integer) this.value;
    }

    public Float getAsFloat() {
        return (Float) this.value;
    }


    public Double getAsDouble() {
        return (Double) this.value;
    }

    public String[] getAsStringArray() {
        return (String[]) this.value;
    }

    public enum Type {
        STRING,
        STRING_ARRAY,
        FLOAT,
        INTEGER,
        DOUBLE,
        BOOLEAN
    }
}
