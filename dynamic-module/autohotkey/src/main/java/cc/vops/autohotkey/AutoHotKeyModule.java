package cc.vops.autohotkey;

import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.module.AbstractModule;

import java.util.ArrayList;
import java.util.List;

public class AutoHotKeyModule extends AbstractModule {
    public static AutoHotKeyModule instance;
    public List<Setting> hotkeys = new ArrayList<>();

    public AutoHotKeyModule() {
        super("Auto Text Hotkey");
        this.setDefaultState(false);
        this.setPreviewLabel("/team rally", 1.4F);
        this.getSettingsList().clear();

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
