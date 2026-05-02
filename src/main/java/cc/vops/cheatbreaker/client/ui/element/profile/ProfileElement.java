package cc.vops.cheatbreaker.client.ui.element.profile;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.config.ConfigManager;
import cc.vops.cheatbreaker.client.config.Profile;
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
import java.util.Collections;

// IIlIlllllIIIlIIllIllIlIlI
public class ProfileElement extends AbstractModulesGuiElement {
    private final int IllIIIIIIIlIlIllllIIllIII;
    public final Profile profile;
    private final AbstractScrollableElement parent;
    private int IlllIllIlIIIIlIIlIIllIIIl = 0;
    private final Identifier deleteIcon = CheatBreaker.asset("icons/delete-64.png");
    private final Identifier arrowIcon = CheatBreaker.asset("icons/right.png");
    private final Identifier pencilIcon = CheatBreaker.asset("icons/pencil-64.png");

    public ProfileElement(AbstractScrollableElement parent, int n, Profile profile, float f) {
        super(f);
        this.parent = parent;
        this.IllIIIIIIIlIlIllllIIllIII = n;
        this.profile = profile;
    }

    @Override
    public void handleDrawElement(GuiGraphicsExtractor gfx, int mouseX, int mouseY, float partialTicks) {
        boolean bl;
        boolean bl2;
        float f2;
        boolean bl3 = mouseX > this.x + 12 && this.isMouseInside(mouseX, mouseY, false);
        int n3 = 75;
        RenderUtil.drawRect(gfx, this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, 0x2F2F2F2F);
        if (bl3) {
            if (this.IlllIllIlIIIIlIIlIIllIIIl < n3) {
                f2 = CBModulesGui.getSmoothFloat(790);
                this.IlllIllIlIIIIlIIlIIllIIIl = (int)((float)this.IlllIllIlIIIIlIIlIIllIIIl + f2);
                if (this.IlllIllIlIIIIlIIlIIllIIIl > n3) {
                    this.IlllIllIlIIIIlIIlIIllIIIl = n3;
                }
            }
        } else if (this.IlllIllIlIIIIlIIlIIllIIIl > 0) {
            f2 = CBModulesGui.getSmoothFloat(790);
            this.IlllIllIlIIIIlIIlIIllIIIl = (float)this.IlllIllIlIIIIlIIlIIllIIIl - f2 < 0.0f ? 0 : (int)((float)this.IlllIllIlIIIIlIIlIIllIIIl - f2);
        }
        if (this.IlllIllIlIIIIlIIlIIllIIIl > 0) {
            f2 = (float)this.IlllIllIlIIIIlIIlIIllIIIl / (float)n3 * (float)100;
            RenderUtil.drawRect(gfx, this.x + 12, ((float)this.y + ((float)this.height - (float)this.height * f2 / (float)100)), this.x + this.width - (this.profile.isEditable() ? 0 : 30), this.y + this.height, this.IllIIIIIIIlIlIllllIIllIII);
        }
        boolean bl4 = (float) mouseX > (float)this.x * this.scale && (float) mouseX < (float)(this.x + 12) * this.scale && (float) mouseY >= (float)(this.y + this.yOffset) * this.scale && (float) mouseY <= (float)(this.y + this.height / 2 + this.yOffset) * this.scale;
        boolean bl5 = (float) mouseX > (float)this.x * this.scale && (float) mouseX < (float)(this.x + 12) * this.scale && (float) mouseY > (float)(this.y + this.height / 2 + this.yOffset) * this.scale && (float) mouseY < (float)(this.y + this.height + this.yOffset) * this.scale;
        int color = CheatBreaker.getColor(0.0f, 0.0f, 0.0f, 0.1919192f * 1.8236842f);
        float f3 = 6.571429f * 0.38043478f;
        if (this.profile.isEditable()) {
            bl2 = false;
            bl = false;
            ProfilesListElement parent = (ProfilesListElement)this.parent;
            if (parent.profileElementList.indexOf(this) != 0 && parent.profileElementList.indexOf(this) > 1) {
                bl2 = true;
//                GL11.glPushMatrix();
                gfx.pose().pushMatrix();
                if (bl4) {
                    color = CheatBreaker.getColor(0.0f, 0.0f, 0.0f, 0.14444444f * 4.5f);
                }
//                GL11.glTranslatef((float)(this.x + 6) - f3, (float)this.y + (float)5, 0.0f);
//                GL11.glRotatef(-90, 0.0f, 0.0f, 1.0f);
                gfx.pose().translate((float)(this.x + 6) - f3, (float) this.y + 7.0F);
                gfx.pose().rotate((float) Math.toRadians(-90));
                RenderUtil.drawIcon(gfx, this.arrowIcon, f3, (float)-1, 0.0f, color);
//                GL11.glPopMatrix();
//                GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0952381f * 0.3195652f);
                color = CheatBreaker.getColor(0.0f, 0.0f, 0.0f, 1.0952381f * 0.3195652f);
                gfx.pose().popMatrix();
            }
            if (parent.profileElementList.indexOf(this) != parent.profileElementList.size() - 1) {
                bl = true;
//                GL11.glPushMatrix();
                gfx.pose().pushMatrix();
                if (bl5) {
//                    GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.2112676f * 0.5366279f);
                    color = CheatBreaker.getColor(0.0f, 0.0f, 0.0f, 1.2112676f * 0.5366279f);
                }
//                GL11.glTranslatef((float)(this.x + 6) + f3, (float)this.y + (float)7, 0.0f);
//                GL11.glRotatef(90, 0.0f, 0.0f, 1.0f);
                gfx.pose().translate((float)(this.x + 6) + f3, (float)this.y + 7.0f);
                gfx.pose().rotate((float) Math.toRadians(90));
                RenderUtil.drawIcon(gfx, this.arrowIcon, f3, 2.0f, 0.0f, color);
//                GL11.glPopMatrix();
                gfx.pose().popMatrix();
            }
            if (!bl2 && !bl) {
                RenderUtil.drawIcon(gfx, this.arrowIcon, 1.173913f * 2.1296296f, (float)(this.x + 4), (float)this.y + (float)6, color);
            }
        } else {
            RenderUtil.drawIcon(gfx, this.arrowIcon, 6.6666665f * 0.375f, (float)(this.x + 4), (float)this.y + (float)6, color);
        }
        if (CheatBreaker.getInstance().getActiveProfile() == this.profile) {
//            CheatBreaker.getInstance().playBold18px.drawString(this.profile.getName().toUpperCase(), (float)this.x + (float)16, (float)(this.y + 4), -818991313);
            RenderUtil.drawString(gfx, Fonts.playBold18, this.profile.getName().toUpperCase(), (float)this.x + 16.0f, (float)(this.y + 7), -818991313);
        } else {
//            CheatBreaker.getInstance().playRegular16px.drawString(this.profile.getName().toUpperCase(), (float)this.x + (float)16, (float)this.y + 4, -818991313);
            RenderUtil.drawString(gfx, Fonts.playRegular16, this.profile.getName().toUpperCase(), (float)this.x + 16.0f, (float)this.y + 4, -818991313);
        }
        if (CheatBreaker.getInstance().getActiveProfile() == this.profile) {
//            CheatBreaker.getInstance().playRegular14px.drawString(" (Active)", (float)this.x + (float)17 + (float) CheatBreaker.getInstance().playBold18px.getStringWidth(this.profile.getName().toUpperCase()), (float)this.y + (float)7, 0x6F2F2F2F);
            RenderUtil.drawString(gfx, Fonts.playRegular14, " (Active)", (float)this.x + 17.0f + (float) Fonts.playBold18.width(this.profile.getName().toUpperCase()), (float)this.y + 6.0f, 0x6F2F2F2F);
        }
        if (this.profile.isEditable()) {
            bl2 = (float) mouseX > (float)(this.x + this.width - 30) * this.scale && (float) mouseX < (float)(this.x + this.width - 13) * this.scale && (float) mouseY > (float)(this.y + this.yOffset) * this.scale && (float) mouseY < (float)(this.y + this.height + this.yOffset) * this.scale;
            int color2 = CheatBreaker.getColor(bl2 ? 0.0f : 1.1707317f * 0.21354167f, bl2 ? 0.0f : 0.101648346f * 2.4594595f, bl2 ? 0.48876402f * 1.0229886f : 0.5647059f * 0.4427083f, 0.5675676f * 1.145238f);
            RenderUtil.drawIcon(gfx, this.pencilIcon, (float)5, (float)(this.x + this.width - 26), (float)this.y + 5.1916666f * 0.6741573f, color2);
            bl = (float) mouseX > (float)(this.x + this.width - 17) * this.scale && (float) mouseX < (float)(this.x + this.width - 2) * this.scale && (float) mouseY > (float)(this.y + this.yOffset) * this.scale && (float) mouseY < (float)(this.y + this.height + this.yOffset) * this.scale;
            int color3 = CheatBreaker.getColor(bl ? 1.4181818f * 0.5641026f : 0.96875f * 0.2580645f, bl ? 0.0f : 0.17553192f * 1.4242424f, bl ? 0.0f : 15.250001f * 0.016393442f, 0.44444445f * 1.4625f);
            RenderUtil.drawIcon(gfx, this.deleteIcon, (float)5, (float)(this.x + this.width - 13), (float)this.y + 0.7653061f * 4.5733333f, color3);
        }
    }

    @Override
    public void onClick(int mouseX, int mouseY, int button) {
        boolean bl = (float) mouseX > (float)(this.x + this.width - 17) * this.scale && (float) mouseX < (float)(this.x + this.width - 2) * this.scale && (float) mouseY > (float)(this.y + this.yOffset) * this.scale && (float) mouseY < (float)(this.y + this.height + this.yOffset) * this.scale;
        boolean bl2 = (float) mouseX > (float)(this.x + this.width - 30) * this.scale && (float) mouseX < (float)(this.x + this.width - 13) * this.scale && (float) mouseY > (float)(this.y + this.yOffset) * this.scale && (float) mouseY < (float)(this.y + this.height + this.yOffset) * this.scale;
        boolean bl3 = (float) mouseX > (float)this.x * this.scale && (float) mouseX < (float)(this.x + 12) * this.scale && (float) mouseY >= (float)(this.y + this.yOffset) * this.scale && (float) mouseY <= (float)(this.y + this.height / 2 + this.yOffset) * this.scale;
        boolean bl4 = (float) mouseX > (float)this.x * this.scale && (float) mouseX < (float)(this.x + 12) * this.scale && (float) mouseY > (float)(this.y + this.height / 2 + this.yOffset) * this.scale && (float) mouseY < (float)(this.y + this.height + this.yOffset) * this.scale;
        ProfilesListElement list = (ProfilesListElement)this.parent;
        if (this.profile.isEditable() && (bl3 || bl4)) {
            if (bl3 && ((ProfilesListElement)this.parent).profileElementList.indexOf(this) != 0 && ((ProfilesListElement)this.parent).profileElementList.indexOf(this) > 1) {
                CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
                this.profile.setIndex(list.profileElementList.indexOf(this) - 1);
                list.profileElementList.get((int)(list.profileElementList.indexOf((Object)this) - 1)).profile.setIndex(list.profileElementList.indexOf(this));
                Collections.swap(list.profileElementList, list.profileElementList.indexOf(this), list.profileElementList.indexOf(this) - 1);
            }
            if (bl4 && list.profileElementList.indexOf(this) != list.profileElementList.size() - 1) {
                CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
                this.profile.setIndex(list.profileElementList.indexOf(this) + 1);
                list.profileElementList.get((int)(list.profileElementList.indexOf((Object)this) + 1)).profile.setIndex(list.profileElementList.indexOf(this));
                Collections.swap(list.profileElementList, list.profileElementList.indexOf(this), list.profileElementList.indexOf(this) + 1);
            }
        } else if (this.profile.isEditable() && bl) {
            File file;
            File file2;
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            if (CheatBreaker.getInstance().getActiveProfile() == this.profile) {
                CheatBreaker.getInstance().setActiveProfile(CheatBreaker.getInstance().getProfiles().getFirst());
                CheatBreaker.getInstance().getConfigManager().readProfile(CheatBreaker.getInstance().getActiveProfile().getName());
                CheatBreaker.getInstance().getModuleManager().keyStrokes.initialize();
            }
            if (this.profile.isEditable() && (file2 = (file = ConfigManager.profilesDir).exists() || file.mkdirs() ? new File(file + File.separator + this.profile.getName().toLowerCase() + ".json") : null).exists() && file2.delete()) {
                CheatBreaker.getInstance().getProfiles().removeIf(ilIIlIIlIIlllIlIIIlIllIIl -> ilIIlIIlIIlllIlIIIlIllIIl == this.profile);
                list.profileElementList.removeIf(iIlIlllllIIIlIIllIllIlIlI -> iIlIlllllIIIlIIllIllIlIlI == this);
            }
        } else if (this.profile.isEditable() && bl2) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            Minecraft.getInstance().setScreen(new CBProfileCreateGui(this.profile, CBModulesGui.instance, (ProfilesListElement)this.parent, this.IllIIIIIIIlIlIllllIIllIII, this.scale));
        } else if (CheatBreaker.getInstance().getActiveProfile() != this.profile) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            CheatBreaker.getInstance().getConfigManager().writeProfile(CheatBreaker.getInstance().getActiveProfile().getName());
            CheatBreaker.getInstance().setActiveProfile(this.profile);
            CheatBreaker.getInstance().getConfigManager().readProfile(CheatBreaker.getInstance().getActiveProfile().getName());
            CheatBreaker.getInstance().getModuleManager().keyStrokes.initialize();
        }
    }
}