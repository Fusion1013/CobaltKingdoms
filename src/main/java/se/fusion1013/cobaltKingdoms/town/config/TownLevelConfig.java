package se.fusion1013.cobaltKingdoms.town.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class TownLevelConfig {

    private final int level;
    private final int xpThreshold;
    private final double questRewardMultiplier;
    private final double questRequirementsMultiplier;
    private final int maxSimultaneousQuests;
    private final Map<String, Integer> availableQuests = new HashMap<>();

    public TownLevelConfig(Map<?, ?> values) {
        this.level = getOrDefault(values, "level", 0);
        this.xpThreshold = getOrDefault(values, "xp_threshold", 0);
        this.questRewardMultiplier = getOrDefault(values, "quest_reward_multiplier", 1.0);
        this.questRequirementsMultiplier = getOrDefault(values, "quest_requirements_multiplier", 1.0);
        this.maxSimultaneousQuests = getOrDefault(values, "max_simultaneous_quests", 1);

        List<Map<?, ?>> list = (List<Map<?, ?>>) values.get("possible_quests");
        for (Map<?, ?> map : list) {
            String quest = (String) map.get("quest");
            int weight = (int) map.get("weight");
            availableQuests.put(quest, weight);
        }
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

    public String getRandomQuest() {

        int totalWeight = 0;

        for (int weight : availableQuests.values()) {
            totalWeight += weight;
        }

        if (totalWeight <= 0) {
            return null;
        }

        int random = ThreadLocalRandom.current().nextInt(totalWeight);

        int current = 0;

        for (Map.Entry<String, Integer> entry : availableQuests.entrySet()) {

            current += entry.getValue();

            if (random < current) {
                return entry.getKey();
            }
        }

        return null;
    }

}
