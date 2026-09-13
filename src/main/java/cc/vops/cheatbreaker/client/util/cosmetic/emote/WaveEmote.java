package cc.vops.cheatbreaker.client.util.cosmetic.emote;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.util.cosmetic.keyframe.KeyframeEmoteData;
import cc.vops.cheatbreaker.client.util.cosmetic.keyframe.KeyframeEmote;

public class WaveEmote extends KeyframeEmote {
    public WaveEmote() {
        super(CheatBreaker.getInstance().getEmoteManager().getEmoteData("Wave"));
    }
}
