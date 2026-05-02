package cc.vops.cheatbreaker.client.ui.overlay.element;


import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.mainmenu.AbstractElement;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import cc.vops.cheatbreaker.client.util.dash.DashUtil;
import cc.vops.cheatbreaker.client.util.dash.Station;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public class RadioStationElement extends AbstractElement {
    private final Station station;
    private final Identifier starIcon = CheatBreaker.asset("icons/star-21.png");
    private final Identifier startFilledIcon = CheatBreaker.asset("icons/star-filled-21.png");
    private final RadioElement parent;

    public RadioStationElement(RadioElement parent, Station station) {
        this.parent = parent;
        this.station = station;
    }

    @Override
    protected void handleElementDraw(GuiGraphicsExtractor gfx, float f, float f2, boolean bl) {
        if (this.isMouseInsideElement(f, f2) && bl) {
            RenderUtil.drawRect(gfx, this.x, this.y, this.x + (float)22, this.y + this.height, -13158601);
        } else if (this.isMouseInside(f, f2) && bl) {
            RenderUtil.drawRect(gfx, this.x, this.y, this.x + this.width, this.y + this.height, -13158601);
        }

        boolean isFavourite = this.station.isFavourite();
        int color;
        if (isFavourite) {
            color = CheatBreaker.getColor(0.95f, 0.72f, 0.15f, 1.0f);
        } else {
            color = CheatBreaker.getColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
        boolean bl3 = CheatBreaker.getInstance().getRadioManager().getCurrentStation() == this.station;
        RenderUtil.drawIcon(gfx, isFavourite ? this.startFilledIcon : this.starIcon, (float)5, this.x + (float)7, this.y + (float)5, color);
        RenderUtil.drawString(gfx, Fonts.playRegular14, this.station.getName(), this.x + (float)24, this.y + 1.627451f * 2.390625f, bl3 ? -13369549 : -1);
        RenderUtil.drawString(gfx, Fonts.playRegular12, this.station.getGenre(), this.x + (float)24, this.y + 2.375f * 5.0f, -1342177281);
    }

    private boolean isMouseInsideElement(float f, float f2) {
        return this.isMouseInside(f, f2) && f < this.x + (float)22;
    }

    @Override
    public boolean handleElementMouseClicked(float f, float f2, int n, boolean bl) {
        if (!bl) {
            return false;
        }
        if (this.isMouseInsideElement(f, f2) && bl) {
            this.station.setFavourite(!this.station.isFavourite());
            this.parent.updateElementSize();
            return true;
        }
        if (this.isMouseInside(f, f2) && bl) {
            if (station.isPlay()) {
                DashUtil.end();
            }

            CheatBreaker.getInstance().getRadioManager().getDashQueueThread().offerStation(this.station);
            CheatBreaker.getInstance().getRadioManager().setStation(this.station);
        }
        return false;
    }

    public Station getStation() {
        return this.station;
    }
}
