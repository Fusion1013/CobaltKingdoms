package se.fusion1013.cobaltKingdoms.config.town;

import java.util.Map;

public class TownLevelConfig {

    private final int level;
    private final int xpThreshold;
    private final double questRewardMultiplier;
    private final double questRequirementsMultiplier;
    private final int maxSimultaneousQuests;

    public TownLevelConfig(Map<?, ?> values) {
        this.level = getOrDefault(values, "level", 0);
        this.xpThreshold = getOrDefault(values, "xp_threshold", 0);
        this.questRewardMultiplier = getOrDefault(values, "quest_reward_multiplier", 1.0);
        this.questRequirementsMultiplier = getOrDefault(values, "quest_requirements_multiplier", 1.0);
        this.maxSimultaneousQuests = getOrDefault(values, "max_simultaneous_quests", 1);
    }


    public int getLevel() {
        return level;
    }

    public int getXpThreshold() {
        return xpThreshold;
    }

    public double getQuestRewardMultiplier() {
        return questRewardMultiplier;
    }

    public double getQuestRequirementsMultiplier() {
        return questRequirementsMultiplier;
    }

    public int getMaxSimultaneousQuests() {
        return maxSimultaneousQuests;
    }

    private int getOrDefault(Map<?, ?> values, String key, int defaultValue) {
        if (!values.containsKey(key)) return defaultValue;
        return (int) values.get(key);
    }

    private double getOrDefault(Map<?, ?> values, String key, double defaultValue) {
        if (!values.containsKey(key)) return defaultValue;
        return (double) values.get(key);
    }

}
