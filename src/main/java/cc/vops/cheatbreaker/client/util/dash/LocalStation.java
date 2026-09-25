package cc.vops.cheatbreaker.client.util.dash;

import cc.vops.cheatbreaker.CheatBreaker;
import lombok.Getter;
import lombok.Setter;
import org.endlesssource.mediainterface.SystemMediaFactory;
import org.endlesssource.mediainterface.api.SystemMediaInterface;

public class LocalStation extends Station {
    @Getter @Setter
    private SystemMediaInterface media;

    public LocalStation() {
        super("Computer", "", "This PC", "", "");
    }

    @Getter
    private boolean isPlaying = false;
    private long pos;

    @Override
    public void getData() {
        if (this.media != null) {
            media.getActiveSession().ifPresentOrElse(session -> {
                this.setName(session.getApplicationName().replaceAll("\\.[^.]+$", ""));

                session.getNowPlaying().ifPresentOrElse(now -> {
                    String title = now.getTitle().orElse("");
                    String artist = now.getArtist().orElse("");

                    this.setTitle(title);
                    this.setArtist(artist);

                    if (now.getArtwork().isPresent()) {
                        this.setCoverURL(now.getArtwork().get());

                        if (this.currentResource != null && !("songs/" + this.getTitle()).equals(this.currentResource.getPath())) {
                            this.previousResource = this.currentResource;
                            this.currentResource = null;
                        }
                    }

                    if (now.getPosition().isPresent()) {
                        long newPos = now.getPosition().get().toMillis() / 1000;
                        if (pos != newPos) {
                            pos = newPos;
                            isPlaying = true;
                        } else {
                            isPlaying = false;
                        }
                    }

                    this.setDuration(now.getDuration().map(d -> (int) (d.toMillis() / 1000)).orElse(0));
                }, () -> {
                    isPlaying = false;
                    this.setTitle("No media playing");
                    this.setArtist("");
                    this.setCoverURL("");
                });
            }, () -> {
                isPlaying = false;
                this.setName("Media Player");
                this.setTitle("No active media session");
                this.setArtist("");
                this.setCoverURL("");
            });
        }
    }

    @Override
    public void playStream() {
        if (isPlaying) return;
        if (this.media != null) {
            media.getActiveSession().ifPresent(session -> {
                session.getControls().play();
                this.isPlaying = true;
            });
        }
    }

    @Override
    public void endStream() {
        if (this.media != null) {
            media.getActiveSession().ifPresent(session -> {
                session.getNowPlaying().ifPresent(now -> {
                    session.getControls().pause();
                    this.isPlaying = false;
                });
            });
        }
    }

    @Override
    public boolean isPlay() {
        return isPlaying && CheatBreaker.getInstance().getRadioManager().getCurrentStation() == this;
    }
}
