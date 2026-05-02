package cc.vops.cheatbreaker.client.module.type.notification;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.util.font.CBFontRenderer;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;

class Notification {
    public CBNotificationType type;
    public String content;
    public long duration;
    public long startTime = System.currentTimeMillis();
    public int notificationBottom;
    public int notificationTop;
    public int notificationHeight;
    public int framesAlive = 0;
    final CBNotificationsModule notificationsModule;

    Notification(CBNotificationsModule notificationsModule, CBNotificationType type, String content, long duration) {
        this.notificationsModule = notificationsModule;
        this.type = type;
        this.content = content;
        this.duration = duration;
        this.notificationHeight = type == CBNotificationType.DEFAULT ? 16 : 20;
        this.notificationTop = (CheatBreaker.getScaledHeight() - 14 - this.notificationHeight);
        this.notificationBottom = (CheatBreaker.getScaledHeight() + this.notificationHeight);
    }

    public void update(long deltaTimeMs) {
        float speed = 0.3f;
        float moveAmount = deltaTimeMs * speed;

        if (this.notificationTop != -1) {
            if (this.notificationBottom > this.notificationTop) {
                this.notificationBottom = (int) Math.max(this.notificationBottom - moveAmount, this.notificationTop);
                if (this.notificationBottom == this.notificationTop)
                    this.notificationTop = -1;
            } else if (this.notificationBottom < this.notificationTop) {
                this.notificationBottom = (int) Math.min(this.notificationBottom + moveAmount, this.notificationTop);
                if (this.notificationBottom == this.notificationTop)
                    this.notificationTop = -1;
            } else {
                this.notificationTop = -1;
            }
        }
    }

    public void render(GuiGraphicsExtractor gfx) {
        CBFontRenderer font = Fonts.playRegular16;
        int n = CheatBreaker.getScaledWidth();
        int n2 = this.notificationBottom;
        float contentWidth = font.width(this.content);
        int n3 = (int)(this.type == CBNotificationType.DEFAULT ? contentWidth + (float)10 : contentWidth + (float)30);

        RenderUtil.drawRect(gfx, n - 5 - n3, n2, n - 4, n2 + this.notificationHeight, -1358954496);

        switch (this.type) {
            case ERROR:
                RenderUtil.drawIcon(gfx, CheatBreaker.asset("icons/error-64.png"), 6f, (float)(n - 10 - n3 + 9), (float)(n2 + 4));
                RenderUtil.drawRect(gfx, (int) ((n - 10) - contentWidth - 4.5f), n2 + 4, (int) ((n - 10) - contentWidth - 4f), n2 + this.notificationHeight - 4, 0xFFFFFFFF);
                break;
            case INFO:
                RenderUtil.drawIcon(gfx, CheatBreaker.asset("icons/info-64.png"), 6f, (float)(n - 10 - n3 + 9), (float)(n2 + 4), 0xFFFFFFA6);
                RenderUtil.drawRect(gfx, (int) ((n - 10) - contentWidth - 4.5f), n2 + 4, (int) ((n - 10) - contentWidth - 4f), n2 + this.notificationHeight - 4, 0xFFFFFFFF);
                break;
        }

        long millisElapsed = Mth.clamp(this.duration - (this.startTime + this.duration - System.currentTimeMillis()), 0L, this.duration);
        float millisElapsedDisplay = contentWidth * ((float)millisElapsed / (float)this.duration * 100f / 100f);

        RenderUtil.drawRect(gfx, (int)(n - 10f - contentWidth), (int) (n2 + this.notificationHeight - 4.4f), (int)(n - 10f - contentWidth + contentWidth), (int) (n2 + this.notificationHeight - 4f), 0x30666666);
        RenderUtil.drawRect(gfx, (int)(n - 10f - contentWidth), (int) (n2 + this.notificationHeight - 4.4f), (int)(n - 10f - contentWidth + millisElapsedDisplay), (int) (n2 + this.notificationHeight - 4f), -1878982912);

        RenderUtil.drawString(gfx, font, this.content, (int) ((n - 10) - contentWidth), n2 + (this.type == CBNotificationType.DEFAULT ? 2 : 4), -1);
    }
}
