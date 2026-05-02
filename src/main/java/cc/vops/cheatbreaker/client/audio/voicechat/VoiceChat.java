package cc.vops.cheatbreaker.client.audio.voicechat;


import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.event.type.GameTickEvent;
import cc.vops.cheatbreaker.client.event.type.GuiDrawEvent;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.PlayerHeads;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import cc.vops.cheatbreaker.client.util.Sounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VoiceChat {
    private final Minecraft minecraft = Minecraft.getInstance();
    private final CheatBreaker cheatbreaker = CheatBreaker.getInstance();
    private final Identifier microphoneIcon = CheatBreaker.asset("icons/microphone-64.png");
    private final Map<VoiceUser, Long> userLastSpoken = new HashMap<>();
    private boolean isTalking;
    public boolean checkMicVolume;

    public VoiceChat() {
        CheatBreaker.getInstance().getEventBus().addEvent(GuiDrawEvent.class, this::onRender);
        CheatBreaker.getInstance().getEventBus().addEvent(GameTickEvent.class, this::onTick);
    }

    public void addUserToSpoken(UUID uuid) {
        VoiceUser voiceUser = this.cheatbreaker.getVoiceChatManager().getVoiceUser(uuid);
        if (voiceUser != null && !voiceUser.getUsername().equals(Minecraft.getInstance().getUser().getName())) {
            this.userLastSpoken.put(voiceUser, System.currentTimeMillis() + 250L);
        }
    }

    public void onRender(GuiDrawEvent event) {
        GuiGraphicsExtractor gfx = event.getGraphics();
        if (minecraft.player == null) return;
        if (this.cheatbreaker.getVoiceChatManager().isVoiceChatEnabled() && this.cheatbreaker.getVoiceChatManager().getVoiceChannels() != null && (!this.userLastSpoken.isEmpty() || this.isTalking)) {
            float f = 20;
            float f2 = (float)CheatBreaker.getScaledWidth() - (float)120;
            float[] arrf = new float[]{10};

            if (this.isTalking) {
                this.renderHeadAndName(gfx, this.minecraft.player.getDisplayName().getString(), this.minecraft.player.getUUID(), f2, arrf[0], true);
                arrf[0] = arrf[0] + f;
            }

            this.userLastSpoken.forEach((voiceUser, l) -> {
                this.renderHeadAndName(gfx, voiceUser.getUsername(), voiceUser.getUUID(), f2, arrf[0], false);
                arrf[0] = arrf[0] + f;
            });
        }
    }

    private void renderHeadAndName(GuiGraphicsExtractor gfx, String string, UUID uuid, float f, float f2, boolean isSelf) {
        if (isSelf) {
            RenderUtil.drawCorneredGradientRectWithOutline(gfx, f, f2, f + (float)110, f2 + (float)18, -11493284, -10176146, -11164318);
        } else {
            RenderUtil.drawCorneredGradientRectWithOutline(gfx, f, f2, f + (float)110, f2 + (float)18, -1356454362, -1355664846, -1356191190);
        }
        Identifier identifier = PlayerHeads.getHeadLocation(string, uuid);
        RenderUtil.drawIcon(gfx, identifier, 7.0f, f + 2.0f, f2 + 2.0f);
        RenderUtil.drawString(gfx, Fonts.playRegular16, string, f + (float)22, f2 + (float)4, -1);
    }

    public void onTick(GameTickEvent event) {
        if (!this.userLastSpoken.isEmpty()) {
            ArrayList<VoiceUser> arrayList = new ArrayList<>();
            for (Map.Entry<VoiceUser, Long> entry : this.userLastSpoken.entrySet()) {
                if (System.currentTimeMillis() - entry.getValue() < 0L) continue;
                arrayList.add(entry.getKey());
            }
            arrayList.forEach(this.userLastSpoken::remove);
        }
        if (!this.cheatbreaker.getVoiceChatManager().isVoiceChatEnabled() && this.cheatbreaker.getVoiceChatManager().getVoiceChannels() != null) {
            return;
        }
        if (this.isTalking && !this.minecraft.isWindowActive()) {
            this.isTalking = false;
            CheatBreaker.getInstance().getVoiceChatManager().setTalking(false);
            Sounds.playSound("voice_up");
        }
        boolean isDown = CheatBreaker.getInstance().getGlobalSettings().pushToTalk.isDown();
        if (!this.isTalking && this.minecraft.isWindowActive() && isDown) {
            this.isTalking = true;
            if (checkMicVolume && CheatBreaker.getInstance().getGlobalSettings().microphoneVolume.getAsInteger() < 10) {
                CheatBreaker.getInstance().getModuleManager().notifications.queueNotification("info", "Your microphone is muted.", 3000L);
            } else {
                checkMicVolume = false;
                CheatBreaker.getInstance().getVoiceChatManager().setTalking(true);
                Sounds.playSound("voice_down");
            }
        } else if (this.isTalking && this.minecraft.isWindowActive() && !isDown) {
            this.isTalking = false;
            CheatBreaker.getInstance().getVoiceChatManager().setTalking(false);
            Sounds.playSound("voice_up");
        }
    }
}
