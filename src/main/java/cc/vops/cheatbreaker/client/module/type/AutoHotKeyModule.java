package cc.vops.cheatbreaker.client.module.type;

import cc.vops.cheatbreaker.client.config.Setting;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.util.bridge.GuiBridge;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;

import java.util.ArrayList;
import java.util.List;

// this contains some documentation incase anyone else wants to make a module but its not the best lol
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
            Setting hotkey = new Setting(this, "Hotkey " + (i + 1)).setValue("/command").setUnboundKeyCode().setAllowMouseKeybinding(true);
            hotkey.setEditableString(true);
            hotkey.setDisplayName("Hotkey " + (i + 1));
            this.hotkeys.add(hotkey);
        }

        instance = this;
    }

    public void keyPress(int keycode) {
        if (minecraft.player == null) return;
        if (GuiBridge.getScreen() != null) return;
        if (this.isEnabled()) {
            for (Setting hotkey : AutoHotKeyModule.getInstance().hotkeys) {
                if (!hotkey.isKeyCodeSet()) return;
                if (keycode == hotkey.getKeyCode()) {
                    // not sure if this is reliable, maybe swap with connection.sendCommand
                    if (hotkey.getAsString().startsWith("/")) {
                        minecraft.player.connection.send(new ServerboundChatCommandPacket(hotkey.getAsString().substring(1)));
                    } else {
                        minecraft.player.connection.sendChat(hotkey.getAsString());
                    }
                }
            }
        }
    }

    // instance based because leftover from dynamic modules
    public static AutoHotKeyModule getInstance() {
        return instance;
    }
}
