package se.fusion1013.cobaltKingdoms.town.config;

import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltKingdoms.config.AbstractConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TownConfig extends AbstractConfig {

    private int townMinSpacing;
    private int townVerificationTimerTicks;
    private int townVerificationTimerSeconds;
    private int questSpawnDelaySeconds;
    private int townSpawnDistance;

    private final Map<Integer, TownLevelConfig> levels = new HashMap<>();

    private TownConfig() {
        super("town_config");
        loadValues(config);
    }

    private void loadValues(YamlConfiguration config) {
        townMinSpacing = getInt("town_min_spacing", 250);
        townVerificationTimerTicks = getInt("town_verification_timer_t", 3);
        townVerificationTimerSeconds = getInt("town_verification_timer_s", 3);
        questSpawnDelaySeconds = getInt("quest_spawn_delay_s", 60);
        townSpawnDistance = getInt("town_spawn_distance", 60);

        List<Map<?, ?>> levelList = config.getMapList("levels");
        levelList.forEach(this::addTownLevel);
    }

    private void addTownLevel(Map<?, ?> levelConfig) {
        TownLevelConfig townLevelConfig = new TownLevelConfig(levelConfig);
        levels.put(townLevelConfig.getLevel(), townLevelConfig);
    }

    public int getTownMinSpacing() {
        return townMinSpacing;
    }

    public int getTownVerificationTimer() {
        return townVerificationTimerSeconds * 20 + townVerificationTimerTicks;
    }

    public int getQuestSpawnDelaySeconds() {
        return questSpawnDelaySeconds;
    }

    public int getNextLevelXpThreshold(int currentLevel) {
        int totalXp = 0;
        for (int i = 0; i < currentLevel + 2; i++) {
            TownLevelConfig townLevelConfig = levels.get(i);
            if (townLevelConfig == null) continue;
            totalXp += townLevelConfig.getXpThreshold();
        }
        return totalXp;
    }

    public TownLevelConfig getTownLevelConfig(int xp) {
        int totalXp = 0;
        TownLevelConfig previousTownLevelConfig = levels.get(0);

        for (int i = 0; i < levels.size(); i++) {
            TownLevelConfig townLevelConfig = levels.get(i);
            if (townLevelConfig == null) continue;

            totalXp += townLevelConfig.getXpThreshold();
            if (totalXp > xp) return previousTownLevelConfig;

            previousTownLevelConfig = townLevelConfig;
        }

        return previousTownLevelConfig;
    }


    public static TownConfig init() {
        return new TownConfig();
    }

    public TownLevelConfig getTownLevelConfigFromLevel(int level) {
        return levels.get(level);
    }

    public int getTownSpawnDistance() {
        return townSpawnDistance;
    }
}
