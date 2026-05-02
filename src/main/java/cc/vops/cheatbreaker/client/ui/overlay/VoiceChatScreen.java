package cc.vops.cheatbreaker.client.ui.overlay;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.audio.voicechat.VoiceChannel;
import cc.vops.cheatbreaker.client.audio.voicechat.VoiceUser;
import cc.vops.cheatbreaker.client.ui.AbstractGui;
import cc.vops.cheatbreaker.client.ui.mainmenu.element.GradientTextButton;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import com.google.common.collect.Lists;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class VoiceChatScreen extends AbstractGui {

    private static CheatBreaker cheatBreaker = CheatBreaker.getInstance();
    private List<GradientTextButton> channelButtons;
    private GradientTextButton joinChannelButton;
    private GradientTextButton undeafenButton;
    private VoiceChannel voiceChannel = null;
    private Identifier headphonesImage = CheatBreaker.asset("icons/headphones.png");
    private Identifier speakerImage = CheatBreaker.asset("icons/speaker.png");
    private Identifier mutedSpeakerImage = CheatBreaker.asset("icons/speaker-mute.png");
    private Identifier microphoneImage = CheatBreaker.asset("icons/microphone-64.png");

    @Override
    public void initMenu() {
        this.drawBackground = true;

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            if (!cheatBreaker.getVoiceChatManager().isVoiceChatEnabled()) {
                cheatBreaker.getVoiceChatManager().setVoiceChatEnabled(true);
                cheatBreaker.getVoiceChatManager().getVoiceChannels().add(new VoiceChannel(UUID.randomUUID(), "General"));
                cheatBreaker.getVoiceChatManager().getVoiceChannels().add(new VoiceChannel(UUID.randomUUID(), "Gaming"));
                cheatBreaker.getVoiceChatManager().getVoiceChannels().add(new VoiceChannel(UUID.randomUUID(), "Another Channel"));
                cheatBreaker.getVoiceChatManager().getVoiceChannels().add(new VoiceChannel(UUID.randomUUID(), "xDuke Cape Discussion"));
                cheatBreaker.getVoiceChatManager().setVoiceChannel(cheatBreaker.getVoiceChatManager().getVoiceChannels().get(0));
            }
        }

        if (cheatBreaker.getVoiceChatManager().isVoiceChatEnabled() && cheatBreaker.getVoiceChatManager().getVoiceChannels() != null) {
            this.voiceChannel = cheatBreaker.getVoiceChatManager().getVoiceChannel();
            boolean bl = cheatBreaker.getVoiceChatManager().getVoiceUsers().contains(this.mc.getUser().getProfileId());
            this.joinChannelButton = new GradientTextButton("Join Channel");
            this.undeafenButton = new GradientTextButton(bl ? "Un-deafen" : "Deafen");
            this.channelButtons = new ArrayList<>();
            float f = 16;
            float f2 = this.scaledWidth / (float)8;
            float f3 = this.scaledHeight / 2.0f - (float)8 - f * (float)cheatBreaker.getVoiceChatManager().getVoiceChannels().size() / 2.0f;
            int n = 0;
            for (VoiceChannel channel : cheatBreaker.getVoiceChatManager().getVoiceChannels()) {
                GradientTextButton button = new GradientTextButton(channel.getChannelName());
                this.channelButtons.add(button);
                button.setElementSize(f2, f3 + (float)12 + f * (float)n, (float)110, 12);
                if (this.voiceChannel.getChannelName().equals(channel.getChannelName())) {
                    button.applySelectedColorState();
                }
                ++n;
            }
        }
    }

    @Override
    public boolean doBlur() {
        return true;
    }

    @Override
    public void drawMenu(GuiGraphicsExtractor gfx, float f, float f2, float delta) {
        float f3 = this.scaledWidth / 8f;
        if (cheatBreaker.getVoiceChatManager().isVoiceChatEnabled() && cheatBreaker.getVoiceChatManager().getVoiceChannel() != null) {
            float f4 = 16;
            float f5 = this.scaledHeight / 2.0f - (float)8 - f4 * (float)cheatBreaker.getVoiceChatManager().getVoiceChannels().size() / 2.0f;
            RenderUtil.drawString(gfx, Fonts.playBold18, "VOICE CHAT", f3, f5 - (float)4, -1);
            this.undeafenButton.setElementSize(f3 + (float)60, f5 - (float)4, (float)50, 12);
            this.undeafenButton.drawElement(gfx, f, f2, true);
            this.channelButtons.forEach(channelButton -> {
                if (this.getVoiceChannel(channelButton.getText()) == cheatBreaker.getVoiceChatManager().getVoiceChannel()) {
                    channelButton.draw(gfx, f, f2, true);
                    float xPos = channelButton.getX();
                    float yPos = channelButton.getY();
                    RenderUtil.drawIcon(gfx, CheatBreaker.asset("icons/microphone-64.png"), xPos + (float)4, yPos + 2.0f, (float)8, 8);
                } else if (Objects.equals(this.voiceChannel.getChannelName(), channelButton.getText())) {
                    channelButton.draw(gfx, f, f2, true);
                } else {
                    channelButton.draw(gfx, f, f2, true);
                }
            });
            if (this.voiceChannel != null) {
                this.drawVoiceChannel(gfx, f, f2, f3 + (float)130, CheatBreaker.getScaledHeight() / 2.0f);
            }
        } else {
            float f6 = this.scaledHeight / 2.0f;
            RenderUtil.drawCenteredString(gfx, Fonts.playBold18, "VOICE CHAT IS NOT SUPPORTED", this.scaledWidth / 4f, f6, -1);
        }
    }

    @Override
    protected boolean onMouseClicked(double f, double f2, int button) {
        if (this.channelButtons == null) return false;
        for (GradientTextButton channelButton : this.channelButtons) {
            VoiceChannel voiceChannel;
            if (!channelButton.isMouseInside(f, f2) || this.voiceChannel == (voiceChannel = this.getVoiceChannel(channelButton.getText()))) continue;
            if (voiceChannel != null) {
                this.voiceChannel.getVoiceUsers().removeIf(voiceUser -> voiceUser.getUUID().equals(this.mc.getUser().getProfileId()));
                this.voiceChannel.getListeningList().removeIf(uuid -> uuid.equals(this.mc.getUser().getProfileId()));
            }

            for (GradientTextButton otherChannelButton : this.channelButtons) {
                if (this.voiceChannel == cheatBreaker.getVoiceChatManager().getVoiceChannel() || !otherChannelButton.getText().equals(this.voiceChannel.getChannelName())) continue;
                otherChannelButton.applyDefaultColorState();
            }
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            voiceChannel.getListeningList().add(this.mc.getUser().getProfileId());
            voiceChannel.getVoiceUsers().add(new VoiceUser(this.mc.getUser().getProfileId(), this.mc.getUser().getName()));
            this.voiceChannel = voiceChannel;
            if (this.voiceChannel == cheatBreaker.getVoiceChatManager().getVoiceChannel()) continue;
            channelButton.applyLighterColorState();
        }
        if (this.voiceChannel != null) {
            if (this.joinChannelButton.isMouseInside(f, f2)) {
                CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
//                cheatBreaker.getVoiceChatManager().sendPacketToQueue(new PacketVoiceMute(this.voiceChannel.getUUID()));
                for (GradientTextButton channelButton : this.channelButtons) {
                    channelButton.applyDefaultColorState();
                }
                for (GradientTextButton channelButton : this.channelButtons) {
                    if (!channelButton.getText().equals(this.voiceChannel.getChannelName())) continue;
                    channelButton.applySelectedColorState();
                }
            }
            if (this.undeafenButton.isMouseInside(f, f2)) {
                UUID iterator = this.mc.getUser().getProfileId();
                CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
//                cheatBreaker.getVoiceChatManager().sendPacketToQueue(new PacketVoiceMute(iterator));
                if (!cheatBreaker.getVoiceChatManager().getVoiceUsers().removeIf(uuid2 -> uuid2.equals(iterator))) {
                    cheatBreaker.getVoiceChatManager().getVoiceUsers().add(iterator);
                }
                this.undeafenButton.setText((cheatBreaker.getVoiceChatManager().getVoiceUsers().contains(this.mc.getUser().getProfileId())) ? "Un-deafen" : "Deafen");
            }
            this.lIIIIIIIIIlIllIIllIlIIlIl((float) f, (float) f2, this.scaledWidth / (float)8 + (float)130, CheatBreaker.getScaleFactor() / 2.0f);
        }

        return false;
    }

    @Override
    protected void onMouseReleased(double mx, double my, int button) {

    }

    /*
     * Iterators could be improved
     */
    private void drawVoiceChannel(GuiGraphicsExtractor gfx, float f, float f2, float f3, float f4) {
        f4 /= CheatBreaker.getScaleFactor();

        float f5 = 14;
        float f6 = (float)this.voiceChannel.getUsers().size() * f5;
        RenderUtil.drawString(gfx, Fonts.playBold18, this.voiceChannel.getChannelName(), f3, (f4 -= f6 / 2.0f) - (float)14, -1);
        if (!this.lIIlIlIllIIlIIIlIIIlllIII()) {
            this.joinChannelButton.setElementSize(f3 + (float)125, f4 - (float)14, (float)50, 12);
            this.joinChannelButton.drawElement(gfx, f, f2, true);
        }
        RenderUtil.drawRect(gfx, f3, f4, f3 + (float)175, f4 + f6, -1626337264);
        int n = 0;
        ArrayList<VoiceUser> arrayList = Lists.newArrayList(this.voiceChannel.getUsers());
        arrayList.sort((voiceUser, voiceUser2) -> {
            if (this.voiceChannel.isListening(voiceUser.getUUID()) && !this.voiceChannel.isListening(voiceUser2.getUUID())) {
                return -1;
            }
            if (!this.voiceChannel.isListening(voiceUser.getUUID()) && this.voiceChannel.isListening(voiceUser2.getUUID())) {
                return 1;
            }
            return 0;
        });
        for (VoiceUser user : arrayList) {
            boolean bl = this.voiceChannel.isListening(user.getUUID());
            boolean bl2 = cheatBreaker.getVoiceChatManager().getVoiceUsers().contains(user.getUUID());
            float f7 = f4 + (float)n * f5;
            float f8 = f3;
            boolean bl3 = f > f3 + (float)158 && f < f3 + (float)184 && f2 > f7 && f2 < f7 + f5;
            if (!bl) {
                RenderUtil.drawIcon(gfx, this.headphonesImage, f8 + (float)4, f7 + (float)3, (float)8, 8);
            } else {
                RenderUtil.drawIcon(gfx, this.microphoneImage, f8 + (float)4, f7 + (float)3, (float)8, 8);
            }
            f8 = f3 + (float)10;
            if (!user.getUUID().equals(this.mc.getUser().getProfileId())) {
                if (bl2) {
                    RenderUtil.drawIcon(gfx, this.mutedSpeakerImage, f3 + (float)162, f7 + (float)3, (float)8, 8, CheatBreaker.getColor(1.0f, 1.4848485f * 0.06734694f, 4.9f * 0.020408163f, bl3 ? 1.0f : 0.8117647f * 0.73913044f));
                } else {
                    RenderUtil.drawIcon(gfx, this.speakerImage, f3 + (float)162, f7 + (float)3, (float)8, 8, CheatBreaker.getColor(1.0f, 1.0f, 1.0f, bl3 ? 1.0f : 0.11904762f * 5.04f));
                }
            }
            RenderUtil.drawString(gfx, Fonts.playBold18, user.getUsername().toUpperCase(), f8 + (float)6, f7 + 2.0f, bl ? -1 : 0x6FFFFFFF);
            ++n;
        }
    }

    private void lIIIIIIIIIlIllIIllIlIIlIl(float f, float f2, float f3, float f4) {
        float f5 = 14;
        float f6 = (float)this.voiceChannel.getUsers().size() * f5;
        f4 -= f6 / 2.0f;
        int n = 0;
        for (VoiceUser voiceUser : this.voiceChannel.getUsers()) {
            boolean bl;
            float f7 = f4 + (float)n * f5;
            boolean bl2 = bl = f > f3 + (float)158 && f < f3 + (float)184 && f2 > f7 && f2 < f7 + f5;
            if (!voiceUser.getUUID().equals(this.mc.getUser().getProfileId()) && bl) {
                CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
//                cheatBreaker.getVoiceChatManager().sendPacketToQueue(new PacketVoiceMute(voiceUser.getUUID()));
                if (!cheatBreaker.getVoiceChatManager().getVoiceUsers().removeIf(uUID -> uUID.equals(voiceUser.getUUID()))) {
                    cheatBreaker.getVoiceChatManager().getVoiceUsers().add(voiceUser.getUUID());
                }
            }
            ++n;
        }
    }

    public void keyTyped(char c, int n) {
//        super.handleKeyTyped(c, n);
//        // && lIllIllIlIIllIllIlIlIIlIl.IIIIllIIllIIIIllIllIIIlIl() ???????
//        if (n == 25) {
//            this.mc.displayGuiScreen(null);
//            this.mc.setIngameFocus();
//        }
    }

    private VoiceChannel getVoiceChannel(String string) {
        for (VoiceChannel voiceChannel : cheatBreaker.getVoiceChatManager().getVoiceChannels()) {
            if (!voiceChannel.getChannelName().equals(string)) continue;
            return voiceChannel;
        }
        return null;
    }

    private boolean lIIlIlIllIIlIIIlIIIlllIII() {
        return this.voiceChannel == cheatBreaker.getVoiceChatManager().getVoiceChannel();
    }
}
