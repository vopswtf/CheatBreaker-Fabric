package cc.vops.cheatbreaker.client.config;

import cc.vops.cheatbreaker.client.ui.module.GuiAnchor;

import java.util.HashMap;
import java.util.Map;

public class ProfileConfig {
    public static class ModuleEntry {
        public boolean state;
        public boolean renderHud;
        public GuiAnchor position;
        public float xTranslation;
        public float yTranslation;
        public Map<String, Object> settings = new HashMap<>();
        public Map<String, Integer> keybindings = new HashMap<>();
    }

    public Map<String, ModuleEntry> modules = new HashMap<>();
}