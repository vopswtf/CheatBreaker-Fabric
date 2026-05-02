package cc.vops.cheatbreaker.client.module.type.notification;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.event.type.GuiDrawEvent;
import cc.vops.cheatbreaker.client.event.type.KeepAliveEvent;
import cc.vops.cheatbreaker.client.event.type.MenuDrawEvent;
import cc.vops.cheatbreaker.client.event.type.WindowTickEvent;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.ui.overlay.SocialOverlayScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CBNotificationsModule extends AbstractModule
{
    public long time;
    private List<Notification> notifications;

    public CBNotificationsModule() {
        super("Notifications");
        this.time = System.currentTimeMillis();
        this.notifications = new ArrayList<>();
        this.addEvent(KeepAliveEvent.class, this::onKeepAlive);
        this.addEvent(WindowTickEvent.class, this::onTick);
        this.addEvent(GuiDrawEvent.class, this::onDraw);
        this.addEvent(MenuDrawEvent.class, this::onDrawMenu);
        this.isEditable = false;
        this.setDefaultState(true);
    }

    private void onKeepAlive(final KeepAliveEvent time) {
        this.time = System.currentTimeMillis();
    }

    private long lastUpdateTime = System.currentTimeMillis();

    private void onTick(final WindowTickEvent event) {
        long currentTime = System.currentTimeMillis();
        long delta = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;

        Iterator<Notification> iterator = this.notifications.iterator();
        while (iterator.hasNext()) {
            Notification notification = iterator.next();
            notification.update(delta);

            if (notification.startTime + notification.duration <= currentTime) {
                int notificationBottom = notification.notificationBottom;
                for (Notification otherNotification : this.notifications) {
                    if (otherNotification.notificationBottom < notification.notificationBottom) {
                        otherNotification.framesAlive = 0;
                        otherNotification.notificationTop = notificationBottom;
                        notificationBottom = otherNotification.notificationBottom;
                    }
                }
                iterator.remove();
            }
        }
    }

    private void onDraw(final GuiDrawEvent event) {
        GuiGraphicsExtractor gfx = event.getGraphics();
        gfx.pose().pushMatrix();

        for (Notification notification : this.notifications) {
            notification.render(gfx);
        }

        gfx.pose().scale(CheatBreaker.getScaleFactor(), CheatBreaker.getScaleFactor());
        SocialOverlayScreen.getInstance().renderGameOverlay(gfx);

        if (CheatBreaker.getInstance().getGlobalSettings().pinRadio.getAsBoolean() && !(minecraft.screen instanceof SocialOverlayScreen)) {
            SocialOverlayScreen.getInstance().getRadioElement().drawElement(event.getGraphics(), -1, -1, true);
        }

        gfx.pose().popMatrix();
    }

    public void onDrawMenu(final MenuDrawEvent event) {
        GuiGraphicsExtractor gfx = event.getGraphics();
        gfx.pose().pushMatrix();

        for (Notification notification : this.notifications) {
            notification.render(gfx);
        }

        gfx.pose().scale(CheatBreaker.getScaleFactor(), CheatBreaker.getScaleFactor());
        SocialOverlayScreen.getInstance().renderGameOverlay(gfx);

        gfx.pose().popMatrix();
    }

    public void queueNotification(final String type, String content, long duration) {
        if (duration < 2000L) duration = 2000L;
        content = content.replaceAll("&([abcdefghijklmrABCDEFGHIJKLMNR0-9])|(&$)", "§$1");
        final String lowerCase = type.toLowerCase();
        CBNotificationType resolvedType;
        switch (lowerCase) {
            case "info": {
                resolvedType = CBNotificationType.INFO;
                break;
            }
            case "error": {
                resolvedType = CBNotificationType.ERROR;
                break;
            }
            default: {
                resolvedType = CBNotificationType.DEFAULT;
                break;
            }
        }
        final Notification notification = new Notification(this, resolvedType, content, duration);
        int notificationTop = notification.notificationTop - notification.notificationHeight - 2;
        for (int i = this.notifications.size() - 1; i >= 0; --i) {
            final Notification otherNotification = this.notifications.get(i);
            otherNotification.framesAlive = 0;
            otherNotification.notificationTop = notificationTop;
            notificationTop -= 2 + otherNotification.notificationHeight;
        }
        this.notifications.add(notification);
    }
}
