package se.fusion1013.cobaltKingdoms.quest;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.cobaltCore.util.HexUtils;
import se.fusion1013.cobaltKingdoms.database.kingdom.town.TownEntity;
import se.fusion1013.cobaltKingdoms.util.LargeItemStack;

import java.util.*;

public class QuestUtil {

    private static final int MAX_ITEM_SAMPLE_ITERATIONS = 20;
    private static final Random random = new Random();

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
            Map<QuestItem, Double> stockList,
            List<ItemStack> blacklist) {

        List<LargeItemStack> generatedItems = new ArrayList<>();
        HashSet<QuestItem> usedItems = new HashSet<>();
        HashMap<LargeItemStack, Double> itemStackValues = new HashMap<>();
        double totalValue = 0;
        int uniqueItemsCount = random.nextInt(maxUniqueItems - minUniqueItems + 1) + minUniqueItems;
        int iteration = 0;
        blacklist = blacklist.stream().map(i -> {
            ItemStack newItem = i.clone();
            newItem.setAmount(1);
            return newItem;
        }).toList();

        double targetValuePerStack = minValue / (uniqueItemsCount * 1.25);

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

            if (blacklist.contains(tradeItem.getItem())) {
                continue;
            }

            // Ensure the minimum stack of this item will not push us over the maxValue
            double minStackVal = tradeItem.valuePerItem() * tradeItem.minQuantity();
            double maxStackVal = tradeItem.valuePerItem() * tradeItem.maxQuantity();
            double averageStackVal = (minStackVal + maxStackVal) / 2.0;

            if (averageStackVal < targetValuePerStack) continue;

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
                ItemStack itemTemplate = tradeItem.item();
                LargeItemStack itemStack = new LargeItemStack(itemTemplate, quantity);

                generatedItems.add(itemStack);

                usedItems.add(tradeItem);
                itemStackValues.put(itemStack, stackValue);
            }

            // If we reach the maximum number of unique items and have more tries left,
            // but totalValue is still below minValue
            if (iteration < MAX_ITEM_SAMPLE_ITERATIONS
                    && generatedItems.size() >= uniqueItemsCount && totalValue < minValue) {
                // Find and remove the least valuable item stack
                LargeItemStack leastValuable = generatedItems.stream()
                        .min(Comparator.comparing(itemStackValues::get))
                        .get();

                totalValue -= itemStackValues.get(leastValuable);
                generatedItems.remove(leastValuable);
                itemStackValues.remove(leastValuable);
            }
        }

        // Final check: if totalValue is still below minValue, just return what was generated
        return generatedItems.stream()
                .flatMap(large -> large.getItems().stream())
                .toList();
    }

    public static void spawnRandomFirework(Location loc) {
        Firework firework = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK_ROCKET);
        FireworkMeta meta = firework.getFireworkMeta();

        Random random = new Random();

        // Pick 1-3 random colors
        Color[] possibleColors = {
                Color.AQUA, Color.BLUE, Color.FUCHSIA, Color.GREEN, Color.LIME,
                Color.MAROON, Color.NAVY, Color.ORANGE, Color.PURPLE, Color.RED, Color.SILVER, Color.WHITE, Color.YELLOW
        };

        int colorCount = 1 + random.nextInt(3);
        Color[] colors = new Color[colorCount];
        for (int i = 0; i < colorCount; i++) {
            colors[i] = possibleColors[random.nextInt(possibleColors.length)];
        }

        // Pick 1-2 random fade colors
        int fadeCount = 1 + random.nextInt(2);
        Color[] fades = new Color[fadeCount];
        for (int i = 0; i < fadeCount; i++) {
            fades[i] = possibleColors[random.nextInt(possibleColors.length)];
        }

        // Random firework type
        FireworkEffect.Type[] types = FireworkEffect.Type.values();
        FireworkEffect.Type type = types[random.nextInt(types.length)];

        // Random trail & flicker
        boolean flicker = random.nextBoolean();
        boolean trail = random.nextBoolean();

        // Build the effect
        FireworkEffect effect = FireworkEffect.builder()
                .withColor(colors)
                .withFade(fades)
                .with(type)
                .flicker(flicker)
                .trail(trail)
                .build();

        meta.addEffect(effect);

        // Random power 1-3
        meta.setPower(1 + random.nextInt(3));

        firework.setFireworkMeta(meta);
    }

    public static void clearQuestItems(Player player, Long questId) {
        for (ItemStack item : player.getInventory()) {
            if (item == null || item.isEmpty()) continue;
            if (!item.getPersistentDataContainer().has(QuestManager.QUEST_ID_KEY)) continue;
            Long id = item.getPersistentDataContainer().get(QuestManager.QUEST_ID_KEY, PersistentDataType.LONG);
            if (!id.equals(questId)) continue;
            item.setAmount(0);
        }
    }

    public static void giveQuestCompass(Player player, Location targetLocation, String title, Long questId) {
        // Create compass
        ItemStack compass = new ItemStack(Material.COMPASS);
        CompassMeta compassMeta = (CompassMeta) compass.getItemMeta();
        compassMeta.setLodestone(targetLocation);
        compassMeta.setLodestoneTracked(false);
        compassMeta.getPersistentDataContainer().set(QuestManager.QUEST_ID_KEY, PersistentDataType.LONG, questId);
        compassMeta.setDisplayName(HexUtils.colorify("&z" + title));
        compass.setItemMeta(compassMeta);
        player.give(compass);
    }

    public static float calculateScalingMultiplier(
            double distance,
            float minScalingDist,
            float baseScalingDist,
            float maxScalingDist,
            float minScalingMult,
            float maxScalingMult) {

        distance = Math.max(minScalingDist, Math.min(maxScalingDist, distance));

        float scaledMult;

        if (distance <= baseScalingDist) {
            float range = baseScalingDist - minScalingDist;
            float distAboveMin = (float) distance - minScalingDist;
            float ratio = distAboveMin / range;
            float multRange = 1.0f - minScalingMult;
            scaledMult = minScalingMult + (multRange * ratio);
        } else {
            float range = maxScalingDist - baseScalingDist;
            float distAboveBase = (float) distance - baseScalingDist;
            float ratio = distAboveBase / range;
            float multRange = maxScalingMult - 1.0f;
            scaledMult = 1.0f + (multRange * ratio);
        }
        return scaledMult;
    }

    /**
     * Samples a random QuestItem from the given stock list based on their probabilities.
     *
     * @param stockList Map of QuestItems and their probabilities.
     * @return A randomly sampled QuestItem.
     */
    public static QuestItem sampleRandomItem(Map<QuestItem, Double> stockList) {
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
                        Component.text("Deliver to " + handInTown.getDisplayName()).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                )
        );
        meta.getPersistentDataContainer().set(QuestManager.QUEST_ID_KEY, PersistentDataType.LONG, questId);

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     *
     * @param upper .
     * @param bias  Higher => stronger bias towards lower numbers.
     * @return .
     */
    public static int getRandomWeighted(int upper, double bias) {
        return (int) (Math.pow(random.nextDouble(), bias) * (upper + 1));
    }

    public static String formatDuration(long millis) {

        long days = millis / (24 * 60 * 60 * 1000);
        millis %= (24 * 60 * 60 * 1000);

        long hours = millis / (60 * 60 * 1000);
        millis %= (60 * 60 * 1000);

        long minutes = millis / (60 * 1000);
        millis %= (60 * 1000);

        long seconds = millis / 1000;

        StringBuilder sb = new StringBuilder();

        if (days > 0) {
            sb.append(days).append("d ");
        }

        if (hours > 0) {
            sb.append(hours).append("h ");
        }

        if (minutes > 0) {
            sb.append(minutes).append("m ");
        }

        if (seconds > 0 || sb.length() == 0) {
            sb.append(seconds).append("s");
        }

        return sb.toString().trim();
    }

    public static List<String> wrapText(String text, int maxLineLength) {

        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        String[] words = text.split(" ");

        int currentLength = 0;

        for (String word : words) {

            // +1 for the space
            int additionalLength = word.length() + (currentLength == 0 ? 0 : 1);

            if (currentLength + additionalLength > maxLineLength) {

                result.add(current.toString());
                current = new StringBuilder();
                current.append(word);

                currentLength = word.length();

            } else {

                if (currentLength > 0) {
                    current.append(" ");
                }

                current.append(word);

                currentLength += additionalLength;
            }
        }

        if (!current.isEmpty()) result.add(current.toString());

        return result;
    }

    public static String formatTitle(String title, String symbol) {
        return "&y" + title;
    }
}
