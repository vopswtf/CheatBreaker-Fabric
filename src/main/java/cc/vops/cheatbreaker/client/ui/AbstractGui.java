package cc.vops.cheatbreaker.client.ui;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.event.type.MenuDrawEvent;
import cc.vops.cheatbreaker.client.event.type.RenderPreviewEvent;
import cc.vops.cheatbreaker.client.ui.mainmenu.AbstractElement;
import cc.vops.cheatbreaker.client.ui.module.CBModulePlaceGui;
import cc.vops.cheatbreaker.client.ui.module.CBModulesGui;
import cc.vops.cheatbreaker.client.ui.module.CBProfileCreateGui;
import cc.vops.cheatbreaker.client.ui.overlay.Alert;
import cc.vops.cheatbreaker.client.util.Matrix3x2fStackDebug;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import cc.vops.cheatbreaker.mixin.debug.Matrix3x2fStackAccessor;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public abstract class AbstractGui extends Screen {

    protected Minecraft mc;
    protected List<AbstractElement> elements = new ArrayList<>();
    protected int scaledWidth, scaledHeight;
    protected int elementListSize = 0;
    protected int pendingScrollDelta = 0;
    protected boolean drawBackground = false;

    public AbstractGui() {
        super(Component.empty());
        this.mc = Minecraft.getInstance();
    }

    @Override
    protected void init() {
        updateScale();
        initMenu();
    }

    protected abstract void initMenu();

    private void updateScale() {
        float scale = CheatBreaker.getScaleFactor();
        this.scaledWidth = (int) (this.width / scale);
        this.scaledHeight = (int) (this.height / scale);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        updateScale();
        initMenu();
    }

    @Override
    public void extractTransparentBackground(GuiGraphicsExtractor GuiGraphicsExtractor) {
        // nope
    }

    @Override
    public void extractMenuBackground(GuiGraphicsExtractor gfx) {
        if (drawBackground) {
            super.extractMenuBackground(gfx);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor gfx, int mouseX, int mouseY, float delta) {
        if (this instanceof CBModulesGui || this instanceof CBModulePlaceGui || this instanceof CBProfileCreateGui) {
            CheatBreaker.getInstance().getEventBus().callEvent(new RenderPreviewEvent(gfx, null));
        }

        float scale = CheatBreaker.getScaleFactor();

        int before = ((Matrix3x2fStackAccessor) gfx.pose()).getCurr();

        gfx.pose().pushMatrix();
        gfx.pose().scale(scale, scale);

        if (this.pendingScrollDelta != 0) {
            onScroll(gfx, this.pendingScrollDelta);
        }

        drawMenu(gfx, mouseX / scale, mouseY / scale, delta);
        this.pendingScrollDelta = 0;

        gfx.pose().popMatrix();

        int after = ((Matrix3x2fStackAccessor) gfx.pose()).getCurr();
        if (before != after) {
            Matrix3x2fStackDebug debug = (Matrix3x2fStackDebug) gfx.pose();
            CheatBreaker.LOGGER.error("Matrix stack leak detected in class {}! Before: {}, After: {}", this.getClass().getName(), before, after);
            for (int i = before; i < after; i++) {
                StackTraceElement leak = debug.cb$getPushOrigin(i);

//                CheatBreaker.LOGGER.error(
//                        "Matrix leak at depth {}: {}.{}:{}",
//                        i,
//                        leak.getClassName(),
//                        leak.getMethodName(),
//                        leak.getLineNumber()
//                );
            }

            // auto-fix
            while (((Matrix3x2fStackAccessor) gfx.pose()).getCurr() > before) {
                gfx.pose().popMatrix();
            }
        }


        CheatBreaker.getInstance().getEventBus().callEvent(new MenuDrawEvent(gfx));
    }

    public void onScroll(GuiGraphicsExtractor gui, int delta) {

    }

    protected abstract void drawMenu(GuiGraphicsExtractor gfx, float mouseX, float mouseY, float delta);

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        double mouseX = mouseButtonEvent.x();
        double mouseY = mouseButtonEvent.y();
        int button = mouseButtonEvent.button();

        float scale = CheatBreaker.getScaleFactor();
        if (onMouseClicked(mouseX / scale, mouseY / scale, button)) return true;

        return super.mouseClicked(mouseButtonEvent, bl);
    }

    protected abstract boolean onMouseClicked(double mx, double my, int button);

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        float scale = CheatBreaker.getScaleFactor();
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();

        onMouseReleased(mouseX / scale, mouseY / scale, button);
        return super.mouseReleased(event);
    }

    public boolean doBlur() {
        return CheatBreaker.getInstance().getGlobalSettings().guiBlur.getAsBoolean();
    }

    protected void onMouseClicked(float mx, float my, int button, AbstractElement... ignore) {
        List<AbstractElement> ignoreList = Arrays.asList(ignore);

        AbstractElement clickedBringToFront = null;

        // Iterate from top-most to bottom-most
        for (int i = elements.size() - 1; i >= 0; i--) {
            AbstractElement element = elements.get(i);

            // Skip ignored elements
            if (ignoreList.contains(element))
                continue;

            // Skip if mouse not inside
            if (!element.isMouseInside(mx, my))
                continue;

            // Element is under mouse: try clicking
            boolean consumed = element.handleElementMouseClicked(mx, my, button, this.mouseClicked(element, mx, my, ignore));

            // Bring this element to front if pressed (like old cheatbreaker)
            clickedBringToFront = element;

            if (consumed && elements.contains(clickedBringToFront)) {
                elements.remove(clickedBringToFront);
                elements.add(clickedBringToFront);
            }
        }

    }

    protected boolean mouseClicked(AbstractElement var1, float var2, float var3, AbstractElement ... var4) {
        AbstractElement var8;
        List<AbstractElement> var5 = Arrays.asList(var4);
        boolean var6 = true;
        for (int var7 = this.elements.size() - 1; var7 >= 0 && (var8 = this.elements.get(var7)) != var1; --var7) {
            if (var5.contains(var8) || !var8.isMouseInside(var2, var3)) continue;
            var6 = false;
            break;
        }
        return var6;
    }

    protected void setElementsAndUpdateSize(AbstractElement... elements) {
        this.elements = new ArrayList<>();
        this.elements.addAll(Arrays.asList(elements));
        this.elementListSize = this.elements.size();
    }

    public void addElements(AbstractElement... elements) {
        this.elements.addAll(Arrays.asList(elements));
        this.initMenu();
    }

    public void removeElements(AbstractElement... elements) {
        this.elements.removeAll(Arrays.asList(elements));
        this.initMenu();
    }

    public void onMouseScroll(double yDelta) {
        this.pendingScrollDelta += (int) yDelta;
    }

    protected abstract void onMouseReleased(double mx, double my, int button);

    @Override
    public void tick() {
        super.tick();
        updateElements();
    }

    protected void updateElements() {
        elements.forEach(AbstractElement::handleElementUpdate);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        elements.forEach(e -> e.handleElementKeyTyped(event.key(), event.scancode(), event.modifiers()));
        return super.keyPressed(event);
    }
    @Override
    public boolean charTyped(CharacterEvent p_425889_) {
        this.onKeyTyped(p_425889_.codepoint(), p_425889_.codepoint());
        return super.charTyped(p_425889_);
    }

    public void onKeyTyped(int codepoint, int modifiers) {
        this.elements.forEach(e -> e.handleCharInput((char) codepoint, modifiers));
    }

    @Override
    protected void extractBlurredBackground(GuiGraphicsExtractor gfx) {
        if (doBlur()) gfx.blurBeforeThisStratum();
    }

    protected void drawElements(GuiGraphicsExtractor gfx, float f, float f2, AbstractElement... elements) {
        List<AbstractElement> list = Arrays.asList(elements);
        for (AbstractElement element : this.elements) {
            if (list.contains(element)) continue;
            element.drawElement(gfx, f, f2, this.isMouseHovered(element, f, f2));
        }
    }

    protected boolean isMouseHovered(AbstractElement element, float f, float f2, AbstractElement... elements) {
        AbstractElement element2;
        List<AbstractElement> list = Arrays.asList(elements);
        boolean bl = true;
        for (int i = this.elements.size() - 1; i >= 0 && (element2 = this.elements.get(i)) != element; --i) {
            if (list.contains(element2) || !element2.isMouseInside(f, f2)) continue;
            bl = false;
            break;
        }
        return bl;
    }

    protected void drawIcon(GuiGraphicsExtractor gfx, Identifier texture, float size, float x, float y) {
        RenderUtil.drawIcon(gfx, texture, size, x, y);
    }

    protected void drawIcon(GuiGraphicsExtractor gfx, Identifier texture, float width, float height,  float x, float y) {
        RenderUtil.drawIcon(gfx, texture, width, height, x, y);
    }

    public boolean allowMovement() {
        return false;
    }
}
