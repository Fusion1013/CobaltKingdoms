package se.fusion1013.cobaltKingdoms.config.quest;

import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltKingdoms.config.AbstractConfig;
import se.fusion1013.cobaltKingdoms.quest.model.QuestItem;

import java.util.HashMap;
import java.util.Map;

import static se.fusion1013.cobaltKingdoms.config.quest.QuestConfigUtil.loadQuestItemPool;

public class QuestArtifactHuntConfig extends AbstractConfig {

    private double rewardMultiplier = 1;

    private int minRewardUniqueItems = -1;
    private int maxRewardUniqueItems = -1;

    private Map<QuestItem, Double> rewardPools = new HashMap<>();

    public QuestArtifactHuntConfig(ConfigurationSection yaml) {
        super("quest_config");
        loadValues(yaml);
    }

    private void loadValues(ConfigurationSection yaml) {
        rewardMultiplier = yaml.getDouble("reward_multiplier", 1.0);
        minRewardUniqueItems = yaml.getInt("min_reward_unique_items", -1);
        maxRewardUniqueItems = yaml.getInt("max_reward_unique_items", -1);

        rewardPools = loadQuestItemPool("reward_pool", yaml);
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

    public Map<QuestItem, Double> getRewardPool() {
        return rewardPools;
    }
}
