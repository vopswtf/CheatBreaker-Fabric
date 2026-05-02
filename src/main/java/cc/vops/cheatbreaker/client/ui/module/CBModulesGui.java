package cc.vops.cheatbreaker.client.ui.module;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.GlobalSettings;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.ui.AbstractGui;
import cc.vops.cheatbreaker.client.ui.element.AbstractScrollableElement;
import cc.vops.cheatbreaker.client.ui.element.module.ModuleListElement;
import cc.vops.cheatbreaker.client.ui.element.module.ModulePreviewContainer;
import cc.vops.cheatbreaker.client.ui.element.module.ModulesGuiButtonElement;
import cc.vops.cheatbreaker.client.ui.element.profile.ProfilesListElement;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.Keyboard;
import cc.vops.cheatbreaker.client.util.Mouse;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CBModulesGui extends AbstractGui {
    public static CBModulesGui instance;
    private final Identifier cogIcon = CheatBreaker.asset("icons/cog-64.png");
    private final Identifier deleteIcon = CheatBreaker.asset("icons/delete-64.png");
    private final List<CBModulePosition> positions = new ArrayList<>();
    private final List<AbstractScrollableElement> elementList = new ArrayList<>();
    private final List<ModulesGuiButtonElement> buttons = new ArrayList<>();

    private static AbstractModule draggingModule;

    private List<AbstractModule> modules;
    private float animationPhase = 0;

    public AbstractScrollableElement profilesElement;
    public AbstractScrollableElement modulesElement;
    public AbstractScrollableElement settingsElement;
    public AbstractScrollableElement focusedElement = null;

    public AbstractScrollableElement currentScrollableElement = null;
    public static boolean allMenusClosed = false;

    public boolean IlIlIIIlllllIIIlIlIlIllII = false; // somethign with undoList
    public boolean showModSizeOutline = false;

    private float mouseX2;
    private float mouseY2;
    private ModuleDataHolder dataHolder;
    private List<ModuleActionData> undoList;
    private List<ModuleActionData> redo;

    private int mouseX;
    private int mouseY;
    private int arrowKeyMoves;

    @Override
    protected void initMenu() {
        instance = this;
        animationPhase = 5;
        this.mouseX = -1;
        this.mouseY = -1;

        float f = CheatBreaker.getScaleFactor();
        int n = this.scaledWidth;
        int n2 = this.scaledHeight;

        draggingModule = null;
        allMenusClosed = false;
        this.undoList = new ArrayList<>();
        this.redo = new ArrayList<>();
        this.arrowKeyMoves = 0;

        this.elementList.clear();
        this.buttons.clear();
        this.currentScrollableElement = null;

        this.modulesElement = new ModulePreviewContainer(f, n / 2 - 365 / 2, n2 / 2 + 14, 370, n2 / 2 - 35);
        this.elementList.add(this.modulesElement);

        this.modules = new ArrayList<>();
        this.modules.addAll(CheatBreaker.getInstance().getModuleManager().modules);
        this.modules.removeIf(module -> !module.isEditable);

        this.settingsElement = new ModuleListElement(modules, f, n / 2 - 365 / 2, n2 / 2 + 14, 370, n2 / 2 - 35);
        this.elementList.add(settingsElement);

        this.profilesElement = new ProfilesListElement(f, scaledWidth / 2 - 565, scaledHeight / 2 + 14, 370, scaledHeight / 2 - 35);
        this.elementList.add(this.profilesElement);

        this.buttons.add(new ModulesGuiButtonElement(this.modulesElement, "Mods", n / 2 - 50, n2 / 2 - 19, 100, 28, -13916106, f));
        this.buttons.add(new ModulesGuiButtonElement(this.settingsElement, "cog-64.png", n / 2 + 54, n2 / 2 - 19, 28, 28, -12418828, f));
        this.buttons.add(new ModulesGuiButtonElement(this.profilesElement, "profiles-64.png", n / 2 - 82, n2 / 2 - 19, 28, 28, -12418828, f));
    }

    @Override
    public void tick() {
        float f2 = this.animationPhase > 30 ? 2.0f + this.animationPhase / 2.0f : (float)4;
        this.animationPhase = this.animationPhase + f2 >= (float)255 ? 255 : (this.animationPhase + f2);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int n = event.key();
        char c = (char) n;

        if (n == GLFW.GLFW_KEY_ESCAPE || n == GlobalSettings.getKeyCode(CheatBreaker.getInstance().getGlobalSettings().openMenu)) {
            CheatBreaker.getInstance().getConfigManager().write();
        }

        if (n == GLFW.GLFW_KEY_Z && Keyboard.isCtrlKeyDown()) {
            if (!this.undoList.isEmpty()) {
                int n2 = this.undoList.size() - 1;
                ModuleActionData moduleActionData = this.undoList.get(this.undoList.size() - 1);
                for (int i = 0; i < moduleActionData.modules.size(); ++i) {
                    AbstractModule cBModule = moduleActionData.modules.get(i);
                    float f = moduleActionData.xTranslations.get(i);
                    float f2 = moduleActionData.yTranslations.get(i);
                    GuiAnchor cBGuiAnchor = moduleActionData.anchors.get(i);
                    Float f3 = moduleActionData.scales.get(i);
                    cBModule.setAnchor(cBGuiAnchor);
                    cBModule.setTranslations(f, f2);
                    cBModule.scale.setValue(f3);
                }
                if (this.redo.size() > 50) {
                    this.redo.remove(0);
                }
                this.redo.add(moduleActionData);
                this.undoList.remove(n2);
            }
        } else if (n == GLFW.GLFW_KEY_H && Keyboard.isCtrlKeyDown()) {
            if (!this.redo.isEmpty()) {
                int n3 = this.redo.size() - 1;
                ModuleActionData moduleActionData = this.redo.get(this.redo.size() - 1);
                for (int i = 0; i < moduleActionData.modules.size(); ++i) {
                    AbstractModule module = moduleActionData.modules.get(i);
                    float xTranslation = moduleActionData.xTranslations.get(i);
                    float yTranslation = moduleActionData.yTranslations.get(i);
                    GuiAnchor anchor = moduleActionData.anchors.get(i);
                    float scale = moduleActionData.scales.get(i);
                    module.setAnchor(anchor);
                    module.setTranslations(xTranslation, yTranslation);
                    module.scale.setValue(scale);
                }
                if (this.redo.size() > 50) {
                    this.redo.remove(0);
                }
                this.undoList.add(moduleActionData);
                this.redo.remove(n3);
            }
        } else {
            for (CBModulePosition CBModulePosition : this.positions) {
                AbstractModule cBModule = CBModulePosition.module;
                if (cBModule == null) continue;
                switch (n) {
                    case 203: {
                        cBModule.setTranslations(cBModule.getXTranslation() - 1.0f, cBModule.getYTranslation());
                        break;
                    }
                    case 205: {
                        cBModule.setTranslations(cBModule.getXTranslation() + 1.0f, cBModule.getYTranslation());
                        break;
                    }
                    case 200: {
                        cBModule.setTranslations(cBModule.getXTranslation(), cBModule.getYTranslation() - 1.0f);
                        break;
                    }
                    case 208: {
                        cBModule.setTranslations(cBModule.getXTranslation(), cBModule.getYTranslation() + 1.0f);
                    }
                }
            }
        }

        return super.keyPressed(event);
    }

    @Override
    protected void drawMenu(GuiGraphicsExtractor gfx, float mouseX, float mouseY, float delta) {
        if (this.pendingScrollDelta != 0 && this.focusedElement != null) {
            this.focusedElement.onScroll(gfx, this.pendingScrollDelta);
        }

        float f2;
        float f3;
        float scale = CheatBreaker.getScaleFactor();

        // Moving module stuff here
        if (draggingModule != null) {
            if (!Mouse.isButtonDown(1)) {
                RenderUtil.drawRoundedRect(gfx, 2, 0.0, 2.916666637692187 * 0.8571428656578064, this.scaledHeight, 0.0, -15599126);
                RenderUtil.drawRoundedRect(gfx, (float)this.scaledWidth - 5.0f * 0.5f, 0.0, this.scaledWidth - 2, this.scaledHeight, 0.0, -15599126);
                RenderUtil.drawRoundedRect(gfx, 0.0, 2, this.scaledWidth, 1.1547619104385376 * 2.164948442965692, 0.0, -15599126);
                RenderUtil.drawRoundedRect(gfx, 0.0, (float)this.scaledHeight - 1.3529412f * 2.5869565f, this.scaledWidth, this.scaledHeight - 3, 0.0, -15599126);
            }
            this.modules.sort((cBModule, cBModule2) -> {
                if (cBModule == draggingModule || cBModule2 == draggingModule || cBModule.getGuiAnchor() == null || cBModule2.getGuiAnchor() == null) {
                    return 0;
                }
                float[] modulePoints = cBModule.getScaledPoints(true);
                float[] modulePoints2 = cBModule2.getScaledPoints(true);
                float[] selectedPoints = draggingModule.getScaledPoints(true);
                Rectangle rectangle = new Rectangle((int) (modulePoints[0] * (Float) cBModule.masterScale()), (int) (modulePoints[1] * ((Float)cBModule.masterScale()).floatValue()), (int) (cBModule.width * ((Float)cBModule.masterScale()).floatValue()), (int) (cBModule.height * ((Float)cBModule.masterScale()).floatValue()));
                Rectangle rectangle2 = new Rectangle((int) (modulePoints2[0] * (Float) cBModule2.masterScale()), (int) (modulePoints2[1] * ((Float)cBModule2.masterScale()).floatValue()), (int) (cBModule2.width * ((Float)cBModule2.masterScale()).floatValue()), (int) (cBModule2.height * ((Float)cBModule2.masterScale()).floatValue()));
                Rectangle rectangle3 = new Rectangle((int) (selectedPoints[0] * (Float) draggingModule.masterScale()), (int) (selectedPoints[1] * (Float) CBModulesGui.draggingModule.masterScale()), (int) (CBModulesGui.draggingModule.width * (Float) CBModulesGui.draggingModule.masterScale()), (int) (CBModulesGui.draggingModule.height * (Float) CBModulesGui.draggingModule.masterScale()));
                try {
                    if (this.getIntersectionFloat(rectangle, rectangle3) > this.getIntersectionFloat(rectangle2, rectangle3)) {
                        return -1;
                    }
                    return 1;
                }
                catch (Exception exception) {
                    return 0;
                }
            });
            CBModulePosition CBModulePosition = this.getModulePosition(draggingModule);
            if (CBModulePosition != null) {
                this.positions.remove(CBModulePosition);
                this.positions.add(CBModulePosition);
            }
            for (CBModulePosition position : this.positions) {
                this.dragModule(position, mouseX, mouseY);
                if (!(Boolean) CheatBreaker.getInstance().getGlobalSettings().snapModules.getValue() || !this.IlIlIIIlllllIIIlIlIlIllII || Mouse.isButtonDown(1) || position.module != draggingModule) continue;
                for (AbstractModule cBModule3 : this.modules) {
                    if (this.getModulePosition(cBModule3) != null || cBModule3.getGuiAnchor() == null || !cBModule3.isEnabled()) continue;
                    float f5 = 18;
                    if (cBModule3.width < f5) {
                        cBModule3.width = f5;
                    }
                    if (cBModule3.height < (float)18) {
                        cBModule3.height = 18;
                    }
                    if (position.module.width < f5) {
                        position.module.width = f5;
                    }
                    if (position.module.height < (float)18) {
                        position.module.height = 18;
                    }
                    boolean bl = true;
                    boolean bl2 = true;
                    float[] arrf = cBModule3.getScaledPoints(true);
                    float[] scaledPoints = position.module.getScaledPoints(true);
                    float f6 = arrf[0] * (Float) cBModule3.masterScale() - scaledPoints[0] * (Float) position.module.masterScale();
                    float f7 = (arrf[0] + cBModule3.width) * (Float) cBModule3.masterScale() - (scaledPoints[0] + position.module.width) * (Float) position.module.masterScale();
                    float f8 = (arrf[0] + cBModule3.width) * (Float) cBModule3.masterScale() - scaledPoints[0] * (Float) position.module.masterScale();
                    float f9 = arrf[0] * (Float) cBModule3.masterScale() - (scaledPoints[0] + position.module.width) * (Float) position.module.masterScale();
                    float f10 = arrf[1] * (Float) cBModule3.masterScale() - scaledPoints[1] * (Float) position.module.masterScale();
                    f3 = (arrf[1] + cBModule3.height) * (Float) cBModule3.masterScale() - (scaledPoints[1] + position.module.height) * (Float) position.module.masterScale();
                    f2 = (arrf[1] + cBModule3.height) * (Float) cBModule3.masterScale() - scaledPoints[1] * (Float) position.module.masterScale();
                    float f11 = arrf[1] * (Float) cBModule3.masterScale() - (scaledPoints[1] + position.module.height) * (Float) position.module.masterScale();
                    int n3 = 2;
                    if (f6 >= (float)(-n3) && f6 <= (float)n3) {
                        bl = false;
                        this.snapHorizontally(f6);
                    }
                    if (f7 >= (float)(-n3) && f7 <= (float)n3 && bl) {
                        bl = false;
                        this.snapHorizontally(f7);
                    }
                    if (f9 >= (float)(-n3) && f9 <= (float)n3 && bl) {
                        bl = false;
                        this.snapHorizontally(f9);
                    }
                    if (f8 >= (float)(-n3) && f8 <= (float)n3 && bl) {
                        this.snapHorizontally(f8);
                    }
                    if (f10 >= (float)(-n3) && f10 <= (float)n3) {
                        bl2 = false;
                        this.snapVertically(f10);
                    }
                    if (f3 >= (float)(-n3) && f3 <= (float)n3 && bl2) {
                        bl2 = false;
                        this.snapVertically(f3);
                    }
                    if (f11 >= (float)(-n3) && f11 <= (float)n3 && bl2) {
                        bl2 = false;
                        this.snapVertically(f11);
                    }
                    if (!(f2 >= (float)(-n3)) || !(f2 <= (float)n3) || !bl2) continue;
                    this.snapVertically(f2);
                }
            }
        } else if (this.dataHolder != null) {
            float f12 = 1.0f;
            switch (this.dataHolder.screenLocation) {
                case RIGHT_BOTTOM: {
                    float n4 = mouseY - this.dataHolder.mouseY + (mouseX - this.dataHolder.mouseX);
                    f12 = this.dataHolder.scale - (float)n4 / (float)115;
                    break;
                }
                case LEFT_TOP: {
                    float n4 = mouseY - this.dataHolder.mouseY + (mouseX - this.dataHolder.mouseX);
                    f12 = this.dataHolder.scale + (float)n4 / (float)115;
                    break;
                }
                case RIGHT_TOP: {
                    float n4 = mouseX - this.dataHolder.mouseX - (mouseY - this.dataHolder.mouseY);
                    f12 = this.dataHolder.scale - (float)n4 / (float)115;
                    break;
                }
                case LEFT_BOTTOM: {
                    float n4 = mouseX - this.dataHolder.mouseX - (mouseY - this.dataHolder.mouseY);
                    f12 = this.dataHolder.scale + (float)n4 / (float)115;
                }
            }
            if (f12 >= 1.0421053f * 0.47979796f && f12 <= 1.8962264f * 0.7910448f) {
                this.dataHolder.module.scale.setValue((float) ((double) Math.round((double) f12 * (double) 100) / (double) 100));
            }
        }
        // end of moving module stuff
        boolean bl = true;

        this.setFocusedElement(scaledWidth);
        this.doKeyboardTick();

        for (AbstractModule module : this.modules) {
            boolean bl3 = this.drawModule(gfx, .85f / scale, module, (int) mouseX, (int) mouseY, bl);
            if (bl3) continue;
            bl = false;
        }

        Rectangle object;

        float f13 = (this.animationPhase * 8) / (float)255;
        gfx.pose().pushMatrix();

        int n7 = Color.WHITE.getRGB();
        if (f13 / (float)4 > 0.0f && f13 / (float)4 < 1.0f) {
            n7 = new Color(1.0f, 1.0f, 1.0f, f13 / (float)4).getRGB();
        }

        // open animation translation
        if (f13 > 1.0f) {
            gfx.pose().translate(-((this.animationPhase * 2.0f) - 32.0f) / 12.0f - 1.0f, 0.0f);
        }

        drawIcon(
                gfx,
                CheatBreaker.asset("logo_white.png"),
                (float)(scaledWidth / 2 - 14),
                (float)(scaledHeight / 2 - 47 - (CheatBreaker.getInstance().isUsingStaffModules() ? 22 : 0)),
                (float)28,
                15
        );

        if (f13 > 2.0f) {
//            CheatBreaker.getInstance().playBold18px.drawString("| CHEAT", n5 / 2 + 18, (float)(n6 / 2 - 42 - (CheatBreaker.getInstance().isUsingStaffModules() ? 22 : 0)), n7);
//            CheatBreaker.getInstance().playRegular18px.drawString("BREAKER", n5 / 2 + 53, (float)(n6 / 2 - 42 - (CheatBreaker.getInstance().isUsingStaffModules() ? 22 : 0)), n7);
            RenderUtil.drawString(
                    gfx,
                    Fonts.playBold18,
                    "| CHEAT",
                    ((float) scaledWidth / 2 + 18),
                    ((float) scaledHeight / 2 - 41 - (CheatBreaker.getInstance().isUsingStaffModules() ? 22 : 0)),
                    n7
            );

            RenderUtil.drawString(
                    gfx,
                    Fonts.playRegular18,
                    "BREAKER",
                    ((float) scaledWidth / 2 + 53),
                    ((float) scaledHeight / 2 - 45 - (CheatBreaker.getInstance().isUsingStaffModules() ? 22 : 0)),
                    n7
            );
        }

        drawModules(gfx);

        gfx.pose().popMatrix();

        for (ModulesGuiButtonElement buttonElement : this.buttons) {
            buttonElement.handleDrawElement(gfx, (int) mouseX, (int) mouseY, delta);
        }

        if (draggingModule == null) {
            gfx.pose().pushMatrix();

            gfx.enableScissor(
                    scaledWidth / 2 - 183,
                    scaledHeight / 2 + 14,
                    scaledWidth / 2 + 189,
                    scaledHeight - 20
            );

            for (AbstractScrollableElement abstractScrollableElement : this.elementList) {
                if (abstractScrollableElement != this.focusedElement && abstractScrollableElement != this.currentScrollableElement) continue;
                abstractScrollableElement.handleDrawElement(gfx, (int) mouseX, (int) mouseY, delta);
            }

            gfx.disableScissor();
            gfx.pose().popMatrix();
        }

        if (this.mouseX != -1) {
            if (Mouse.isButtonDown(0)) {
                if (this.mouseX != mouseX && this.mouseY != mouseY) {
                    this.drawMouseSelection(gfx, mouseX, mouseY);
                }
            } else {
                this.positions.clear();
                for (AbstractModule cBModule4 : this.modules) {
                    int n10;
                    int n11;
                    if (cBModule4.getGuiAnchor() == null || !cBModule4.isEnabled()) continue;
                    float[] arrf = cBModule4.getScaledPoints(true);
                    float f14 = scale / (Float) cBModule4.masterScale();
                    object = new Rectangle((int) (arrf[0] * (Float) cBModule4.masterScale() - 2.0f), (int) (arrf[1] * (Float) cBModule4.masterScale() - 2.0f), (int) (cBModule4.width * ((Float)cBModule4.masterScale()).floatValue() + (float)4), (int) (cBModule4.height * ((Float)cBModule4.masterScale()).floatValue() + (float)4));
                    if (!object.intersects(new Rectangle(n11 = (int) Math.min(this.mouseX, mouseX), n10 = (int) Math.min(this.mouseY, mouseY), (int) (Math.max(this.mouseX, mouseX) - n11), (int) (Math.max(this.mouseY, mouseY) - n10)))) continue;
                    f3 = (float)mouseX - cBModule4.getXTranslation();
                    f2 = (float)mouseY - cBModule4.getYTranslation();
                    this.positions.add(new CBModulePosition(cBModule4, f3, f2));
                }
                this.mouseX = -1;
                this.mouseY = -1;
            }
        }

    }

    private void setFocusedElement(int n) {
        if (allMenusClosed) {
            if (this.focusedElement != null) {
                this.setFocusedElement(this.focusedElement, true, n);
            }
        } else if (this.currentScrollableElement != null) {
            if (this.focusedElement != null) {
                this.setFocusedElement(this.focusedElement, true, n);
            }
            this.setFocusedElement(this.currentScrollableElement, false, n);
        }
    }

    private long lastKeyboardTick = 0;
    private void doKeyboardTick() {
        if (System.currentTimeMillis() - this.lastKeyboardTick < 25) return;
        lastKeyboardTick = System.currentTimeMillis();
        if (!this.positions.isEmpty()) {
            boolean leftKey = Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT) ;
            boolean rightDown = Keyboard.isKeyDown(GLFW.GLFW_KEY_RIGHT) ;
            boolean upDown = Keyboard.isKeyDown(GLFW.GLFW_KEY_UP) ;
            boolean downDown = Keyboard.isKeyDown(GLFW.GLFW_KEY_DOWN) ;
            if (leftKey || rightDown || upDown || downDown) {
                this.arrowKeyMoves++;
                if (this.arrowKeyMoves % 3 == 0) {
                    for (CBModulePosition position : this.positions) {
                        AbstractModule module = position.module;
                        if (module == null) continue;
                        if (leftKey) {
                            module.setTranslations(module.getXTranslation() - 1, module.getYTranslation());
                        }
                        if (rightDown) {
                            module.setTranslations(module.getXTranslation() + 1, module.getYTranslation());
                        }
                        if (upDown) {
                            module.setTranslations(module.getXTranslation(), module.getYTranslation() - 1);
                        }
                        if (downDown) {
                            module.setTranslations( module.getXTranslation(),  module.getYTranslation() + 1);
                        }
                    }
                }
            }
        }
    }

    private boolean drawModule(GuiGraphicsExtractor gfx, float f, AbstractModule cBModule, int n, int n2, boolean bl) {
        int n3;
        int n4;
        int n5;
        int n6;
        float[] object;
        boolean isDragSelected;
        if (cBModule.getGuiAnchor() == null || !cBModule.isEnabled() || !cBModule.isEditable && !cBModule.isRenderHud()) {
            return true;
        }
        boolean bl3 = false;
        float f2 = 18;
        if (cBModule.width < f2) {
            cBModule.width = f2;
        }
        if (cBModule.height < (float)18) {
            cBModule.height = 18;
        }
        gfx.pose().pushMatrix();
        float[] arrf = cBModule.getScaledPoints(true);
        cBModule.scaleAndTranslate(gfx);
        isDragSelected = this.mouseX != -1;
        if (isDragSelected) {
            Rectangle rectangle1 = new Rectangle((int) (arrf[0] * cBModule.masterScale() - 2.0f), (int) (arrf[1] * ((Float)cBModule.masterScale()).floatValue() - 2.0f), (int) (cBModule.width * ((Float)cBModule.masterScale()).floatValue() + (float)4), (int) (cBModule.height * ((Float)cBModule.masterScale()).floatValue() + (float)4));
            n6 = Math.min(this.mouseX, n);
            n5 = Math.min(this.mouseY, n2);
            n4 = Math.max(this.mouseX, n) - n6;
            n3 = Math.max(this.mouseY, n2) - n5;
            Rectangle rectangle = new Rectangle(n6, n5, n4, n3);
            isDragSelected = rectangle1.intersects(rectangle);
        }
        n6 = (float) n > (object = cBModule.getScaledPoints(true))[0] * cBModule.masterScale() && (float) n < (object[0] + cBModule.width) * cBModule.masterScale() && (float) n2 > object[1] * cBModule.masterScale() && (float) n2 < (object[1] + cBModule.height) * cBModule.masterScale() ? 1 : 0;
        if (!this.showModSizeOutline) {
            if (this.getModulePosition(cBModule) != null || isDragSelected) {
                RenderUtil.drawRectWithOutline(gfx, 0.0f, 0.0f, cBModule.width, cBModule.height, 2.064516f * 0.2421875f, -1627324417, 0x1AFFFFFF);
            } else {
                RenderUtil.drawRectWithOutline(gfx, 0.0f, 0.0f, cBModule.width, cBModule.height, 1.2179487f * 0.41052634f, 0x6FFFFFFF, 0x1AFFFFFF);
            }
        }
        if (!this.showModSizeOutline && n6 != 0) {
            n5 = !cBModule.getSettingsList().isEmpty() && (float)n >= (object[0] + 2.0f) * cBModule.masterScale() && (float)n <= (object[0] + (float)10) * ((Float)cBModule.masterScale()).floatValue() && (float)n2 >= (object[1] + cBModule.height - (float)8) * ((Float)cBModule.masterScale()).floatValue() && (float)n2 <= (object[1] + cBModule.height - 2.0f) * ((Float)cBModule.masterScale()).floatValue() ? 1 : 0;
            int n8 = n4 = (float)n > (object[0] + cBModule.width - (float)10) * cBModule.masterScale() && (float)n < (object[0] + cBModule.width - 2.0f) * ((Float)cBModule.masterScale()).floatValue() && (float)n2 > (object[1] + cBModule.height - (float)8) * ((Float)cBModule.masterScale()).floatValue() && (float)n2 < (object[1] + cBModule.height - 2.0f) * ((Float)cBModule.masterScale()).floatValue() ? 1 : 0;
            if (!cBModule.getSettingsList().isEmpty()) {
//                GL11.glColor4f(1.0f, 1.0f, 1.0f, n5 != 0 ? 1.0f : 0.20895523f * 2.8714287f);
                RenderUtil.drawIcon(gfx, this.cogIcon, (float)3, 2.0f, cBModule.height - 2.162162f * 3.4687502f, CheatBreaker.getColor(1.0f, 1.0f, 1.0f, n5 != 0 ? 1.0f : 0.20895523f * 2.8714287f));
            }
//            GL11.glColor4f(1.2952381f * 0.61764705f, 0.4181818f * 0.47826087f, 0.09268292f * 2.1578948f, n4 != 0 ? 1.0f : 2.025f * 0.2962963f);
            RenderUtil.drawIcon(gfx, this.deleteIcon, (float)3, cBModule.width - (float)8, cBModule.height - 0.2972973f * 25.227272f, CheatBreaker.getColor(1.2952381f * 0.61764705f, 0.4181818f * 0.47826087f, 0.09268292f * 2.1578948f, n4 != 0 ? 1.0f : 2.025f * 0.2962963f));
        }
        gfx.pose().pushMatrix();
        float f3 = f / cBModule.masterScale();
//        GL11.glScalef(f3, f3, f3);
        gfx.pose().scale(f3, f3);
        if (bl) {
            n4 = this.dataHolder != null && this.dataHolder.module == cBModule && this.dataHolder.screenLocation == ScreenLocation.LEFT_BOTTOM || n6 == 0 && (float)n >= (object[0] + cBModule.width - (float)5) * ((Float)cBModule.masterScale()).floatValue() && (float)n <= (object[0] + cBModule.width + (float)5) * ((Float)cBModule.masterScale()).floatValue() && (float)n2 >= (object[1] - (float)5) * ((Float)cBModule.masterScale()).floatValue() && (float)n2 <= (object[1] + (float)5) * ((Float)cBModule.masterScale()).floatValue() ? 1 : 0;
            n3 = this.dataHolder != null && this.dataHolder.module == cBModule && this.dataHolder.screenLocation == ScreenLocation.RIGHT_TOP || n6 == 0 && (float)n >= (object[0] - (float)5) * ((Float)cBModule.masterScale()).floatValue() && (float)n <= (object[0] + (float)5) * ((Float)cBModule.masterScale()).floatValue() && (float)n2 >= (object[1] + cBModule.height - (float)5) * ((Float)cBModule.masterScale()).floatValue() && (float)n2 <= (object[1] + cBModule.height + (float)5) * ((Float)cBModule.masterScale()).floatValue() ? 1 : 0;
            boolean bl5 = this.dataHolder != null && this.dataHolder.module == cBModule && this.dataHolder.screenLocation == ScreenLocation.RIGHT_BOTTOM || n6 == 0 && (float)n >= (object[0] - (float)5) * ((Float)cBModule.masterScale()).floatValue() && (float)n <= (object[0] + (float)5) * ((Float)cBModule.masterScale()).floatValue() && (float)n2 >= (object[1] - (float)5) * ((Float)cBModule.masterScale()).floatValue() && (float)n2 <= (object[1] + (float)5) * ((Float)cBModule.masterScale()).floatValue();
            boolean bl6 = this.dataHolder != null && this.dataHolder.module == cBModule && this.dataHolder.screenLocation == ScreenLocation.LEFT_TOP || n6 == 0 && (float)n >= (object[0] + cBModule.width - (float)5) * ((Float)cBModule.masterScale()).floatValue() && (float)n <= (object[0] + cBModule.width + (float)5) * ((Float)cBModule.masterScale()).floatValue() && (float)n2 >= (object[1] + cBModule.height - (float)5) * ((Float)cBModule.masterScale()).floatValue() && (float)n2 <= (object[1] + cBModule.height + (float)5) * ((Float)cBModule.masterScale()).floatValue();
//            GL11.glPushMatrix();
            gfx.pose().pushMatrix();
            float f4 = 4;
            if (this.mouseX == -1 && bl5) {
//                GL11.glTranslatef(0.0f, 0.0f, 0.0f);
//                RenderUtil.drawRect(gfx, -f4 / 2.0f, -f4 / 2.0f, f4 / 2.0f, f4 / 2.0f, -16711936);
                gfx.pose().translate(0.0f, 0.0f);
                RenderUtil.drawRect(gfx, (-f4 / 2.0f), (-f4 / 2.0f), (f4 / 2.0f), (f4 / 2.0f), -16711936);
            }
            if (this.mouseX == -1 && n4 != 0) {
//                GL11.glTranslatef(cBModule.width / f3, 0.0f, 0.0f);
//                RenderUtil.drawRect(gfx, -f4 / 2.0f, -f4 / 2.0f, f4 / 2.0f, f4 / 2.0f, -16711936);
                gfx.pose().translate(cBModule.width / f3, 0.0f);
                RenderUtil.drawRect(gfx, (-f4 / 2.0f), (-f4 / 2.0f), (f4 / 2.0f), (f4 / 2.0f), -16711936);
            }
            if (this.mouseX == -1 && bl6) {
//                GL11.glTranslatef(cBModule.width / f3, cBModule.height / f3, 0.0f);
//                RenderUtil.drawRect(gfx, -f4 / 2.0f, -f4 / 2.0f, f4 / 2.0f, f4 / 2.0f, -16711936);
                gfx.pose().translate(cBModule.width / f3, cBModule.height / f3);
                RenderUtil.drawRect(gfx, (-f4 / 2.0f), (-f4 / 2.0f), (f4 / 2.0f), (f4 / 2.0f), -16711936);
            }
            if (this.mouseX == -1 && n3 != 0) {
//                GL11.glTranslatef(0.0f, cBModule.height / f3, 0.0f);
//                RenderUtil.drawRect(gfx, -f4 / 2.0f, -f4 / 2.0f, f4 / 2.0f, f4 / 2.0f, -16711936);
                gfx.pose().translate(0.0f, cBModule.height / f3);
                RenderUtil.drawRect(gfx, (-f4 / 2.0f), (-f4 / 2.0f), (f4 / 2.0f), (f4 / 2.0f), -16711936);
            }
//            GL11.glPopMatrix();
            gfx.pose().popMatrix();
            bl3 = this.mouseX == -1 && (bl5 || n4 != 0 || n3 != 0 || bl6);
        }
        n4 = arrf[1] - Fonts.ubuntuMedium16.height() - (float)6 < 0.0f ? 1 : 0;
        float f5 = n4 != 0 ? cBModule.height * cBModule.masterScale() / f : (float)(-Fonts.ubuntuMedium16.height() - 4);
        switch (cBModule.getPosition()) {
            case LEFT: {
                float f6 = 0.0f;
//                CheatBreaker.getInstance().ubuntuMedium16px.drawString(cBModule.getName(), f6, f5, -1);
                RenderUtil.drawString(gfx, Fonts.ubuntuMedium16, cBModule.getName(), f6, f5, -1);
                break;
            }
            case CENTER: {
                float f7 = cBModule.width * cBModule.masterScale() / f / 2.0f;
//                CheatBreaker.getInstance().ubuntuMedium16px.drawString(cBModule.getName(), f7, f5, -1);
                RenderUtil.drawString(gfx, Fonts.ubuntuMedium16, cBModule.getName(), f7 - (float)Fonts.ubuntuMedium16.width(cBModule.getName()) / 2.0f, f5, -1);
                break;
            }
            case RIGHT: {
                float f8 = cBModule.width * cBModule.masterScale() / f - (float) Fonts.ubuntuMedium16.width(cBModule.getName());
//                CheatBreaker.getInstance().ubuntuMedium16px.drawString(cBModule.getName(), f8, f5, -1);
                RenderUtil.drawString(gfx, Fonts.ubuntuMedium16, cBModule.getName(), f8, f5, -1);
                break;
            }
        }
        gfx.pose().popMatrix();
        gfx.pose().popMatrix();
        return !bl3;
    }


    private void setFocusedElement(AbstractScrollableElement abstractScrollableElement, boolean bl, int n) {
        if (bl) {
            abstractScrollableElement.x = n / 2 - 182;
            allMenusClosed = false;
            this.focusedElement = null;
        } else {
            this.currentScrollableElement = null;
            this.focusedElement = abstractScrollableElement;
        }
    }

    private void drawMouseSelection(GuiGraphicsExtractor gfx, float mouseX, float mouseY) {
        RenderUtil.drawRect(gfx, mouseX, this.mouseY, mouseX + 1.1538461f * 0.43333334f, mouseY, -1358888961);

        RenderUtil.drawRect(gfx,
                this.mouseX - 0.4329897f * 1.1547619f,
                mouseY,
                mouseX + 18.2f * 0.027472526f,
                mouseY + 0.121212125f * 4.125f,
                -1358888961
        );

        RenderUtil.drawRect(gfx,
                this.mouseX - 0.8666667f * 0.5769231f,
                this.mouseY,
                this.mouseX,
                mouseY,
                -1358888961
        );

        RenderUtil.drawRect(gfx,
                this.mouseX - 0.557971f * 0.8961039f,
                this.mouseY - 0.3611111f * 1.3846154f,
                mouseX + 1.2692307f * 0.3939394f,
                this.mouseY,
                -1358888961
        );

        RenderUtil.drawRect(gfx, this.mouseX, this.mouseY, mouseX, mouseY, 0x1F00FFFF);
    }

    @Override
    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (this.focusedElement != null && this.focusedElement.isMouseInside( mouseX,  mouseY, true)) {
            this.focusedElement.onClick((int) mouseX, (int) mouseY, button);
            return true;
        } else {
            AbstractModule iterator;
            if (!(draggingModule != null && this.IlIlIIIlllllIIIlIlIlIllII || (iterator = this.getModuleAtPosition(mouseX, mouseY)) == null)) {
                boolean bl;
                float[] arrf = iterator.getScaledPoints(true);
                boolean bl2 = !iterator.getSettingsList().isEmpty() && (float)mouseX >= arrf[0] * ((Float) iterator.masterScale()).floatValue() && (float)mouseX <= (arrf[0] + (float)10) * ((Float) iterator.masterScale()).floatValue() && (float)mouseY >= (arrf[1] + iterator.height - (float)10) * ((Float) iterator.masterScale()).floatValue() && (float)mouseY <= (arrf[1] + iterator.height + 2.0f) * ((Float) iterator.masterScale()).floatValue();
                boolean bl3 = bl = (float)mouseX > (arrf[0] + iterator.width - (float)10) * ((Float) iterator.masterScale()).floatValue() && (float)mouseX < (arrf[0] + iterator.width + 2.0f) * ((Float) iterator.masterScale()).floatValue() && (float)mouseY > (arrf[1] + iterator.height - (float)10) * ((Float) iterator.masterScale()).floatValue() && (float)mouseY < (arrf[1] + iterator.height + 2.0f) * ((Float) iterator.masterScale()).floatValue();
                if (bl2) {
                    CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
                    ((ModuleListElement)this.settingsElement).resetColor = false;
                    ((ModuleListElement)this.settingsElement).module = iterator;
                    this.currentScrollableElement = this.settingsElement;
                } else if (bl) {
                    CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
                    iterator.setState(false);
                }
                return true;
            }

            for (AbstractModule module : this.modules) {
                GuiAnchor anchor;
                ScreenLocation screenLocation;
                if (module.getGuiAnchor() == null || !module.isEnabled())
                    continue;
                float[] arrf = module.getScaledPoints(true);
                boolean bl4 = (float) mouseX > arrf[0] * module.masterScale() && (float) mouseX < (arrf[0] + module.width) * module.masterScale() && (float) mouseY > arrf[1] * module.masterScale() && (float) mouseY < (arrf[1] + module.height) * module.masterScale();
                boolean bl5 = this.dataHolder != null && this.dataHolder.module == module && this.dataHolder.screenLocation == ScreenLocation.LEFT_BOTTOM || !bl4 && (float) mouseX >= (arrf[0] + module.width - (float) 5) * module.masterScale() && (float) mouseX <= (arrf[0] + module.width + (float) 5) * module.masterScale() && (float) mouseY >= (arrf[1] - (float) 5) * module.masterScale() && (float) mouseY <= (arrf[1] + (float) 5) * module.masterScale();
                boolean bl6 = this.dataHolder != null && this.dataHolder.module == module && this.dataHolder.screenLocation == ScreenLocation.RIGHT_TOP || !bl4 && (float) mouseX >= (arrf[0] - (float) 5) * module.masterScale() && (float) mouseX <= (arrf[0] + (float) 5) * module.masterScale() && (float) mouseY >= (arrf[1] + module.height - (float) 5) * module.masterScale() && (float) mouseY <= (arrf[1] + module.height + (float) 5) * module.masterScale();
                boolean bl7 = this.dataHolder != null && this.dataHolder.module == module && this.dataHolder.screenLocation == ScreenLocation.RIGHT_BOTTOM || !bl4 && (float) mouseX >= (arrf[0] - (float) 5) * module.masterScale() && (float) mouseX <= (arrf[0] + (float) 5) * module.masterScale() && (float) mouseY >= (arrf[1] - (float) 5) * module.masterScale() && (float) mouseY <= (arrf[1] + (float) 5) * module.masterScale();
                boolean bl = this.dataHolder != null && this.dataHolder.module == module && this.dataHolder.screenLocation == ScreenLocation.LEFT_TOP || !bl4 && (float) mouseX >= (arrf[0] + module.width - (float) 5) * module.masterScale() && (float) mouseX <= (arrf[0] + module.width + (float) 5) * module.masterScale() && (float) mouseY >= (arrf[1] + module.height - (float) 5) * module.masterScale() && (float) mouseY <= (arrf[1] + module.height + (float) 5) * module.masterScale();
                if (this.mouseX != -1 || !bl5 && !bl6 && !bl7 && !bl) continue;
                if (bl5) {
                    screenLocation = ScreenLocation.LEFT_BOTTOM;
                    anchor = GuiAnchor.LEFT_BOTTOM;
                } else if (bl6) {
                    screenLocation = ScreenLocation.RIGHT_TOP;
                    anchor = GuiAnchor.RIGHT_TOP;
                } else if (bl7) {
                    screenLocation = ScreenLocation.RIGHT_BOTTOM;
                    anchor = GuiAnchor.RIGHT_BOTTOM;
                } else {
                    screenLocation = ScreenLocation.LEFT_TOP;
                    anchor = GuiAnchor.LEFT_TOP;
                }

                if (this.isHoveringModule(mouseX, mouseY)) continue;
                if (button == 0) {
                    this.undoList.add(new ModuleActionData(this, this.positions));
                    this.dataHolder = new ModuleDataHolder(this, module, screenLocation, (int) mouseX, (int) mouseY);
                    this.updateModuleAnchorAndTranslation(module, anchor, mouseX, mouseY);
                } else if (button == 1) {
                    GuiAnchor cBGuiAnchor2 = module.getGuiAnchor();
                    this.updateModuleAnchorAndTranslation(module, anchor, mouseX, mouseY);
                    module.scale.setValue(1.0f);
                    this.updateModuleAnchorAndTranslation(module, cBGuiAnchor2, mouseX, mouseY);
                }
                return true;
            }

            if (draggingModule == null) {
                if (this.handleMainButtonPress((int) mouseX, (int) mouseY, button)) return true;
                handleModulePreviewClick(mouseX, mouseY, button);
            }

            if (this.isHoveringModule(mouseX, mouseY)) return true;
            this.mouseX = (int) mouseX;
            this.mouseY = (int) mouseY;
        }

        return false;
    }


    private void handleModulePreviewClick(double n, double n2, int n3) {
        for (AbstractModule cBModule : this.modules) {
            boolean bl;
            float[] arrf;
            if (cBModule.getGuiAnchor() == null || !cBModule.isEnabled())
                continue;
            float f = cBModule.width;
            float f2 = cBModule.height;
            float f3 = 18;
            if (f < f3) {
                cBModule.width = f3;
            }
            if (f2 < (float) 18) {
                cBModule.height = 18;
            }
            if (!((float) n > (arrf = cBModule.getScaledPoints(true))[0] * (Float) cBModule.masterScale() && (float) n < (arrf[0] + cBModule.width) * ((Float) cBModule.masterScale()).floatValue() && (float) n2 > arrf[1] * ((Float) cBModule.masterScale()).floatValue() && (float) n2 < (arrf[1] + cBModule.height) * ((Float) cBModule.masterScale()).floatValue()))
                continue;
            boolean bl3 = !cBModule.getSettingsList().isEmpty() && (float) n >= arrf[0] * (Float) cBModule.masterScale() && (float) n <= (arrf[0] + (float) 10) * ((Float) cBModule.masterScale()).floatValue() && (float) n2 >= (arrf[1] + cBModule.height - (float) 10) * ((Float) cBModule.masterScale()).floatValue() && (float) n2 <= (arrf[1] + cBModule.height + 2.0f) * ((Float) cBModule.masterScale()).floatValue();
            boolean bl4 = bl = (float) n > (arrf[0] + cBModule.width - (float) 10) * (Float) cBModule.masterScale() && (float) n < (arrf[0] + cBModule.width + 2.0f) * ((Float) cBModule.masterScale()).floatValue() && (float) n2 > (arrf[1] + cBModule.height - (float) 10) * ((Float) cBModule.masterScale()).floatValue() && (float) n2 < (arrf[1] + cBModule.height + 2.0f) * ((Float) cBModule.masterScale()).floatValue();
            if (n3 == 0 && !bl3 && !bl) {
                boolean bl5 = true;
                if (this.getModulePosition(cBModule) != null) {
                    this.removePositionForModule(cBModule);
                    bl5 = false;
                }
                float f4 = (float) n - cBModule.getXTranslation() * (Float) cBModule.masterScale();
                float f5 = (float) n2 - cBModule.getYTranslation() * (Float) cBModule.masterScale();
                this.mouseX2 = -1;
                this.mouseY2 = -1;
                this.IlIlIIIlllllIIIlIlIlIllII = false;
                draggingModule = cBModule;
                if (this.getModulePosition(cBModule) == null) {
                    if (!Keyboard.isCtrlKeyDown() && bl5) {
                        this.positions.clear();
                    }
                    if (bl5 || !Keyboard.isCtrlKeyDown()) {
                        this.positions.add(new CBModulePosition(cBModule, f4, f5));
                    }
                }
                this.setSelectedModulesPosition(n, n2);
            }
            if (!(n3 != 0 || this.focusedElement != null && this.focusedElement.isMouseInside(n, n2, true))) {
                if (bl3) {
                    CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
                    ((ModuleListElement)this.settingsElement).resetColor = false;
                    ((ModuleListElement)this.settingsElement).module = cBModule;
                    this.currentScrollableElement = this.settingsElement;
                } else if (bl) {
                    CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
                    cBModule.setState(false);
                }
            } else if (n3 == 1) {
                CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
                float[] arrf2 = CBAnchorHelper.getPositions(cBModule.getGuiAnchor());
                cBModule.setTranslations(arrf2[0], arrf2[1]);
            }
            break;
        }
    }

    private void setSelectedModulesPosition(double n, double n2) {
        for (CBModulePosition position : this.positions) {
            if (position.module == null || position.module.getGuiAnchor() == null) continue;
            position.x = (float)n - position.module.getXTranslation();
            position.y = (float)n2 - position.module.getYTranslation();
        }
    }

    private void removePositionForModule(AbstractModule cBModule) {
        this.positions.removeIf(CBModulePosition -> CBModulePosition.module == cBModule);
    }

    private void updateModuleAnchorAndTranslation(AbstractModule module, GuiAnchor anchor, double n, double n2) {
        if (anchor != module.getGuiAnchor()) {
            float[] arrf = module.getScaledPoints(true);
            module.setAnchor(anchor);
            float[] arrf2 = module.getScaledPoints(false);
            module.setTranslations(arrf[0] * module.masterScale() - arrf2[0] * module.masterScale(), arrf[1] * module.masterScale() - arrf2[1] * module.masterScale());
        }

    }

    private boolean isHoveringModule(double n, double n2) {
        boolean bl = false;
        for (AbstractModule abstractModule : this.modules) {
            if (abstractModule.getGuiAnchor() == null) continue;
            float[] arrf = abstractModule.getScaledPoints(true);
            boolean bl2 = (float)n > arrf[0] * abstractModule.masterScale() && (float)n < (arrf[0] + abstractModule.width) * abstractModule.masterScale() && (float)n2 > arrf[1] * abstractModule.masterScale() && (float)n2 < (arrf[1] + abstractModule.height) * abstractModule.masterScale();
            bl = bl || bl2;
        }
        return bl;
    }

    private AbstractModule getModuleAtPosition(double n, double n2) {
        for (AbstractModule cBModule : this.modules) {
            if (cBModule.getGuiAnchor() == null) continue;
            float[] arrf = cBModule.getScaledPoints(true);
            boolean bl = (float) n > (arrf[0] + cBModule.width - (float) 10) * cBModule.masterScale() && (float) n < (arrf[0] + cBModule.width + 2.0f) * cBModule.masterScale() && (float) n2 > (arrf[1] + cBModule.height - (float) 10) * cBModule.masterScale() && (float) n2 < (arrf[1] + cBModule.height + 2.0f) * cBModule.masterScale();
            boolean bl2 = !cBModule.getSettingsList().isEmpty() && (float)n >= arrf[0] * cBModule.masterScale() && (float)n <= (arrf[0] + (float)10) * cBModule.masterScale() && (float)n2 >= (arrf[1] + cBModule.height - (float)10) * cBModule.masterScale() && (float)n2 <= (arrf[1] + cBModule.height + 2.0f) * cBModule.masterScale();
            if (!bl && !bl2) continue;
            return cBModule;
        }
        return null;
    }

    private void drawModules(GuiGraphicsExtractor gfx) {
        if (!Mouse.isButtonDown(1) && draggingModule != null) {
            for (CBModulePosition CBModulePosition : this.positions) {
                if (CBModulePosition.module != draggingModule || !(Boolean) CheatBreaker.getInstance().getGlobalSettings().snapModules.getValue()) continue;
                for (AbstractModule cBModule : this.modules) {
                    if (this.getModulePosition(cBModule) != null || cBModule.getGuiAnchor() == null || !cBModule.isEnabled() || !cBModule.isEditable && !cBModule.isRenderHud()) continue;
                    float f = 18;
                    if (cBModule.width < f) {
                        cBModule.width = f;
                    }
                    if (cBModule.height < (float)18) {
                        cBModule.height = 18;
                    }
                    if (CBModulePosition.module.width < f) {
                        CBModulePosition.module.width = f;
                    }
                    if (CBModulePosition.module.height < (float)18) {
                        CBModulePosition.module.height = 18;
                    }
                    float[] arrf = cBModule.getScaledPoints(true);
                    float[] arrf2 = CBModulePosition.module.getScaledPoints(true);
                    boolean bl = false;
                    float f2 = arrf[0] * cBModule.masterScale() - arrf2[0] * (Float) CBModulePosition.module.masterScale();
                    float f3 = (arrf[0] + cBModule.width) * cBModule.masterScale() - (arrf2[0] + CBModulePosition.module.width) * (Float) CBModulePosition.module.masterScale();
                    float f4 = (arrf[0] + cBModule.width) * cBModule.masterScale() - arrf2[0] * (Float) CBModulePosition.module.masterScale();
                    float f5 = arrf[0] * cBModule.masterScale() - (arrf2[0] + CBModulePosition.module.width) * (Float) CBModulePosition.module.masterScale();
                    float f6 = arrf[1] * cBModule.masterScale() - arrf2[1] * (Float) CBModulePosition.module.masterScale();
                    float f7 = (arrf[1] + cBModule.height) * cBModule.masterScale() - (arrf2[1] + CBModulePosition.module.height) * (Float) CBModulePosition.module.masterScale();
                    float f8 = (arrf[1] + cBModule.height) * cBModule.masterScale() - arrf2[1] * (Float) CBModulePosition.module.masterScale();
                    float f9 = arrf[1] * cBModule.masterScale() - (arrf2[1] + CBModulePosition.module.height) * (Float) CBModulePosition.module.masterScale();
                    int n = 2;
                    if (f2 >= (float)(-n) && f2 <= (float)n) {
                        bl = true;
                        RenderUtil.drawRoundedRect(gfx, arrf[0] * cBModule.masterScale() - 0.6666667f * 0.75f, 0.0, arrf[0] * cBModule.masterScale(), this.scaledHeight, 0.0, -3596854);
                    }
                    if (f3 >= (float)(-n) && f3 <= (float)n) {
                        bl = true;
                        RenderUtil.drawRoundedRect(gfx, (arrf[0] + cBModule.width) * cBModule.masterScale(), 0.0, (arrf[0] + cBModule.width) * cBModule.masterScale() + 1.7272727f * 0.28947368f, this.scaledHeight, 0.0, -3596854);
                    }
                    if (f5 >= (float)(-n) && f5 <= (float)n) {
                        bl = true;
                        RenderUtil.drawRoundedRect(gfx, arrf[0] * cBModule.masterScale(), 0.0, arrf[0] * cBModule.masterScale() + 0.29775283f * 1.6792452f, this.scaledHeight, 0.0, -3596854);
                    }
                    if (f4 >= (float)(-n) && f4 <= (float)n) {
                        bl = true;
                        RenderUtil.drawRoundedRect(gfx, (arrf[0] + cBModule.width) * cBModule.masterScale(), 0.0, (arrf[0] + cBModule.width) * cBModule.masterScale() + 1.5238096f * 0.328125f, this.scaledHeight, 0.0, -3596854);
                    }
                    if (f6 >= (float)(-n) && f6 <= (float)n) {
                        bl = true;
                        RenderUtil.drawRoundedRect(gfx, 0.0, arrf[1] * cBModule.masterScale(), this.scaledWidth, arrf[1] * cBModule.masterScale() + 0.3888889f * 1.2857143f, 0.0, -3596854);
                    }
                    if (f7 >= (float)(-n) && f7 <= (float)n) {
                        bl = true;
                        RenderUtil.drawRoundedRect(gfx, 0.0, (arrf[1] + cBModule.height) * cBModule.masterScale(), this.scaledWidth, (arrf[1] + cBModule.height) * cBModule.masterScale() + 0.51724136f * 0.9666667f, 0.0, -3596854);
                    }
                    if (f9 >= (float)(-n) && f9 <= (float)n) {
                        bl = true;
                        RenderUtil.drawRoundedRect(gfx, 0.0, arrf[1] * cBModule.masterScale(), this.scaledWidth, arrf[1] * cBModule.masterScale() + 0.16666667f * 3.0f, 0.0, -3596854);
                    }
                    if (f8 >= (float)(-n) && f8 <= (float)n) {
                        bl = true;
                        RenderUtil.drawRoundedRect(gfx, 0.0, (arrf[1] + cBModule.height) * ((Float)cBModule.masterScale()).floatValue() - 0.5810811f * 0.8604651f, this.scaledWidth, (arrf[1] + cBModule.height) * ((Float)cBModule.masterScale()).floatValue(), 0.0, -3596854);
                    }
                    if (!bl) continue;
                    gfx.pose().pushMatrix();
                    RenderUtil.drawRectWithOutline(gfx, 0f, 0.0f, cBModule.width, cBModule.height, 0.01923077f * 26.0f, 0, 449387978);
                    gfx.pose().popMatrix();
                }
            }
        }
    }

    private CBModulePosition getModulePosition(AbstractModule cBModule) {
        for (CBModulePosition CBModulePosition : this.positions) {
            if (cBModule != CBModulePosition.module) continue;
            return CBModulePosition;
        }
        return null;
    }

    @Override
    protected void onMouseReleased(double mouseX, double mouseY, int button) {
        if (this.dataHolder != null && button == 0) {
            this.updateModuleAnchorAndTranslations(this.dataHolder.module, this.dataHolder.anchor);
            this.dataHolder = null;
        }
        if (draggingModule != null && button == 0) {
            if (this.IlIlIIIlllllIIIlIlIlIllII) {
                for (CBModulePosition CBModulePosition : this.positions) {
                    GuiAnchor cBGuiAnchor = CBAnchorHelper.getAnchor((float) mouseX, (float) mouseY, this);
                    if (cBGuiAnchor == CBModulePosition.module.getGuiAnchor() || !this.IlIlIIIlllllIIIlIlIlIllII) continue;
                    this.updateModuleAnchorAndTranslations(CBModulePosition.module, cBGuiAnchor);
                    CBModulePosition.x = (float)mouseX - CBModulePosition.module.getXTranslation();
                    CBModulePosition.y = (float)mouseY - CBModulePosition.module.getYTranslation();
                }
                if (this.getModulePosition(draggingModule) == null) {
                    float x = (float)mouseX - draggingModule.getXTranslation();
                    float y = (float)mouseY - draggingModule.getYTranslation();
                    this.positions.add(new CBModulePosition(draggingModule, x, y));
                }
                CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            }
            draggingModule = null;
        }
    }

    private void updateModuleAnchorAndTranslations(AbstractModule cBModule, GuiAnchor cBGuiAnchor) {
        if (cBGuiAnchor != cBModule.getGuiAnchor()) {
            float[] scaledPointsWithTranslations = cBModule.getScaledPoints(true);
            cBModule.setAnchor(cBGuiAnchor);
            float[] scaledPointsWithoutTranslations = cBModule.getScaledPoints(false);
            cBModule.setTranslations(
                    scaledPointsWithTranslations[0] * (Float) cBModule.scale.getValue() - scaledPointsWithoutTranslations[0] * (Float) cBModule.scale.getValue(),
                    scaledPointsWithTranslations[1] * (Float) cBModule.scale.getValue() - scaledPointsWithoutTranslations[1] * (Float) cBModule.scale.getValue()
            );
        }
    }

    public static float getSmoothFloat(float f) {
        float f2 = f / (float)(Minecraft.getInstance().getFps() + 1);
        return Math.max(f2, 1.0f);
    }

    private boolean handleMainButtonPress(int n, int n2, int n3) {
        for (ModulesGuiButtonElement button : this.buttons) {
            if (n3 != 0 || !button.isMouseInside(n, n2, true) || allMenusClosed) continue;
            if (button.scrollableElement != null && this.focusedElement != button.scrollableElement && this.currentScrollableElement == null) {
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                this.currentScrollableElement = button.scrollableElement;
                return true;
            }
            if (button.scrollableElement == null || this.currentScrollableElement != null) continue;
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            allMenusClosed = true;
            return true;
        }

        return false;
    }


    private float getIntersectionFloat(Rectangle rectangle, Rectangle rectangle2) {
        float f = Math.max(Math.abs(rectangle.x - rectangle2.x) - rectangle2.width / 2, 0);
        float f2 = Math.max(Math.abs(rectangle.y - rectangle2.y) - rectangle2.height / 2, 0);
        return f * f + f2 * f2;
    }


    private void snapHorizontally(float f) {
        for (CBModulePosition CBModulePosition : this.positions) {
            CBModulePosition.module.setTranslations(CBModulePosition.module.getXTranslation() + f, CBModulePosition.module.getYTranslation());
        }
    }

    private void snapVertically(float f) {
        for (CBModulePosition CBModulePosition : this.positions) {
            CBModulePosition.module.setTranslations(CBModulePosition.module.getXTranslation(), CBModulePosition.module.getYTranslation() + f);
        }
    }

    private void dragModule(CBModulePosition CBModulePosition, double n, double n2) {
        if (CBModulePosition.module.getGuiAnchor() == null || !CBModulePosition.module.isEnabled() || !CBModulePosition.module.isEditable && !CBModulePosition.module.isRenderHud()) {
            return;
        }
        float f = (float)n - CBModulePosition.x;
        float f2 = (float)n2 - CBModulePosition.y;
        if (!(this.IlIlIIIlllllIIIlIlIlIllII || CBModulePosition.module != draggingModule || (float)n == this.mouseX2 && (float)n2 == this.mouseY2)) {
            if (this.undoList.size() > 50) {
                this.undoList.removeFirst();
            }
            this.undoList.add(new ModuleActionData(this, this.positions));
            CheatBreaker.getInstance().createNewProfile();
            this.IlIlIIIlllllIIIlIlIlIllII = true;
        }
        float[] arrf = CBModulePosition.module.getScaledPoints(false);
        if (!Mouse.isButtonDown(1) && this.IlIlIIIlllllIIIlIlIlIllII && CBModulePosition.module == draggingModule) {
            float f3 = f;
            float f4 = f2;
            f = this.clampModuleXTranslation(CBModulePosition.module, f, arrf, (int) (CBModulePosition.module.width * (Float) CBModulePosition.module.masterScale()));
            f2 = this.clampModuleYTranslation(CBModulePosition.module, f2, arrf, (int) (CBModulePosition.module.height * (Float) CBModulePosition.module.masterScale()));
            float f5 = f3 - f;
            float f6 = f4 - f2;
            for (CBModulePosition dragCache2 : this.positions) {
                if (dragCache2 == CBModulePosition) continue;
                arrf = dragCache2.module.getScaledPoints(false);
                float f7 = this.clampModuleXTranslation(dragCache2.module, dragCache2.module.getXTranslation() - f5, arrf, (int) (dragCache2.module.width * (Float) dragCache2.module.scale.getValue()));
                float f8 = this.clampModuleYTranslation(dragCache2.module, dragCache2.module.getYTranslation() - f6, arrf, (int) (dragCache2.module.height * (Float) dragCache2.module.scale.getValue()));
                dragCache2.module.setTranslations(f7, f8);
            }
        }
        if (this.IlIlIIIlllllIIIlIlIlIllII) {
            CBModulePosition.module.setTranslations(f, f2);
        }
    }


    private float clampModuleXTranslation(AbstractModule cBModule, float f, float[] arrf, int n) {
        float f2 = f;
        float padding = 2.0f;
        if (f2 + arrf[0] * (Float) cBModule.masterScale() < padding) {
            f2 = -arrf[0] * (Float) cBModule.masterScale() + padding;
        } else if (f2 + arrf[0] * (Float) cBModule.masterScale() + (float)n > (float)this.scaledWidth - padding) {
            f2 = (float)this.scaledWidth - arrf[0] * (Float) cBModule.masterScale() - (float)n - padding;
        }
        return f2;
    }


    private float clampModuleYTranslation(AbstractModule cBModule, float f, float[] arrf, int n) {
        float f2 = f;
        float padding = 2.0f;
        if (f2 + arrf[1] * (Float) cBModule.masterScale() < padding) {
            f2 = -arrf[1] * (Float) cBModule.masterScale() + padding;
        } else if (f2 + arrf[1] * (Float) cBModule.masterScale() + (float)n > (float)this.scaledHeight - padding) {
            f2 = (float)this.scaledHeight - arrf[1] * (Float) cBModule.masterScale() - (float)n - padding;
        }
        return f2;
    }
}
