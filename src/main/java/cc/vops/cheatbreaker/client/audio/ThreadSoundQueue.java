package cc.vops.cheatbreaker.client.audio;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.audio.voicechat.VoiceChatManager;
import net.minecraft.client.Minecraft;

public class ThreadSoundQueue implements Runnable
{
    private final VoiceChatManager sndManager;
    private final Object notifier;

    public ThreadSoundQueue(final VoiceChatManager sndManager) {
        this.notifier = new Object();
        this.sndManager = sndManager;
    }

    @Override
    public void run() {
        while (CheatBreaker.getInstance().isEnabled()) {
            if (!this.sndManager.queue.isEmpty()) {
//                final Datalet data = this.sndManager.queue.poll();
//                if (data == null) {
//                    continue;
//                }
//                final boolean end = data.data == null;
//                if (this.sndManager.newDatalet(data) && !end) {
//                    this.sndManager.createStream(data);
//                }
//                else if (end) {
//                    this.sndManager.giveEnd(data.id);
//                }
//                else {
//                    this.sndManager.giveStream(data);
//                }
            }
            else {
                try {
                    synchronized (this) {
                        this.wait();
                    }
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}