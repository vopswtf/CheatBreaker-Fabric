package cc.vops.cheatbreaker.client.ui.overlay.element;

import cc.vops.cheatbreaker.client.ui.mainmenu.AbstractElement;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ElementListElement<T extends AbstractElement> extends AbstractElement {
    protected final List<T> elements = new CopyOnWriteArrayList<>();

    public ElementListElement(List<T> list) {
        this.elements.addAll(list);
    }

    @Override
    public void handleElementDraw(GuiGraphicsExtractor gfx, float f, float f2, boolean bl) {
        this.elements.forEach(element -> element.drawElement(gfx, f, f2, bl));
    }

    @Override
    public void handleElementClose() {
        this.elements.forEach(AbstractElement::handleElementClose);
    }

    @Override
    public void handleElementUpdate() {
        this.elements.forEach(AbstractElement::handleElementUpdate);
    }

    @Override
    public void handleElementKeyTyped(int keyCode, int scanCode, int modifiers) {
        this.elements.forEach(element -> element.handleElementKeyTyped(keyCode, scanCode, modifiers));
    }

    @Override
    public boolean handleElementMouseClicked(float f, float f2, int n, boolean bl) {
        if (!bl) {
            return false;
        }
        boolean bl2 = false;
        for (AbstractElement element : this.elements) {
            if (bl2) break;
            bl2 = element.handleElementMouseClicked(f, f2, n, bl);
        }
        return bl2;
    }

    @Override
    public boolean handleElementMouseRelease(float f, float f2, int n, boolean bl) {
        if (!bl) {
            return false;
        }
        boolean bl2 = false;
        for (AbstractElement element : this.elements) {
            if (bl2) break;
            bl2 = element.handleElementMouseRelease(f, f2, n, bl);
        }
        return bl2;
    }

    public List<T> getElements() {
        return this.elements;
    }
}

