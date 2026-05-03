package cc.vops.cheatbreaker.client.ui.element.profile;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.ConfigManager;
import cc.vops.cheatbreaker.client.config.Profile;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.ui.element.AbstractModulesGuiElement;
import cc.vops.cheatbreaker.client.ui.element.AbstractScrollableElement;
import cc.vops.cheatbreaker.client.ui.module.CBModulesGui;
import cc.vops.cheatbreaker.client.ui.module.CBProfileCreateGui;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfilesListElement extends AbstractScrollableElement {
    private final int highlightColor;
    public final List<ProfileElement> profileElementList;
    private final Identifier plusIcon = CheatBreaker.asset("icons/plus-64.png");

    public ProfilesListElement(float f, int n, int n2, int n3, int n4) {
        super(f, n, n2, n3, n4);
        this.highlightColor = -12418828;
        this.profileElementList = new ArrayList<>();
        this.loadProfiles();
    }

    @Override
    public void handleDrawElement(GuiGraphicsExtractor gfx, int mouseX, int mouseY, float partialTicks) {
        Object object;
        int n3;
        RenderUtil.drawRoundedRect(gfx, (double)this.x, (double)this.y, (double)(this.x + this.width), (double)(this.y + this.height + 2), (double)8, -657931);
        this.preDraw(gfx, mouseX, mouseY);
        this.scrollHeight = 15;
        for (n3 = 0; n3 < this.profileElementList.size(); ++n3) {
            object = this.profileElementList.get(n3);
            ((AbstractModulesGuiElement)object).setDimensions(this.x + 4, this.y + 4 + n3 * 18, this.width - 12, 18);
            ((ProfileElement)object).yOffset = this.scrollAmount;
            ((ProfileElement)object).handleDrawElement(gfx, mouseX, mouseY, partialTicks);
            this.scrollHeight += ((AbstractModulesGuiElement)object).getHeight();
        }
        double sf = CheatBreaker.getScaleFactor();
        n3 = (float) mouseX * sf > (float)(this.x + this.width - 92) * this.scale && (float) mouseX * sf < (float)(this.x + this.width - 6) * this.scale && (float) mouseY * sf > (float)(this.y + this.scrollHeight - 10 + this.scrollAmount) * this.scale && (float) mouseY * sf < (float)(this.y + this.scrollHeight + 3 + this.scrollAmount) * this.scale ? 1 : 0;
        int color = CheatBreaker.getColor(n3 != 0 ? 0.0f : 0.22590362f * 1.1066667f, n3 != 0 ? 1.4117647f * 0.56666666f : 1.3333334f * 0.1875f, n3 != 0 ? 0.0f : 0.14423077f * 1.7333333f, 1.7058823f * 0.38103446f);
        RenderUtil.drawIcon(gfx, this.plusIcon, 3.4435484f * 1.0163934f, (float)(this.x + this.width - 15), (float)(this.y + this.scrollHeight) - 0.6506024f * 9.990741f);
        object = (n3 != 0 ? "(COPIES CURRENT PROFILE) " : "") + "ADD NEW PROFILE";
        RenderUtil.drawString(gfx, Fonts.ubuntuMedium16, (String)object, (float)(this.x + this.width - 17) - Fonts.ubuntuMedium16.width((String)object), (float)(this.y + this.scrollHeight) - 6.0f, color);
        this.scrollHeight += 10;
        this.postDraw(gfx, mouseX, mouseY);
    }

    @Override
    public void onClick(int mouseX, int mouseY, int button) {
        for (ProfileElement iIlIlllllIIIlIIllIllIlIlI : this.profileElementList) {
            if (!iIlIlllllIIIlIIllIllIlIlI.isMouseInside(mouseX, mouseY, true)) continue;
            iIlIlllllIIIlIIllIllIlIlI.onClick(mouseX, mouseY - scrollAmount, button);
            return;
        }
        boolean bl = (float) mouseX * CheatBreaker.getScaleFactor() > (float)(this.x + this.width - 92) * this.scale && (float) mouseX * CheatBreaker.getScaleFactor() < (float)(this.x + this.width - 6) * this.scale && (float) mouseY * CheatBreaker.getScaleFactor() > (float)(this.y + this.scrollHeight - 20 + this.scrollAmount) * this.scale && (float) mouseY * CheatBreaker.getScaleFactor() < (float)(this.y + this.scrollHeight - 7 + this.scrollAmount) * this.scale;
        if (bl) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            Minecraft.getInstance().setScreen(new CBProfileCreateGui(CBModulesGui.instance,this, this.highlightColor, this.scale));
        }
    }

    @Override
    public boolean hasSettings(AbstractModule cBModule) {
        return true;
    }

    @Override
    public void handleModuleMouseClick(AbstractModule cBModule) {
    }

    public void loadProfiles() {
        new Thread(() -> {
            this.profileElementList.clear();
            File file = ConfigManager.profilesDir;
            if (file.exists()) {
                for (File file2 : file.listFiles()) {
                    if (!file2.getName().endsWith(".json")) continue;
                    Profile profile = null;
                    for (Profile profile2 : CheatBreaker.getInstance().getProfiles()) {
                        if (!file2.getName().equals(profile2.getName() + ".json")) continue;
                        profile = profile2;
                    }
                    if (profile != null) continue;
                    CheatBreaker.getInstance().getProfiles().add(new Profile(file2.getName().replace(".json", ""), false));
                }
            }
            for (Profile profile : CheatBreaker.getInstance().getProfiles()) {
                this.profileElementList.add(new ProfileElement(this, this.highlightColor, profile, this.scale));
            }
//            System.out.println("Loaded " + this.profileElementList.size() + " profiles.");
            for (ProfileElement profileElement : this.profileElementList) {
//                System.out.println("Loaded profile " + profileElement.profile.getName() + " with index " + profileElement.profile.getIndex());
            }
            this.profileElementList.sort((profileElement1, profileElement2) -> {
                if (profileElement1.profile.getName().equalsIgnoreCase("default")) {
                    return 0;
                }
                if (profileElement1.profile.getIndex() < profileElement2.profile.getIndex()) {
                    return -1;
                }
                return 1;
            });
        }).start();
    }
}