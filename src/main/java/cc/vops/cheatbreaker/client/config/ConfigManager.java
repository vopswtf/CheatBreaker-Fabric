package cc.vops.cheatbreaker.client.config;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.module.AbstractModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ConfigManager {

    public static final File configDir;
    public static final File profilesDir;
    private static final File globalConfigFile;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    static {
        configDir = new File(Minecraft.getInstance().gameDirectory, "cheatbreaker/config");
        profilesDir = new File(configDir, "profiles");
        globalConfigFile = new File(configDir, "global.json");

        configDir.mkdirs();
        profilesDir.mkdirs();
    }

    // ---------------------------------------------------------------------
    // PUBLIC API
    // ---------------------------------------------------------------------

    public void write() {
        createRequiredFiles();
        writeGlobalConfig();
        writeProfile(CheatBreaker.getInstance().getActiveProfile().getName());
    }

    public void read() {
        createRequiredFiles();
        readGlobalConfig();

        if (CheatBreaker.getInstance().getActiveProfile() == null &&
                !CheatBreaker.getInstance().getProfiles().isEmpty()) {
            CheatBreaker.getInstance().setActiveProfile(
                    CheatBreaker.getInstance().getProfiles().getFirst()
            );
        }

        readProfile(CheatBreaker.getInstance().getActiveProfile().getName());
    }

    public static File getConfigFile(String name) {
        return new File(profilesDir, name + ".json");
    }

    // ---------------------------------------------------------------------
    // GLOBAL CONFIG
    // ---------------------------------------------------------------------

    private void writeGlobalConfig() {
        GlobalConfig config = new GlobalConfig();

        if (CheatBreaker.getInstance().getActiveProfile() != null) {
            config.activeProfile = CheatBreaker.getInstance().getActiveProfile().getName();
        }

        CheatBreaker.getInstance().getProfiles().forEach(p ->
                config.profileIndexes.put(p.getName(), p.getIndex())
        );

        CheatBreaker.getInstance().getGlobalSettings().settingsList.forEach(s -> {
            if (!s.getLabel().equalsIgnoreCase("label")) {
                config.settings.put(s.getLabel(), s.getValue());
            }
        });

        writeJson(globalConfigFile, config);
    }

    private void readGlobalConfig() {
        if (!globalConfigFile.exists()) {
            writeGlobalConfig();
            return;
        }

        GlobalConfig config = readJson(globalConfigFile, GlobalConfig.class);
        if (config == null) return;

        // set profile indexes
        config.profileIndexes.forEach((profileName, index) -> {
            CheatBreaker.getInstance().getProfiles().forEach(p -> {
                if (p.getName().equalsIgnoreCase(profileName)) {
                    p.setIndex(index);
                }
            });
        });

        // set active profile
        CheatBreaker.getInstance().getProfiles().forEach(p -> {
            if (p.getName().equalsIgnoreCase(config.activeProfile) && p.isEditable()) {
                CheatBreaker.getInstance().setActiveProfile(p);
            }
        });

        // global settings
        CheatBreaker.getInstance().getGlobalSettings().settingsList.forEach(setting -> {
            if (setting.getLabel().equalsIgnoreCase("label")) return;
            Object value = config.settings.get(setting.getLabel());
            if (value != null) applySetting(setting, value);
        });
    }

    // ---------------------------------------------------------------------
    // PROFILE CONFIG
    // ---------------------------------------------------------------------

    public void writeProfile(String profileName) {
        if (profileName.equalsIgnoreCase("default")) return;

        File profileFile = new File(profilesDir, profileName + ".json");

        ProfileConfig config = new ProfileConfig();

        for (AbstractModule module : CheatBreaker.getInstance().getModuleManager().modules) {
            ProfileConfig.ModuleEntry entry = new ProfileConfig.ModuleEntry();

            entry.state = module.isEnabled();
            entry.renderHud = module.isRenderHud();
            entry.position = module.getGuiAnchor();
            entry.xTranslation = module.getXTranslation();
            entry.yTranslation = module.getYTranslation();

            module.getSettingsList().forEach(s -> {
                if (!s.getLabel().equals("label")) {
                    entry.settings.put(s.getLabel(), s.getValue());
                }
            });

            config.modules.put(module.getName(), entry);
        }

        writeJson(profileFile, config);
    }

    public void readProfile(String profileName) {
        if (profileName.equalsIgnoreCase("default")) {
            loadDefaultProfile();
            return;
        }

        File profileFile = new File(profilesDir, profileName + ".json");
        if (!profileFile.exists()) {
            writeProfile(profileName);
            return;
        }

        ProfileConfig config = readJson(profileFile, ProfileConfig.class);
        if (config == null) return;

        for (AbstractModule module : CheatBreaker.getInstance().getModuleManager().modules) {
            ProfileConfig.ModuleEntry entry = config.modules.get(module.getName());
            if (entry == null) continue;

            module.setState(entry.state);
            module.setRenderHud(entry.renderHud);

            if (entry.position != null)
                module.setAnchor(entry.position);

            module.setTranslations(entry.xTranslation, entry.yTranslation);

            module.getSettingsList().forEach(s -> {
                if (s.getLabel().equals("label")) return;
                Object value = entry.settings.get(s.getLabel());
                if (value != null) applySetting(s, value);
            });
        }
    }

    // ---------------------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------------------

    private boolean createRequiredFiles() {
        try {
            configDir.mkdirs();
            profilesDir.mkdirs();

            if (!globalConfigFile.exists())
                return globalConfigFile.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void loadDefaultProfile() {
        CheatBreaker.getInstance().getModuleManager().modules.forEach(module -> {
            module.setState(module.defaultState);
            module.setAnchor(module.defaultGuiAnchor);
            module.setTranslations(module.defaultXTranslation, module.defaultYTranslation);
            module.setRenderHud(module.defaultRenderHud);

            for (int i = 0; i < module.getSettingsList().size(); i++) {
                try {
                    module.getSettingsList().get(i).setValue(
                            module.getDefaultSettingsValues().get(i), false
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void applySetting(Setting setting, Object value) {
        try {
            switch (setting.getType()) {
                case BOOLEAN -> setting.setValue((Boolean) value);
                case INTEGER -> setting.setValue(((Number) value).intValue());
                case FLOAT -> setting.setValue(((Number) value).floatValue());
                case DOUBLE -> setting.setValue(((Number) value).doubleValue());
                case STRING, STRING_ARRAY -> setting.setValue(value.toString());
            }
        } catch (Exception ignored) {}
    }

    private <T> T readJson(File file, Class<T> clazz) {
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    private void writeJson(File file, Object obj) {
        try {
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                gson.toJson(obj, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
