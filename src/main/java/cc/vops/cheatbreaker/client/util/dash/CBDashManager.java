package cc.vops.cheatbreaker.client.util.dash;

import lombok.Getter;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class CBDashManager {

    private final List<Station> stations = new CopyOnWriteArrayList<>();
    private final DashQueueThread dashQueueThread = new DashQueueThread();
    private final DashThread dashThread;
    private Station station;
    private final LocalStation localStation = new LocalStation();

    public CBDashManager() {
        this.dashQueueThread.setDaemon(true);
        this.dashQueueThread.start();
        this.stations.addFirst(this.localStation);
        this.station = this.localStation;
        this.dashThread = new DashThread();
        this.dashThread.setDaemon(true);
        this.dashThread.start();
        this.dashQueueThread.offerStation(this.station);

        CompletableFuture.supplyAsync(DashUtil::get).thenAccept(stations::addAll);
    }

    public void setStation(Station station) {
        boolean wasPlaying = this.station != null && this.station.isPlay();
        if (wasPlaying) this.station.endStream();
        this.station = station;
        if (wasPlaying) this.station.playStream();
    }

    public void endStream() {
        if (this.station != null) {
            this.station.endStream();
        }
    }

    public List<Station> getStations() {
        return this.stations;
    }

    public DashQueueThread getDashQueueThread() {
        return this.dashQueueThread;
    }

    public DashThread getDashThread() {
        return this.dashThread;
    }

    public Station getCurrentStation() {
        return this.station;
    }

    public void setCurrentStation(Station station) {
        this.station = station;
    }
}
 