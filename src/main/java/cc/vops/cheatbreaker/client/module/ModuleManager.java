package cc.vops.cheatbreaker.client.module;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.audio.voicechat.VoiceChat;
import cc.vops.cheatbreaker.client.module.type.*;
import cc.vops.cheatbreaker.client.module.type.armorstatus.ArmorStatusModule;
import cc.vops.cheatbreaker.client.module.type.keystrokes.KeystrokesModule;
import cc.vops.cheatbreaker.client.module.type.minimap.MiniMapModule;
import cc.vops.cheatbreaker.client.module.type.notification.CBNotificationsModule;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    public List<AbstractModule> modules;
    public List<AbstractModule> staffModules;
    public AbstractModule llIIlllIIIIlllIllIlIlllIl;

    public ToggleSprintModule toggleSprint;
    public MiniMapModule minmap;
    public CBNotificationsModule notifications;
    public ArmorStatusModule armourStatus;
    public ScoreboardModule scoreboard;
    public Object xray;
    public PotionStatusModule potionStatus;
    public BossBarModule bossBar;
    public DirectionHudModule directionHud;
    public KeystrokesModule keyStrokes;
    public FPSModule fpsModule;
    public CPSModule cpsModule;
    public CoordinatesModule coordinatesModule;
    public VoiceChat voiceChat;
    public TeammatesModule teammatesModule;

    private static final List<Class<? extends AbstractModule>> pendingRegistration = new ArrayList<>();

    public ModuleManager() {
        modules = new ArrayList<>();
        staffModules = new ArrayList<>();

        modules.add(notifications = new CBNotificationsModule());
        modules.add(coordinatesModule = new CoordinatesModule());
        modules.add(minmap = new MiniMapModule());
        modules.add(toggleSprint = new ToggleSprintModule());
        modules.add(potionStatus = new PotionStatusModule());
        modules.add(armourStatus = new ArmorStatusModule());
        modules.add(keyStrokes = new KeystrokesModule());
        modules.add(scoreboard = new ScoreboardModule());
        modules.add(directionHud = new DirectionHudModule());
        modules.add(bossBar = new BossBarModule());
        modules.add(cpsModule = new CPSModule());
        modules.add(fpsModule = new FPSModule());
        this.voiceChat = new VoiceChat();
        this.teammatesModule = new TeammatesModule();
//
//        staffModules.add(xray = new XRayModule());
        for (Class<? extends AbstractModule> moduleClass : new ArrayList<>(pendingRegistration)) {
            CheatBreaker.LOGGER.info("Registering Dynamic Module: {}", moduleClass.getName());

            try {
                modules.add(moduleClass.getConstructor().newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        pendingRegistration.clear();
    }

    public static void registerModule(Class<? extends AbstractModule> moduleClass) {
        if (CheatBreaker.getInstance() == null || CheatBreaker.getInstance().getModuleManager() == null) {
            pendingRegistration.add(moduleClass);
            return;
        }

        try {
            AbstractModule module = moduleClass.getConstructor().newInstance();
            CheatBreaker.getInstance().getModuleManager().modules.add(module);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
