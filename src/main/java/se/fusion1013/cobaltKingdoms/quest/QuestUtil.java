package se.fusion1013.cobaltKingdoms.quest;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownEntity;

import java.util.*;

public class QuestUtil {

    private static final int MAX_ITEM_SAMPLE_ITERATIONS = 20;

    public static String formatMaterialName(String input) {
        String[] words = input.toLowerCase().split("_");

        StringBuilder result = new StringBuilder();

        for (String word : words) {
            result.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
        }

        return result.toString().trim();
    }

    /**
     * Generates a list of trade items based on specified parameters.
     *
     * @param minValue       Minimum total value of generated items.
     * @param maxValue       Maximum total value of generated items.
     * @param minUniqueItems Minimum number of unique items to generate.
     * @param maxUniqueItems Maximum number of unique items to generate.
     * @param stockList      Map of TradeItems and their probabilities.
     * @return List of generated ItemStacks.
     */
    public static List<ItemStack> generateTradeItems(
            float minValue,
            float maxValue,
            int minUniqueItems,
            int maxUniqueItems,
            Map<QuestItem, Double> stockList) {

        Random random = new Random();
        List<ItemStack> generatedItems = new ArrayList<>();
        HashSet<QuestItem> usedItems = new HashSet<>();
        HashMap<ItemStack, Double> itemStackValues = new HashMap<>();
        double totalValue = 0;
        int uniqueItemsCount = random.nextInt(maxUniqueItems - minUniqueItems + 1) + minUniqueItems;
        int iteration = 0;

        // Loop until we have the target number of unique items and reach minValue
        // Or until we've tried MAX_ITEM_SAMPLE_ITERATIONS times
        while ((generatedItems.size() < uniqueItemsCount || totalValue < minValue)
                && iteration < MAX_ITEM_SAMPLE_ITERATIONS) {
            // Try to add another item to reach minValue
            iteration++;

            // Sample a random item from the stock list
            QuestItem tradeItem = sampleRandomItem(stockList);

            // If we already have this item, skip it
            if (usedItems.contains(tradeItem)) {
                continue;
            }

            // Ensure the minimum stack of this item will not push us over the maxValue
            double minStackVal = tradeItem.valuePerItem() * tradeItem.minQuantity();
            if (totalValue + minStackVal <= maxValue) {

                // Get the maximum stack quantity that can be added without exceeding maxValue
                int maxStackQuantity = (int) ((maxValue - totalValue) / tradeItem.valuePerItem());
                maxStackQuantity = Math.min(maxStackQuantity, tradeItem.maxQuantity());

                // Get the minimum stack quantity
                int minStackQuantity = tradeItem.minQuantity();

                // If this is the last item, calculate the minStack value needed to reach minValue
                if (generatedItems.size() == uniqueItemsCount - 1) {
                    minStackQuantity = (int) Math.ceil((minValue - totalValue) / tradeItem.valuePerItem());
                    minStackQuantity = Math.min(Math.max(minStackQuantity, tradeItem.minQuantity()), maxStackQuantity);
                }

                // Sample a quantity that is within the range to not push us over the maxValue
                int quantity = random.nextInt(
                        maxStackQuantity - minStackQuantity + 1) + minStackQuantity;

                double stackValue = tradeItem.valuePerItem() * quantity;

                totalValue += stackValue;
                ItemStack itemStack = tradeItem.item();
                itemStack.setAmount(Math.clamp(quantity, 1, 64));

                generatedItems.add(itemStack);
                usedItems.add(tradeItem);
                itemStackValues.put(itemStack, stackValue);
            }

            // If we reach the maximum number of unique items and have more tries left,
            // but totalValue is still below minValue
            if (iteration < MAX_ITEM_SAMPLE_ITERATIONS
                    && generatedItems.size() >= uniqueItemsCount && totalValue < minValue) {
                // Find and remove the least valuable item stack
                ItemStack leastValuable = generatedItems.stream()
                        .min(Comparator.comparing(itemStackValues::get))
                        .get();

                totalValue -= itemStackValues.get(leastValuable);
                generatedItems.remove(leastValuable);
                itemStackValues.remove(leastValuable);
            }
        }

        // Final check: if totalValue is still below minValue, just return what was generated
        return generatedItems;
    }

    /**
     * Samples a random QuestItem from the given stock list based on their probabilities.
     *
     * @param stockList Map of QuestItems and their probabilities.
     * @return A randomly sampled QuestItem.
     */
    private static QuestItem sampleRandomItem(Map<QuestItem, Double> stockList) {
        Random random = new Random();
        double randomValue = random.nextDouble();
        double cumulativeProbability = 0.0;
        for (Map.Entry<QuestItem, Double> entry : stockList.entrySet()) {
            cumulativeProbability += entry.getValue();
            if (randomValue <= cumulativeProbability) {
                return entry.getKey();
            }
        }
        // Fallback in case of rounding errors
        return stockList.keySet().iterator().next();
    }

    public ItemStack getQuestToken(TownEntity handInTown, Long questId, double difficulty) {
        ItemStack itemStack = new ItemStack(Material.CLOCK);
        ItemMeta meta = itemStack.getItemMeta();

        meta.displayName(Component.text("Quest Token").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false));

        if (difficulty == 0) meta.setItemModel(new NamespacedKey("thegreatwork", "quest/copper_quest_token"));
        if (difficulty == 1) meta.setItemModel(new NamespacedKey("thegreatwork", "quest/iron_quest_token"));
        if (difficulty == 2) meta.setItemModel(new NamespacedKey("thegreatwork", "quest/gold_quest_token"));
        if (difficulty == 3) meta.setItemModel(new NamespacedKey("thegreatwork", "quest/diamond_quest_token"));
        if (difficulty == 4) meta.setItemModel(new NamespacedKey("thegreatwork", "quest/netherite_quest_token"));

        meta.lore(
                List.of(
                        Component.text("Deliver to " + handInTown.getName()).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                )
        );
        meta.getPersistentDataContainer().set(QuestManager.QUEST_ID_KEY, PersistentDataType.LONG, questId);

        itemStack.setItemMeta(meta);
        return itemStack;
    }

}
