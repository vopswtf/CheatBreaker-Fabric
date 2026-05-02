package cc.vops.cheatbreaker.client.module.type;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.event.type.GuiDrawEvent;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.ui.module.GuiAnchor;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.util.Mth;

public class CoordinatesModule extends AbstractModule {

    private final Setting mode;
    private final Setting showWhileTyping;
    private final Setting showCoordinates;
    private final Setting hideYCoordinate;
    private final Setting showDirection;
    private final Setting coordinatesColor;
    private final Setting customLine;
    private final Setting directionColor;

    public CoordinatesModule() {
        super("Coordinates");
        this.setDefaultAnchor(GuiAnchor.LEFT_TOP);
        this.setDefaultTranslations(0.0f, 0.0f);
        this.setDefaultState(false);
        new Setting(this, "label").setValue("General Options");
        this.showWhileTyping = new Setting(this, "Show While Typing").setValue(true);
        this.mode = new Setting(this, "Mode").setValue("Horizontal").acceptedValues("Horizontal", "Vertical");
        this.hideYCoordinate = new Setting(this, "Hide Y Coordinate").setValue(false);
        this.showCoordinates = new Setting(this, "Show Coordinates").setValue(true);
        this.showDirection = new Setting(this, "Direction").setValue(true);
        this.customLine = new Setting(this, "Custom Line").setValue("");
        new Setting(this, "label").setValue("Color Settings");
        this.coordinatesColor = new Setting(this, "Coordinates Color").setValue(-1).setMinMax(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.directionColor = new Setting(this, "Direction Color").setValue(-1).setMinMax(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.setPreviewLabel("(16, 65, 120) NW", 1.0f);
        this.addEvent(GuiDrawEvent.class, this::onRender);
    }

    public void onRender(GuiDrawEvent event) {
        if (!this.isRenderHud()) {
            return;
        }
        if (this.minecraft.player == null) {
            return;
        }


        GuiGraphicsExtractor gfx = event.getGraphics();
        gfx.pose().pushMatrix();

        gfx.pose().scale(CheatBreaker.getScaleFactor(), CheatBreaker.getScaleFactor());
        this.scaleAndTranslate(gfx);

        int n = Mth.floor(this.minecraft.player.position().x);
        int n2 = (int) this.minecraft.player.getBoundingBox().minY;
        int n3 = Mth.floor(this.minecraft.player.position().z);

        if (!(this.minecraft.screen instanceof ChatScreen) || ((Boolean) this.showWhileTyping.getValue())) {
            int n4;
            String object;
            float f = 4;
            if (this.mode.getValue().equals("Horizontal")) {
                object = (Boolean) this.hideYCoordinate.getValue() ? (Boolean) this.showCoordinates.getValue() ? String.format("(%1$d, %2$d) ", n, n3) : "" : (Boolean) this.showCoordinates.getValue() ? String.format("(%1$d, %2$d, %3$d) ", n, n2, n3) : "";
//                n4 = this.minecraft.fontRenderer.drawStringWithShadow(object, 0, 0, this.coordinatesColor.getColorValue());
                RenderUtil.drawStringWithShadow(gfx, minecraft.font, object, 0, 0, this.coordinatesColor.getColorValue());
                n4 = minecraft.font.width(object);
            } else {
                n4 = 50;
                f = (Boolean) this.hideYCoordinate.getValue() ? 8.066038f * 1.1777778f : (float) 16;
                RenderUtil.drawStringWithShadow(gfx, minecraft.font, "X: " + n, 0, 0, this.coordinatesColor.getColorValue());
                if (!((Boolean) this.hideYCoordinate.getValue())) {
                    RenderUtil.drawStringWithShadow(gfx, minecraft.font, "Y: " + n2, 0, 12, this.coordinatesColor.getColorValue());
                }
                RenderUtil.drawStringWithShadow(gfx, minecraft.font, "Z: " + n3, 0, (Boolean) this.hideYCoordinate.getValue() ? 12 : 24, this.coordinatesColor.getColorValue());
            }
            if (((Boolean)this.showDirection.getValue())) {
                String[] directions = new String[]{"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
                double d = Mth.wrapDegrees(this.minecraft.player.yHeadRot) + (double)180;
                d += 11.682692039868188 * (double)1.925926f;
                d %= 360;
                String string = directions[Mth.floor(d /= (double)45)];
                RenderUtil.drawStringWithShadow(gfx, minecraft.font, string, n4, (int) f - 4, this.directionColor.getColorValue());
                n4 += minecraft.font.width(string);
            }
            this.setDimensions(n4, (float) 18 + f);
        }

        gfx.pose().popMatrix();
    }

}
