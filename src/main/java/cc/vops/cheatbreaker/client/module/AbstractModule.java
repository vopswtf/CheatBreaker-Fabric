package cc.vops.cheatbreaker.client.module;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.event.EventBus;
import cc.vops.cheatbreaker.client.module.type.ScoreboardModule;
import cc.vops.cheatbreaker.client.module.type.ToggleSprintModule;
import cc.vops.cheatbreaker.client.ui.AbstractGui;
import cc.vops.cheatbreaker.client.ui.module.CBAnchorHelper;
import cc.vops.cheatbreaker.client.ui.module.GuiAnchor;
import cc.vops.cheatbreaker.client.ui.module.CBPositionEnum;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Getter @Setter
public abstract class AbstractModule {

    // Decompiler left this in it's own class.
    // I assume it was an inner class.
    public enum PreviewType {
        LABEL,
        ICON
    }

    private final String name;
    private final List<Object> defaultSettingsValues;
    private final Map<Class<? extends EventBus.Event>, Consumer> eventMap;
    public boolean defaultState = false;
    public GuiAnchor defaultGuiAnchor;
    public float defaultXTranslation = 0.0f;
    public float defaultYTranslation = 0.0f;
    public boolean defaultRenderHud = true;
    public float width = 0.0f;
    public float height = 0.0f;
    public boolean isEditable = true;
    public Setting scale;
    protected Minecraft minecraft;
    private boolean staffModule = false;
    private boolean staffModuleEnabled = false;
    private boolean enabled = false;
    private GuiAnchor guiAnchor;
    private float xTranslation = 0.0f;
    private float yTranslation = 0.0f;
    private boolean renderHud = true;
    private List<Setting> settingsList;
    private PreviewType previewType;
    private Identifier previewIconLocation;
    private float previewIconWidth;
    private float previewIconHeight;
    private float previewLabelSize;
    private String previewLabel;

    public AbstractModule(String string) {
        this.name = string;
        this.eventMap = new HashMap<>();
        this.minecraft = Minecraft.getInstance();
        this.settingsList = new ArrayList<>();
        this.defaultSettingsValues = new ArrayList<>();
        this.scale = new Setting(this, "Scale").setValue(1.0f).setMinMax(1.0126582f * 0.49375f, 0.55f * 2.7272727f);
    }

    protected <T extends EventBus.Event> void addEvent(Class<T> eventClass, Consumer<T> consumer) {
        this.eventMap.put(eventClass, consumer);
    }

    protected void addAllEvents() {
        for (Map.Entry<Class<? extends EventBus.Event>, Consumer> entry : this.eventMap.entrySet()) {
            CheatBreaker.getInstance().getEventBus().addEvent(entry.getKey(), entry.getValue());
        }
    }

    protected void removeAllEvents() {
        for (Map.Entry<Class<? extends EventBus.Event>, Consumer> entry : this.eventMap.entrySet()) {
            CheatBreaker.getInstance().getEventBus().removeEvent(entry.getKey(), entry.getValue());
        }
    }

    public void setState(boolean state) {
        if (state != this.defaultState) {
            CheatBreaker.getInstance().createNewProfile();
        }
        if (state) {
            if (!this.enabled) {
                this.enabled = true;
                this.addAllEvents();
            }
        } else if (this.enabled) {
            this.enabled = false;
            this.removeAllEvents();
        }
    }

    public void setDefaultState(boolean state) {
        if (state) {
            if (!this.enabled) {
                this.enabled = true;
                this.addAllEvents();
            }
        } else if (this.enabled) {
            this.enabled = false;
            this.removeAllEvents();
        }
        this.defaultState = this.enabled;
    }

    public void setTranslations(float x, float y) {
        this.xTranslation = x;
        this.yTranslation = y;
    }

    public void setDefaultTranslations(float x, float y) {
        this.xTranslation = x;
        this.yTranslation = y;
        this.defaultXTranslation = x;
        this.defaultYTranslation = y;
    }

    public void setDimensions(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public boolean isStaffEnabledModule() {
        return this.staffModuleEnabled;
    }

    public void setStaffModuleEnabled(boolean bl) {
        this.staffModuleEnabled = bl;
        if (!bl && this.isEnabled()) {
            this.setState(false);
        }
    }

    public void scaleAndTranslate(GuiGraphicsExtractor gfx) {
        this.scaleAndTranslate(gfx, this.width, this.height);
    }

    public void scaleAndTranslate(GuiGraphicsExtractor gfx, float width, float height) {
        float scale = this.masterScale() - 0.01f;

        double scaleHeight = gfx.guiHeight() / CheatBreaker.getScaleFactor();
        double scaleWidth = gfx.guiWidth() / CheatBreaker.getScaleFactor();

        gfx.pose().scale(scale, scale);
        width *= scale;
        height *= scale;

        float x = 0.0f;
        float y = 0.0f;

        // Use unscaled width/height for anchor calculations
        switch (this.guiAnchor) {
            case LEFT_TOP -> {
                x = 2.0f;
                y = 2.0f;
            }
            case LEFT_MIDDLE -> {
                x = 2.0f;
                y = (float) (scaleHeight / 2 - height / 2.0f);
            }
            case LEFT_BOTTOM -> {
                x = 2.0f;
                y = (float) (scaleHeight - height - 2.0f);
            }
            case MIDDLE_TOP -> {
                x = (float) (scaleWidth / 2 - width / 2.0f);
                y = 2.0f;
            }
            case MIDDLE_MIDDLE -> {
                x = (float) (scaleWidth / 2 - width / 2.0f);
                y = (float) (scaleHeight / 2 - height / 2.0f);
            }
            case MIDDLE_BOTTOM_LEFT -> {
                x = (float) (scaleWidth / 2 - width);
                y = (float) (scaleHeight - height - 2.0f);
            }
            case MIDDLE_BOTTOM_RIGHT -> {
                x = (float) (scaleWidth / 2);
                y = (float) (scaleHeight - height - 2.0f);
            }
            case RIGHT_TOP -> {
                x = (float) (scaleWidth - width - 2.0f);
                y = 2.0f;
            }
            case RIGHT_MIDDLE -> {
                x = (float) (scaleWidth - width);
                y = (float) (scaleHeight / 2 - height / 2.0f);
            }
            case RIGHT_BOTTOM -> {
                x = (float) (scaleWidth - width);
                y = (float) (scaleHeight - height);
            }
        }

        gfx.pose().translate(x / scale, y / scale);
        gfx.pose().translate(this.xTranslation / scale, this.yTranslation / scale);
    }


    public float[] getScaledPoints(boolean bl) {
        float f = 0.0f;
        float f2 = 0.0f;
        float scale = this.masterScale();
        float f3 = this.width * scale;
        float f4 = this.height * scale;

        double scaleHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight() / CheatBreaker.getScaleFactor();
        double scaleWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth() / CheatBreaker.getScaleFactor();

        switch (this.guiAnchor) {
            case LEFT_TOP: {
                f = 2.0f;
                f2 = 2.0f;
                break;
            }
            case LEFT_MIDDLE: {
                f = 2.0f;
                f2 = (float) (scaleHeight / 2) - f4 / 2.0f;
                break;
            }
            case LEFT_BOTTOM: {
                f2 = (float) scaleHeight - f4 - 2.0f;
                f = 2.0f;
                break;
            }
            case MIDDLE_TOP: {
                f = (float) (scaleWidth / 2) - f3 / 2.0f;
                f2 = 2.0f;
                break;
            }
            case MIDDLE_MIDDLE: {
                f = (float) (scaleWidth / 2) - f3 / 2.0f;
                f2 = (float) (scaleHeight / 2) - f4 / 2.0f;
                break;
            }
            case MIDDLE_BOTTOM_LEFT: {
                f = (float) (scaleWidth / 2) - f3;
                f2 = (float) scaleHeight - f4 - 2.0f;
                break;
            }
            case MIDDLE_BOTTOM_RIGHT: {
                f = (float) scaleWidth / 2;
                f2 = (float) scaleHeight - f4 - 2.0f;
                break;
            }
            case RIGHT_TOP: {
                f = (float) scaleWidth - f3 - 2.0f;
                f2 = 2.0f;
                break;
            }
            case RIGHT_MIDDLE: {
                f = (float) scaleWidth - f3;
                f2 = (float) (scaleHeight / 2) - f4 / 2.0f;
                break;
            }
            case RIGHT_BOTTOM: {
                f = (float) scaleWidth - f3;
                f2 = (float) scaleHeight - f4;
            }
        }
        return new float[]{(f + (bl ? xTranslation : 0.0f)) / scale, (f2 + (bl ? yTranslation : 0.0f)) / scale};
    }

    public void setRenderHud(boolean renderHud) {
        if (renderHud != this.defaultRenderHud) {
            CheatBreaker.getInstance().createNewProfile();
        }
        this.renderHud = renderHud;
    }

    public void setAnchor(GuiAnchor cBGuiAnchor) {
        if (cBGuiAnchor != this.defaultGuiAnchor) {
            CheatBreaker.getInstance().createNewProfile();
        }
        this.guiAnchor = cBGuiAnchor;
    }

    public void setDefaultAnchor(GuiAnchor cBGuiAnchor) {
        this.guiAnchor = cBGuiAnchor;
        this.defaultGuiAnchor = cBGuiAnchor;
    }

    public CBPositionEnum getPosition() {
        return CBAnchorHelper.getHorizontalPositionEnum(this.guiAnchor);
    }

    protected void setPreviewIcon(Identifier location, int width, int height) {
        this.previewType = PreviewType.ICON;
        this.previewIconLocation = location;
        this.previewIconWidth = width;
        this.previewIconHeight = height;
    }

    protected void setPreviewLabel(String label, float size) {
        this.previewType = PreviewType.LABEL;
        this.previewLabel = label;
        this.previewLabelSize = size;
    }

    public Identifier getPreviewIcon() {
        return this.previewIconLocation;
    }

    public void setDefaultRenderHud(boolean bl) {
        this.renderHud = bl;
        this.defaultRenderHud = bl;
    }

    public float masterScale() {
        return (Float) this.scale.getValue() * (1f / CheatBreaker.getScaleFactor());
    }
}

