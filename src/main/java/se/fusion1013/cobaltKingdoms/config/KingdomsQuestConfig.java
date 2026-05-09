package se.fusion1013.cobaltKingdoms.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.quest.QuestItem;

import java.util.*;
import java.util.logging.Logger;

public class KingdomsQuestConfig extends Manager<CobaltKingdoms> {

    private static final YamlConfiguration config = KingdomsConfig.getConfig();
    private static Map<QuestItem, Double> rewardPools = new HashMap<>();
    private static Map<QuestItem, Double> requirementPools = new HashMap<>();

    public KingdomsQuestConfig(CobaltKingdoms plugin) {
        super(plugin);
    }

    private static Map<QuestItem, Double> loadQuestItemPool(String poolName) {
        Logger logger = CobaltKingdoms.getInstance().getLogger();
        Map<Integer, Map<QuestItem, Double>> levelPools = new HashMap<>();

        ConfigurationSection questSection = config.getConfigurationSection("quest");
        ConfigurationSection poolSection = questSection.getConfigurationSection(poolName);

        if (poolSection == null) {
            throw new IllegalStateException("Pool '" + poolName + "' not found");
        }

        Map<QuestItem, Double> itemPool = new HashMap<>();
        double totalWeight = 0;

        // Loop through each category in the pool
        for (String categoryName : poolSection.getKeys(false)) {
            ConfigurationSection categorySection = poolSection.getConfigurationSection(categoryName);

            // Set the default values for the category
            double categoryWeight = categorySection.getDouble("weight", 1);
            int defaultMinAmount = categorySection.getInt("min_amount", 1);
            int defaultMaxAmount = categorySection.getInt("max_amount", 1);

            // Get the items for the category, and skip if no items are defined, or if the weight is 0
            List<Map<?, ?>> itemsList = categorySection.getMapList("items");
            if (itemsList.isEmpty() || categoryWeight <= 0) continue;

            double categoryTotalWeight = 0;
            List<QuestItem> categoryItems = new ArrayList<>();
            List<Double> categoryItemWeights = new ArrayList<>();

            // Loop through each item in the category
            for (Map<?, ?> itemMap : itemsList) {
                if (!itemMap.containsKey("item"))
                    continue;

                String itemName = (String) itemMap.get("item");

                // Get the stats for the item
                double itemWeight = itemMap.containsKey("weight") ? ((Number) itemMap.get("weight")).doubleValue() : 1;
                int minAmount = itemMap.containsKey("min_amount") ? ((Number) itemMap.get("min_amount")).intValue() : defaultMinAmount;
                int maxAmount = itemMap.containsKey("max_amount") ? ((Number) itemMap.get("max_amount")).intValue() : defaultMaxAmount;
                Optional<Float> valueOpt = ItemValueConfig.getMaterialValue(itemName);

                if (valueOpt.isEmpty()) {
                    logger.warning("Material '" + itemName + "' in " + poolName
                            + " pool config is not defined in item_values.yml");
                    logger.warning("This item will not be available for missions");
                    continue;
                }

                QuestItem tradeItem = new QuestItem(
                        itemName,
                        categoryName,
                        minAmount,
                        maxAmount,
                        valueOpt.get()
                );

                categoryItems.add(tradeItem);
                categoryItemWeights.add(itemWeight);
                categoryTotalWeight += itemWeight;
            }

            // Calculate the probability of each item in the category
            for (int i = 0; i < categoryItems.size(); i++) {
                double itemProbability = (categoryItemWeights.get(i) / categoryTotalWeight) * categoryWeight;
                itemPool.put(categoryItems.get(i), itemProbability);
                totalWeight += itemProbability;
            }
        }

        // Normalize probabilities
        for (Map.Entry<QuestItem, Double> entry : itemPool.entrySet()) {
            entry.setValue(entry.getValue() / totalWeight);
        }

        return itemPool;
    }

    @Override
    public void reload() {
        rewardPools = loadQuestItemPool("reward_pool");
        requirementPools = loadQuestItemPool("requirement_pool");
    }

    @Override
    public void disable() {

    }

    public static Map<QuestItem, Double> getRequirementPool() {
        return requirementPools;
    }

    public static Map<QuestItem, Double> getRewardPool() {
        return rewardPools;
    }
}
