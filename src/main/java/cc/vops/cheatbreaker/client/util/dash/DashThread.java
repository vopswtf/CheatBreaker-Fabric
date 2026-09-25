package cc.vops.cheatbreaker.client.util.dash;

import cc.vops.cheatbreaker.CheatBreaker;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import org.endlesssource.mediainterface.SystemMediaFactory;

import java.time.Duration;
import java.time.LocalDateTime;

public class DashThread extends Thread {
    @Override
    public void run() {
        while (CheatBreaker.getInstance().isEnabled()) {
            try {
                while (true) {
                    if (!Minecraft.getInstance().isRunning()) return;
                    if (CheatBreaker.getInstance().getRadioManager() == null) continue;
                    if (CheatBreaker.getInstance().getRadioManager().getLocalStation() instanceof LocalStation ls) {
                        if (ls.getMedia() == null) {
                            try {
                                ls.setMedia(SystemMediaFactory.createSystemInterface());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    Station current = CheatBreaker.getInstance().getRadioManager().getCurrentStation();
                    if (current != null) {
                        if (current instanceof LocalStation) {
                            current.getData();
                        } else if(Station.getStartTime() != null && (Duration.between(Station.getStartTime(), LocalDateTime.now()).toMillis() / 1000L) >= (long)(current.getDuration() + 2)) {
                            current.getData();
                            Thread.sleep(4000L);
                        }
                    }
                    Thread.sleep(1000L);
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
                continue;
            }
        }
    }
}