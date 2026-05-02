package cc.vops.cheatbreaker.client.util.dash;

import java.util.List;

public class CBDashManager {

    private final List<Station> stations = DashUtil.get();
    private final DashQueueThread dashQueueThread = new DashQueueThread();
    private final DashThread dashThread;
    private Station station;

    public CBDashManager() {
        this.dashQueueThread.start();
        this.dashThread = new DashThread();
        this.dashThread.start();
        if (!this.stations.isEmpty()) {
            this.station = this.stations.getFirst();
            this.dashQueueThread.offerStation(this.station);
        }
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
 