package cc.vops.cheatbreaker;

import cc.vops.cheatbreaker.client.audio.AudioDevice;
import cc.vops.cheatbreaker.client.audio.voicechat.VoiceChatManager;
import cc.vops.cheatbreaker.client.config.ConfigManager;
import cc.vops.cheatbreaker.client.config.GlobalSettings;
import cc.vops.cheatbreaker.client.config.Profile;
import cc.vops.cheatbreaker.client.event.EventBus;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import cc.vops.cheatbreaker.client.module.ModuleManager;
import cc.vops.cheatbreaker.client.nethandler.apollo.ApolloNetHandler;
import cc.vops.cheatbreaker.client.ui.module.CBModulePlaceGui;
import cc.vops.cheatbreaker.client.ui.module.CBModulesGui;
import cc.vops.cheatbreaker.client.util.cosmetic.Cosmetic;
import cc.vops.cheatbreaker.client.util.Sounds;
import cc.vops.cheatbreaker.client.util.cosmetic.CosmeticModels;
import cc.vops.cheatbreaker.client.util.cosmetic.EmoteManager;
import cc.vops.cheatbreaker.client.util.dash.CBDashManager;
import cc.vops.cheatbreaker.client.util.friend.FriendsManager;
import cc.vops.cheatbreaker.client.util.friend.Status;
import cc.vops.cheatbreaker.client.websocket.AssetsWebSocket;
import com.mojang.logging.LogUtils;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import org.slf4j.Logger;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.*;

@Getter
public class CheatBreaker implements ModInitializer {
    @Getter
    private static CheatBreaker instance;
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final AudioFormat universalAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000.0f, 16, 1, 2, 16000.0f, false);

    private long startTime;
    private List<Profile> profiles = new ArrayList<>();
    @Setter
    private Profile activeProfile;
    private GlobalSettings globalSettings;
    private ModuleManager moduleManager;
    private EventBus eventBus;
    private ConfigManager configManager;
    private VoiceChatManager voiceChatManager;
    private CBDashManager radioManager;
    private FriendsManager friendsManager;
    private EmoteManager emoteManager;

    @Setter private Status status = Status.ONLINE;
    @Setter private long lastOnline = System.currentTimeMillis(); // for hidden status

    private ApolloNetHandler apolloNetHandler;

    private final List<Cosmetic> cosmetics = new ArrayList<>();
    private AssetsWebSocket assetsWebSocket;

    private final List<Identifier> presetLocations = new ArrayList<>();
    private final List<String> consoleLines = new ArrayList<>();

    public final static byte[] processBytesAuth = "KANYE WEST 2025".getBytes();

    @Setter
    private boolean consoleAllowed = true;
    @Setter
    private boolean acceptingFriendRequests = true;

    @Override
    public void onInitialize() {
        instance = this;
        this.startTime = System.currentTimeMillis();
        Sounds.registerSounds();
        Minecraft.getInstance().execute(CosmeticModels::bakeModels);
    }

    public void onLoad() {
        this.initAudioDevices();
        if (!audioDevices.isEmpty()) {
            LOGGER.info("Initialized all audio devices.");
            this.voiceChatManager = new VoiceChatManager(audioDevices.getFirst());
            LOGGER.info("Created Voice Chat Manager");
        } else {
            this.voiceChatManager = new VoiceChatManager();
            LOGGER.info("Couldn't load audio devices.");
        }

        createDefaultConfigPresets();

        globalSettings = new GlobalSettings();
        LOGGER.info("Created Settings");

        eventBus = new EventBus();
        LOGGER.info("Created EventBus");

        moduleManager = new ModuleManager();
        LOGGER.info("Created ModuleManager");

        configManager = new ConfigManager();
        emoteManager = new EmoteManager();

        radioManager = new CBDashManager();
        LOGGER.info("Created DashManager");

        this.loadProfiles();
        CheatBreaker.LOGGER.info("Loaded " + this.profiles.size() + " custom profiles");
        (this.configManager = new ConfigManager()).read();

        apolloNetHandler = new ApolloNetHandler();

        this.friendsManager = new FriendsManager();
    }

    public static Identifier asset(String path) {
        return Identifier.fromNamespaceAndPath("cheatbreaker", path);
    }

    private void createDefaultConfigPresets() {
        File dir = ConfigManager.profilesDir;
        this.presetLocations.add(asset("presets/Preset 1.json"));

        if (dir.exists() || dir.mkdirs()) {
            for (Identifier Identifier : presetLocations) {
                File dest = new File(dir, new File(Identifier.getPath()).getName());
                if (!dest.exists()) {
                    try (InputStream stream = Minecraft.getInstance().getResourceManager().open(Identifier)) {
                        Files.copy(stream, dest.toPath());
                    } catch (IOException e) {
                        LOGGER.error("Failed copying preset {}", Identifier, e);
                    }
                }
            }
        }
    }

    public Cosmetic getActiveCosmetic(Cosmetic.CosmeticType type, UUID playerId) {
        for (Cosmetic cosmetic : this.cosmetics) {
            if (cosmetic.getType() == type && cosmetic.isEquipped() && cosmetic.getPlayerId().equals(playerId.toString())) {
                return cosmetic;
            }
        }
        return null;
    }

    public void createNewProfile() {
        if (this.activeProfile == this.profiles.getFirst()) {
            final Profile profile = new Profile(this.getNewProfileName("Profile 1"), true);
            this.activeProfile = profile;
            this.profiles.add(profile);
            this.configManager.write();
        }
    }

    private String getNewProfileName(final String base) {
        final File dir = ConfigManager.profilesDir;
        if (dir.exists() || dir.mkdirs()) {
            if (new File(dir, base + ".json").exists()) {
                return this.getNewProfileName(base + "1");
            }
        }
        return base;
    }

    public void connectToAssetsServer() {
        try {
            final Map<String, String> hashMap = new HashMap<>();
            hashMap.put("username", Minecraft.getInstance().getUser().getName());
            hashMap.put("playerId", Minecraft.getInstance().getUser().getProfileId().toString());
            hashMap.put("version", FabricLoader.getInstance().getModContainer("cheatbreaker").orElseThrow().getMetadata().getVersion().getFriendlyString());
            hashMap.put("status", (status == Status.HIDDEN ? lastOnline : status.ordinal()) + "");
            this.assetsWebSocket = new AssetsWebSocket(new URI("wss://cheatbreaker.vops.cc"), hashMap);

            Minecraft.getInstance().getProfileKeyPairManager().prepareKeyPair().whenComplete((keyPair, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    return;
                }

                this.assetsWebSocket.connect();
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void loadProfiles() {
        this.profiles.add(new Profile("default", false));
        final File dir = ConfigManager.profilesDir;
        final File[] files;
        if (dir.exists() && dir.isDirectory() && (files = dir.listFiles()) != null) {
            for (final File file : files) {
                if (file.getName().endsWith(".json")) {
                    this.profiles.add(new Profile(file.getName().replace(".json", ""), true));
                }
            }
        }
    }

    // Audio devices
    private List<AudioDevice> audioDevices = new ArrayList<>();
//    private final VoiceChatManager voiceChatManager;

    private void initAudioDevices() {
        final Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        for (final Mixer.Info info : mixers) {
            final Mixer mixer = AudioSystem.getMixer(info);
            try {
                TargetDataLine dataLine = (TargetDataLine) mixer.getLine(new DataLine.Info(TargetDataLine.class, CheatBreaker.universalAudioFormat));
                if (info != null) {
                    this.audioDevices.add(new AudioDevice(info.getDescription(), info.getName(), dataLine));
                }
            } catch (final IllegalArgumentException | LineUnavailableException ignored) {
                // the device was not a microphone.
            }
        }
    }

    public String[] getAudioDeviceList() {
        final String[] audioDevices = new String[this.audioDevices.size()];
        int var1 = 0;
        for (final Iterator<AudioDevice> var2 = this.audioDevices.iterator(); var2.hasNext(); ++var1) {
            final AudioDevice var3 = var2.next();
            audioDevices[var1] = var3.getDescriptor();
        }
        return audioDevices;
    }


    public boolean isUsingStaffModules() {
        for (final AbstractModule cbModule : this.moduleManager.staffModules) {
            if (cbModule.isStaffEnabledModule()) {
                return true;
            }
        }
        return false;
    }

    public static int getScaledWidth() {
        return Minecraft.getInstance().getWindow().getGuiScaledWidth();
    }

    public static int getScaledHeight() {
        return Minecraft.getInstance().getWindow().getGuiScaledHeight();
    }

    public static float getScaleFactor() {
        int scale = Minecraft.getInstance().getWindow().getGuiScale();
        return 1f / (scale >= 4 ? 2f : scale == 3 ? 1.5f : scale == 1 ? 0.5f : 1f);
    }

    public static int getColor(float r, float g, float b, float a) {
        return ((int)(a * 255) << 24) | ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);
    }

    public static void playSound(Holder<SoundEvent> sound) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, 1.0F));
    }

    public static boolean isInModuleScreen() {
        return Minecraft.getInstance().screen != null && (Minecraft.getInstance().screen instanceof CBModulesGui || Minecraft.getInstance().screen instanceof CBModulePlaceGui);
    }

    public String getPluginMessageChannel() {
        return "CB-Client";
    }

    public String getPluginBinaryChannel() {
        return "CB-Binary";
    }

    public static String getVersion() {
        return FabricLoader.getInstance()
                .getModContainer("cheatbreaker")
                .orElseThrow()
                .getMetadata()
                .getVersion()
                .getFriendlyString();
    }


    public String getStatusString() {
        String s;
        switch (this.getStatus()) {
            case AWAY: {
                s = "Away";
                break;
            }
            case BUSY: {
                s = "Busy";
                break;
            }
            case HIDDEN: {
                s = "Hidden";
                break;
            }
            default: {
                s = "Online";
                break;
            }
        }
        return s;
    }
}
