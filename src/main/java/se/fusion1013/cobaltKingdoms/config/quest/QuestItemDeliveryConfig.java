package se.fusion1013.cobaltKingdoms.config.quest;

import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltKingdoms.config.AbstractConfig;

public class QuestItemDeliveryConfig extends AbstractConfig {

    private double rewardMultiplier = 1;

    private int minRewardScalingDistance = 500;
    private int baseRewardScalingDistance = 1000;
    private int maxRewardScalingDistance = 5000;

    private int minRequirementsScalingDistance = 500;
    private int baseRequirementsScalingDistance = 1000;
    private int maxRequirementsScalingDistance = 5000;

    public QuestItemDeliveryConfig(ConfigurationSection yaml) {
        super("quest_config");
        loadValues(yaml);
    }

    private void loadValues(ConfigurationSection config) {
        rewardMultiplier = config.getDouble("reward_multiplier", 1.0);

        minRewardScalingDistance = config.getInt("min_reward_scaling_distance", 500);
        baseRewardScalingDistance = config.getInt("base_reward_scaling_distance", 1000);
        maxRewardScalingDistance = config.getInt("max_reward_scaling_distance", 5000);

        minRequirementsScalingDistance = config.getInt("min_requirements_scaling_distance", 500);
        baseRequirementsScalingDistance = config.getInt("base_requirements_scaling_distance", 1000);
        maxRequirementsScalingDistance = config.getInt("max_requirements_scaling_distance", 5000);
    }

    public double getRewardMultiplier() {
        return rewardMultiplier;
    }

    public int getMinRewardScalingDistance() {
        return minRewardScalingDistance;
    }

    public int getBaseRewardScalingDistance() {
        return baseRewardScalingDistance;
    }

    public int getMaxRewardScalingDistance() {
        return maxRewardScalingDistance;
    }

    public int getMinRequirementsScalingDistance() {
        return minRequirementsScalingDistance;
    }

    public int getBaseRequirementsScalingDistance() {
        return baseRequirementsScalingDistance;
    }

    public int getMaxRequirementsScalingDistance() {
        return maxRequirementsScalingDistance;
    }
}
