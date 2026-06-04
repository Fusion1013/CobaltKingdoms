package se.fusion1013.cobaltKingdoms.config.quest;

import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.config.ItemValueConfig;
import se.fusion1013.cobaltKingdoms.quest.model.QuestItem;

import java.util.*;
import java.util.logging.Logger;

public class QuestConfigUtil {

    public static Map<QuestItem, Double> loadQuestItemPool(String poolName, ConfigurationSection questSection) {
        Logger logger = CobaltKingdoms.getInstance().getLogger();

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

}
