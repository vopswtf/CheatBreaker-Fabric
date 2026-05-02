package cc.vops.cheatbreaker.client.ui.overlay.element;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.fading.MinMaxFade;
import cc.vops.cheatbreaker.client.ui.mainmenu.element.ScrollableElement;
import cc.vops.cheatbreaker.client.ui.overlay.SocialOverlayScreen;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.ImageDownloader;
import cc.vops.cheatbreaker.client.util.Mouse;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import cc.vops.cheatbreaker.client.util.dash.DashUtil;
import cc.vops.cheatbreaker.client.util.dash.Station;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class RadioElement extends DraggableElement {
    private final Identifier dashIcon = CheatBreaker.asset("dash-logo-54.png");
    private final Identifier playIcon = CheatBreaker.asset("icons/play-24.png");
    private final List<RadioStationElement> radioStationElements;
    private final MinMaxFade fade = new MinMaxFade(300L);
    private float newHeight;
    private boolean hovered;
    private final HorizontalSliderElement slider;
    private final ScrollableElement scrollableContainer;
    private final InputFieldElement filter;
    private final FlatButtonElement pin;

    public RadioElement() {
        this.slider = new HorizontalSliderElement(CheatBreaker.getInstance().getGlobalSettings().radioVolume);
        this.scrollableContainer = new ScrollableElement(this);
        this.filter = new InputFieldElement(Fonts.playRegular16, "Filter", -11842741, -11842741);
        this.pin = new FlatButtonElement((Boolean) this.client.getGlobalSettings().pinRadio.getValue() ? "Unpin" : "Pin");
        this.radioStationElements = new ArrayList<>();
        for (Station station : CheatBreaker.getInstance().getRadioManager().getStations()) {
            this.radioStationElements.add(new RadioStationElement(this, station));
        }
    }

    public void updateElementSize() {
        this.setElementSize(this.x, this.y, this.width, this.height);
    }

    private boolean isFilterMatch(RadioStationElement radioStationElement) {
        String filterText = this.filter.getText().toLowerCase();
        if (filterText.isEmpty()) {
            return true;
        }
        Station station = radioStationElement.getStation();
        return station.getName().toLowerCase().contains(filterText) ||
               (station.getArtist() != null && station.getArtist().toLowerCase().contains(filterText)) ||
               (station.getTitle() != null && station.getTitle().toLowerCase().contains(filterText));
    }

    @Override
    public void handleScroll(GuiGraphicsExtractor gfx, int delta) {
        this.scrollableContainer.handleScroll(gfx, delta);
    }

    @Override
    public void setElementSize(float x, float y, float width, float height) {
        super.setElementSize(x, y, width, height);
        if (this.newHeight == 0.0f) {
            this.newHeight = height;
        }
        this.radioStationElements.sort(Comparator.comparing((RadioStationElement elem) -> !elem.getStation().isFavourite()).thenComparing(elem -> elem.getStation().getName()));
        this.slider.setElementSize(x, y + this.newHeight, width, 8);
        this.filter.setElementSize(x, y + this.newHeight + (float)8, width - (float)30, 13);
        this.pin.setElementSize(x + width - (float)30, y + this.newHeight + (float)8, (float)30, 13);
        this.scrollableContainer.setElementSize(x + width - (float)5, y + this.newHeight + (float)21, (float)5, 99);
        int n = 0;
        for (RadioStationElement radioStationElement : this.radioStationElements) {
            if (!this.isFilterMatch(radioStationElement)) continue;
            float f5 = y + (float)20 + this.newHeight + (float)n;
            radioStationElement.setElementSize(x, f5, width - (float)5, 20);
            n += 20;
        }
        this.scrollableContainer.setScrollAmount(n);
    }

    @Override
    public void handleCharInput(char typedChar, int keyCode) {
        this.filter.handleCharInput(typedChar, keyCode);
    }


    public boolean IIIIllIlIIIllIlllIlllllIl(float f, float f2) {
        return f > this.x && f < this.x + this.width && f2 > this.y && f2 < this.y + this.newHeight;
    }

    @Override
    protected void handleElementDraw(GuiGraphicsExtractor gfx, float f, float f2, boolean bl) {
        if (!(Minecraft.getInstance().screen instanceof SocialOverlayScreen) && !this.client.getGlobalSettings().pinRadio.getAsBoolean()) {
            return;
        }

        this.drag(f, f2);

        if (Mouse.isButtonDown(0) && slider.isMouseInside(f, f2)) {
            this.slider.handleElementMouseClicked(f, f2, 0, true);
        }

        RenderUtil.drawRect(gfx, this.x, this.y, this.x + this.width, this.y + this.newHeight, -14540254);
        Station station = CheatBreaker.getInstance().getRadioManager().getCurrentStation();
        if (station != null) {
            if (station.currentResource == null && !station.getName().isEmpty()) {
                String url = station.getCoverURL();
                if (url.isEmpty()) {
                    url = station.getLogoURL();
                }

                station.currentResource = ImageDownloader.getImage("station-" + station.getArtist() + "-" + station.getName(), url);
            }
//            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            Identifier Identifier = station.currentResource == null ? this.dashIcon : station.currentResource;
            RenderUtil.drawIcon(gfx, Identifier, this.newHeight / 2.0f, this.x, this.y);
            float f3 = this.x + (float)50;
            if (this.mc.screen == SocialOverlayScreen.getInstance()) {
                boolean bl2 = this.isMouseInside(f, f2) && f > this.x + (float)34 && f < this.x + (float)44 && f2 < this.y + this.newHeight;
                if (!DashUtil.isPlayerNotNull()) {
//                    GL11.glColor4f(1.0f, 1.0f, 1.0f, bl2 ? 1.0f : 0.8f);
                    RenderUtil.drawIcon(gfx, this.playIcon, (float)6, this.x + (float)34, this.y + 7.5f, CheatBreaker.getColor(1.0f, 1.0f, 1.0f, bl2 ? 1.0f : 0.8f));
                } else {
                    // pause icon when??? jhalt
                    RenderUtil.drawRect(gfx, this.x + (float)36, this.y + (float)9, this.x + (float)38, this.y + this.newHeight - (float)11, bl2 ? -1 : -1342177281);
                    RenderUtil.drawRect(gfx, this.x + (float)40, this.y + (float)9, this.x + (float)42, this.y + this.newHeight - (float)11, bl2 ? -1 : -1342177281);
                }
            } else {
                f3 = this.x + (float)34;
            }
            String string = station.getTitle();
            String under = station.getArtist();

            if (string == null || string.isEmpty()) {
                string = station.getName();
            }

            if (under == null || under.isEmpty()) {
                under = "";
            }

            RenderUtil.drawString(gfx, Fonts.playRegular16, sliceString(string, 125), f3, this.y + (float)4, -1);
            RenderUtil.drawString(gfx, Fonts.playRegular12, sliceString(under, 125), f3, this.y + (float)17, -1342177281);
        }
        float f4 = this.fade.inOutFade(this.isMouseInside(f, f2) && bl);
        if (this.fade.isFadeOngoing()) {
            this.setElementSize(this.x, this.y, this.width, this.newHeight + (float)120 * f4);
            this.hovered = true;
        } else if (!this.fade.isFadeOngoing() && !this.isMouseInside(f, f2)) {
            this.hovered = false;
        }

        if (this.hovered) {
//            GL11.glPushMatrix();
//            GL11.glEnable(3089);
            gfx.pose().pushMatrix();

            gfx.enableScissor((int)this.x, (int)(this.y + this.newHeight), (int)(this.x + this.width) + 2, (int)(this.y + this.newHeight + (this.height - this.newHeight) * f4));
            RenderUtil.drawRect(gfx, this.x, this.y + this.newHeight, this.x + this.width, this.y + this.height, -14540254);
            this.scrollableContainer.drawScrollable(gfx, f, f2, bl);
            for (RadioStationElement radioStationElement : this.radioStationElements) {
                if (!this.isFilterMatch(radioStationElement)) continue;
                radioStationElement.handleElementDraw(gfx, f, f2 - this.scrollableContainer.IllIIIIIIIlIlIllllIIllIII(), bl && !this.scrollableContainer.isDragClick() && !this.scrollableContainer.isMouseInside(f, f2));
            }
            gfx.pose().popMatrix();
            gfx.pose().popMatrix();
            this.scrollableContainer.handleElementDraw(gfx, f, f2, bl);

            if (this.mc.screen == SocialOverlayScreen.getInstance()) {
                this.filter.handleElementDraw(gfx, f, f2, bl);
                this.pin.handleElementDraw(gfx, f, f2, bl);
                this.slider.drawElement(gfx, f, f2, bl);
            }

            gfx.disableScissor();
        }
    }

    private String sliceString(String text, float maxWidth) {
        if (text == null || text.isEmpty()) return "";

        // Fast check
        if (Fonts.playRegular16.width(text) <= maxWidth) {
            return text;
        }

        int left = 0;
        int right = text.length();

        // Binary search for best fitting length
        while (left < right) {
            int mid = (left + right + 1) / 2;
            String substr = text.substring(0, mid);
            float width = Fonts.playRegular16.width(substr);

            if (width > maxWidth) {
                right = mid - 1;
            } else {
                left = mid;
            }
        }

        // add ellipsis if needed
        if (left < text.length()) {
            return text.substring(0, left - 3) + "...";
        } else {
            return text;
        }
    }

    @Override
    public void handleElementMouse() {
        this.scrollableContainer.handleElementMouse();
    }

    @Override
    public void handleElementUpdate() {
        this.filter.handleElementUpdate();
        this.pin.handleElementUpdate();
    }

    @Override
    public void handleElementClose() {
        this.filter.handleElementClose();
        this.pin.handleElementClose();
    }

    @Override
    public void handleElementKeyTyped(int keyCode, int scanCode, int modifiers) {
        this.filter.handleElementKeyTyped(keyCode, scanCode, modifiers);
        this.pin.handleElementKeyTyped(keyCode, scanCode, modifiers);
        this.scrollableContainer.handleElementKeyTyped(keyCode, scanCode, modifiers);
        if (this.filter.isFocused()) {
            this.updateElementSize();
        }
    }

    @Override
    public boolean handleMouseClickedInternal(float f, float f2, int n) {
        if (!this.filter.isMouseInside(f, f2) && this.filter.isFocused()) {
            this.filter.setFocused(false);
        }
        return false;
    }

    @Override
    public boolean handleElementMouseClicked(float f, float f2, int n, boolean bl) {
        this.filter.handleElementMouseClicked(f, f2, n, bl);
        if (this.filter.isFocused() && n == 1 && this.filter.getText().equals("")) {
            this.updateElementSize();
        }
        if (!bl) {
            return false;
        }
        boolean bl2 = this.isMouseInside(f, f2) && f > this.x + (float) 34 && f < this.x + (float) 44 && f2 < this.y + this.newHeight;
        if (bl2) {
            if (!DashUtil.isPlayerNotNull()) {
                CheatBreaker.getInstance().getRadioManager().getCurrentStation().playStream();
            } else {
                DashUtil.end();
            }
        }
        float f3 = this.fade.inOutFade(this.isMouseInside(f, f2));
        if (this.fade.isCurrentlyInverted()) {
            boolean bl4;
            this.slider.handleElementMouseClicked(f, f2, n, true);
            this.scrollableContainer.handleElementMouseClicked(f, f2, n, bl);
            this.filter.handleElementMouseClicked(f, f2, n, true);
            this.pin.handleElementMouseClicked(f, f2, n, true);
            boolean bl5 = bl4 = f > (float)((int)this.x) && f < (float)((int)(this.x + this.width)) && f2 > (float)((int)(this.y + this.newHeight + (float)21)) && f2 < (float)((int)(this.y + this.newHeight + (float)21 + (this.height - this.newHeight - (float)21) * f3));
            if (bl4) {
                RadioStationElement element;
                Iterator iterator = this.radioStationElements.iterator();
                while (!(!iterator.hasNext() || this.isFilterMatch(element = (RadioStationElement)iterator.next()) && element.handleElementMouseClicked(f, f2 - this.scrollableContainer.IllIIIIIIIlIlIllllIIllIII(), n, bl))) {
                }
            }
            if (this.pin.isMouseInside(f, f2)) {
                this.client.getGlobalSettings().pinRadio.setValue(!((Boolean) this.client.getGlobalSettings().pinRadio.getValue()));
                this.pin.setLabel((Boolean) this.client.getGlobalSettings().pinRadio.getValue() ? "Unpin" : "Pin");
            }
        }
        if (this.isMouseInside(f, f2) && f2 < this.y + this.newHeight && !bl2 && !this.slider.isMouseInside(f, f2) && !this.scrollableContainer.isMouseInside(f, f2)) {
            this.updateDraggingPosition(f, f2);
        }
        return super.handleElementMouseClicked(f, f2, n, bl);
    }
}

