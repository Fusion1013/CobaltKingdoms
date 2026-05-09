package se.fusion1013.cobaltKingdoms.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.config.quest.QuestConfig;
import se.fusion1013.cobaltKingdoms.config.town.TownConfig;

import java.io.File;
import java.io.IOException;

public class KingdomsConfig extends Manager<CobaltKingdoms> {

    private static YamlConfiguration config;

    private static final TownConfig townConfig = TownConfig.init();
    private static final QuestConfig questConfig = QuestConfig.init();

    public KingdomsConfig(CobaltKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        CobaltKingdoms plugin = CobaltKingdoms.getInstance();
        File file = new File(plugin.getDataFolder(), "config.yml");

        if (!file.exists()) {
            plugin.getLogger().warning("config.yml not found, creating default file!");
            plugin.saveResource("config.yml", false);
        }

        config = new YamlConfiguration();
        config.options().parseComments(true);

        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void disable() {

    }

    private static KingdomsConfig INSTANCE;

    public static KingdomsConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new KingdomsConfig(CobaltKingdoms.getInstance());
        }
        return INSTANCE;
    }

    private static void throwKeyNotFoundError(String key) {
        throw new IllegalArgumentException("Config key not found: " + key);
    }

    public static YamlConfiguration getConfig() {
        return config;
    }

    public static TownConfig getTownConfig() {
        return townConfig;
    }

    public static QuestConfig getQuestConfig() {
        return questConfig;
    }

    public static int getInt(String key) {
        if (!config.contains(key)) {
            throwKeyNotFoundError(key);
        }
        return config.getInt(key);
    }

    public static float getFloat(String key) {
        if (!config.contains(key))
            throwKeyNotFoundError(key);
        return (float) config.getDouble(key);
    }

    public static boolean getBool(String key) {
        if (!config.contains(key))
            throwKeyNotFoundError(key);
        return config.getBoolean(key);
    }
}
