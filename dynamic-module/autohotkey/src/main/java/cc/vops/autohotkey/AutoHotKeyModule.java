package cc.vops.autohotkey;

import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.module.AbstractModule;

import java.util.ArrayList;
import java.util.List;

public class AutoHotKeyModule extends AbstractModule {
    // use singleton from constructor
    public static AutoHotKeyModule instance;

    public List<Setting> hotkeys = new ArrayList<>();

    // make sure module is empty constructor
    public AutoHotKeyModule() {
        super("Auto Text Hotkey");
        this.setDefaultState(false); // Default state of module
        this.setPreviewLabel("/team rally", 1.4F); // Preview label, use setPreviewIcon for an icon instead (Identifier)
        this.getSettingsList().clear(); // This removes the scale setting that is added by default, not required if you aren't rendering any GUI

        for (int i = 0; i < 10; i++) {
            Setting hotkey = new Setting(this, "Hotkey " + (i + 1)).setValue("/Command").setKeyCode(0);
            hotkey.setEditableString(true);
            hotkey.setDisplayName("Hotkey " + (i + 1));
            this.hotkeys.add(hotkey);
        }

        instance = this;
    }

    public static AutoHotKeyModule getInstance() {
        return instance;
    }
}
