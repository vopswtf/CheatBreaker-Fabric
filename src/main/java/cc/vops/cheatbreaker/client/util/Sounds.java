package cc.vops.cheatbreaker.client.util;

import cc.vops.cheatbreaker.CheatBreaker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

import java.util.HashMap;

public class Sounds {
    public static final HashMap<String, SoundEvent> SOUNDS = new HashMap<>();

    public static void registerSounds() {
        register("message");
        register("short_whoosh1");
        register("shutter");
        register("voice_down");
        register("voice_up");
    }

    private static void register(String name) {
        Identifier id = CheatBreaker.asset(name);
        SoundEvent soundEvent = Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
        SOUNDS.put(name, soundEvent);
    }

    public static void playSound(String name) {
        if (CheatBreaker.getInstance().getGlobalSettings().muteCheatBreakerSounds.getAsBoolean()) return;
        playSound(name, 1.0f, 1.0f);
    }

    public static void playSound(String name, float volume, float pitch) {
        SoundEvent soundEvent = SOUNDS.get(name);
        if (soundEvent != null) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(soundEvent, volume, pitch));
        } else {
            System.err.println("Sound not found: " + name);
            Thread.dumpStack();
        }
    }
}
