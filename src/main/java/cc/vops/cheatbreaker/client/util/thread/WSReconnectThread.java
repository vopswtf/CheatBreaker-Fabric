package cc.vops.cheatbreaker.client.util.thread;

import cc.vops.cheatbreaker.CheatBreaker;

public class WSReconnectThread extends Thread {
    private final long delay = 10000L;

    @Override
    public void run() {
        try {
            if (!interrupted()) {
                Thread.sleep(this.delay);
                CheatBreaker.LOGGER.info("[CB WS] Attempting reconnect.");
                CheatBreaker.getInstance().connectToAssetsServer();
            }
            if (CheatBreaker.getInstance().getAssetsWebSocket().isOpen()) {
                interrupt();
            }
        }
        catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }

}
