package se.fusion1013.cobaltKingdoms.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

import java.io.File;
import java.io.IOException;

public abstract class AbstractConfig {

    protected final YamlConfiguration config;

    protected AbstractConfig(String name) {
        this.config = getConfigFile(name);
    }

    protected static YamlConfiguration getConfigFile(String name) {
        CobaltKingdoms plugin = CobaltKingdoms.getInstance();
        File file = new File(plugin.getDataFolder(), name + ".yml");

        if (!file.exists()) {
            plugin.getLogger().warning(name + ".yml not found, creating default file!");
            plugin.saveResource(name + ".yml", false);
        }

        YamlConfiguration config = new YamlConfiguration();
        config.options().parseComments(true);

        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }

        return config;
    }

    protected int getInt(String key, int defaultValue) {
        if (!config.contains(key)) return defaultValue;
        return config.getInt(key);
    }

    protected double getDouble(String key, double defaultValue) {
        if (!config.contains(key)) return defaultValue;
        return config.getDouble(key);
    }

}
