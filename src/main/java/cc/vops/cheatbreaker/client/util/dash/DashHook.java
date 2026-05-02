package cc.vops.cheatbreaker.client.util.dash;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.util.cbagent.CBAgentResources;
import javazoom.jl.decoder.JavaLayerHook;
import javazoom.jl.decoder.JavaLayerUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class DashHook implements JavaLayerHook {
    public InputStream getResourceAsStream(String string) {
        Class<JavaLayerUtils> class_ = JavaLayerUtils.class;
        InputStream inputStream = class_.getResourceAsStream(string);
        if (inputStream == null) {
            String string2 = "javazoom/jl/decoder/" + string;
            CheatBreaker.LOGGER.info("Retrieving: " + string2);
            if (CBAgentResources.existsBytes(string2)) {
                inputStream = new ByteArrayInputStream(CBAgentResources.getBytesNative(string2));
            }
        }
        return inputStream;
    }
}