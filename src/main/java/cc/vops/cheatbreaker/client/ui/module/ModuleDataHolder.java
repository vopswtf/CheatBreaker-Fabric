package cc.vops.cheatbreaker.client.ui.module;

import cc.vops.cheatbreaker.client.module.AbstractModule;

class ModuleDataHolder {
    protected AbstractModule module;
    protected float xTranslation;
    protected float yTranslation;
    protected float scale;
    protected float scaledWidth;
    protected float scaledHeight;
    protected int mouseY;
    protected int mouseX;
    protected ScreenLocation screenLocation;
    protected GuiAnchor anchor;
    final CBModulesGui parent;

    public ModuleDataHolder(CBModulesGui parent, AbstractModule module, ScreenLocation screenLocation, int mouseX, int mouseY) {
        this.parent = parent;
        this.module = module;
        this.xTranslation = module.getXTranslation();
        this.yTranslation = module.getYTranslation();
        this.scaledWidth = module.width * (Float) module.scale.getValue();
        this.scaledHeight = module.height * (Float) module.scale.getValue();
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.screenLocation = screenLocation;
        this.scale = (Float) module.scale.getValue();
        this.anchor = module.getGuiAnchor();
    }
}

