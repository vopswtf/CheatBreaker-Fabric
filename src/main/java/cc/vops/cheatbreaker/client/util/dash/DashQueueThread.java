package cc.vops.cheatbreaker.client.util.dash;

import net.minecraft.client.Minecraft;

import java.util.LinkedList;
import java.util.Queue;

public class DashQueueThread extends Thread {
    private final Queue<Station> queue = new LinkedList<>();
    private volatile boolean running = true;

    public void run() {
        try {
            while(running) {
                if (!Minecraft.getInstance().isRunning()) return;
                synchronized(this.queue) {
                    this.queue.wait();
                    Station station = this.queue.poll();
                    if (station != null) {
                        station.getData();
                    }
                }
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }
    }

    public void offerStation(Station station) {
        synchronized(this.queue) {
            this.queue.offer(station);
            this.queue.notify();
        }
    }

    public void shutdown() {
        running = false;
        synchronized (queue) {
            queue.notifyAll();
        }
        this.interrupt();
    }
}