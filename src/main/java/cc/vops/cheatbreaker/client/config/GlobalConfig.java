package cc.vops.cheatbreaker.client.config;

import java.util.HashMap;
import java.util.Map;

public class GlobalConfig {
    public String activeProfile = "default";
    public Map<String, Integer> profileIndexes = new HashMap<>();
    public Map<String, Object> settings = new HashMap<>();
}
