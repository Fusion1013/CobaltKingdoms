package se.fusion1013.cobaltKingdoms.config.quest;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltKingdoms.config.AbstractConfig;

public class QuestConfig extends AbstractConfig {

    private int baseRewardValue = 1024;
    private int baseRequirementsValue = 512;

    private double rewardFluctuationFraction = 0.2;
    private double requirementsFluctuationFraction = 0.2;

    private double minRewardScalingMultiplier = 0.1;
    private double maxRewardScalingMultiplier = 10.0;

    private double minRequirementsScalingMultiplier = 0.4;
    private double maxRequirementsScalingMultiplier = 3.0;

    private int minRewardUniqueItems = 1;
    private int maxRewardUniqueItems = 4;

    private int minRequirementsUniqueItems = 1;
    private int maxRequirementsUniqueItems = 3;

    private QuestItemDeliveryConfig itemDeliveryConfig;
    private QuestArtifactHuntConfig artifactHuntConfig;

//    private final Map<QuestItem, Double> rewardPools = new HashMap<>();
//    private final Map<QuestItem, Double> requirementPools = new HashMap<>();

    private QuestConfig(String name) {
        super(name);
        loadValues(config);
    }

    private void loadValues(YamlConfiguration config) {
        baseRewardValue = getInt("base_reward_value", 1024);
        baseRequirementsValue = getInt("base_requirements_value", 512);

        rewardFluctuationFraction = getDouble("reward_fluctuation_fraction", 0.2);
        requirementsFluctuationFraction = getDouble("requirements_fluctuation_fraction", 0.2);

        minRewardScalingMultiplier = getDouble("min_reward_scaling_multiplier", 0.1);
        maxRewardScalingMultiplier = getDouble("max_reward_scaling_multiplier", 10.0);

        minRequirementsScalingMultiplier = getDouble("min_requirements_scaling_multiplier", 0.4);
        maxRequirementsScalingMultiplier = getDouble("max_requirements_scaling_multiplier", 3.0);

        minRewardUniqueItems = getInt("min_reward_unique_items", 1);
        maxRewardUniqueItems = getInt("max_reward_unique_items", 4);

        minRequirementsUniqueItems = getInt("min_requirements_unique_items", 1);
        maxRequirementsUniqueItems = getInt("max_requirements_unique_items", 3);

        ConfigurationSection itemDelivery = config.getConfigurationSection("item_delivery");
        itemDeliveryConfig = new QuestItemDeliveryConfig(itemDelivery);

        ConfigurationSection artifactHunt = config.getConfigurationSection("artifact_hunt");
        artifactHuntConfig = new QuestArtifactHuntConfig(artifactHunt);

        // Maybe make it so that it combines this with the more specific one
//        rewardPools = loadQuestItemPool("reward_pool", config);
//        requirementPools = loadQuestItemPool("requirement_pool", config);
    }

    public int getBaseRewardValue() {
        return baseRewardValue;
    }

    public int getBaseRequirementsValue() {
        return baseRequirementsValue;
    }

    public double getRewardFluctuationFraction() {
        return rewardFluctuationFraction;
    }

    public double getRequirementsFluctuationFraction() {
        return requirementsFluctuationFraction;
    }

    public double getMinRewardScalingMultiplier() {
        return minRewardScalingMultiplier;
    }

    public double getMaxRewardScalingMultiplier() {
        return maxRewardScalingMultiplier;
    }

    public double getMinRequirementsScalingMultiplier() {
        return minRequirementsScalingMultiplier;
    }

    public double getMaxRequirementsScalingMultiplier() {
        return maxRequirementsScalingMultiplier;
    }

    public int getMinRewardUniqueItems() {
        return minRewardUniqueItems;
    }

    public int getMaxRewardUniqueItems() {
        return maxRewardUniqueItems;
    }

    public int getMinRequirementsUniqueItems() {
        return minRequirementsUniqueItems;
    }

    public int getMaxRequirementsUniqueItems() {
        return maxRequirementsUniqueItems;
    }

    public static QuestConfig init() {
        return new QuestConfig("quest_config");
    }

    public QuestItemDeliveryConfig getItemDeliveryConfig() {
        return itemDeliveryConfig;
    }

//    public Map<QuestItem, Double> getRequirementPool() {
//        return requirementPools;
//    }
//
//    public Map<QuestItem, Double> getRewardPool() {
//        return rewardPools;
//    }

    public QuestArtifactHuntConfig getArtifactHuntConfig() {
        return artifactHuntConfig;
    }
}
