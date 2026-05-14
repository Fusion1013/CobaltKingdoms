package se.fusion1013.cobaltKingdoms.config.quest;

import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltKingdoms.config.AbstractConfig;

public class QuestArtifactHuntConfig extends AbstractConfig {

    private double rewardMultiplier = 1;

    private int minRewardUniqueItems = -1;
    private int maxRewardUniqueItems = -1;

    public QuestArtifactHuntConfig(ConfigurationSection yaml) {
        super("quest_config");
        loadValues(yaml);
    }

    private void loadValues(ConfigurationSection yaml) {
        rewardMultiplier = yaml.getDouble("reward_multiplier", 1.0);
        minRewardUniqueItems = yaml.getInt("min_reward_unique_items", -1);
        maxRewardUniqueItems = yaml.getInt("max_reward_unique_items", -1);
    }

    public double getRewardMultiplier() {
        return rewardMultiplier;
    }

    public int getMinRewardUniqueItems() {
        return minRewardUniqueItems;
    }

    public int getMaxRewardUniqueItems() {
        return maxRewardUniqueItems;
    }
}
