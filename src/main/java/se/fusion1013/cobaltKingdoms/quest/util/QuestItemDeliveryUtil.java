package se.fusion1013.cobaltKingdoms.quest.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.util.HexUtils;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static se.fusion1013.cobaltKingdoms.quest.service.QuestManager.QUEST_ID_KEY;

public class QuestItemDeliveryUtil {

    /**
     * Summons the entity that the player has to bring, based on the difficulty of the quest.
     *
     */
    public static LivingEntity spawnDeliveryEntity(Location location, int difficulty, UUID questId) {
        World world = location.getWorld();

        if (difficulty == 0) {
            world.spawn(location, Horse.class, horse -> {
                horse.setAdult();
                horse.setTamed(true);
                horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
                horse.getPersistentDataContainer().set(QUEST_ID_KEY, PersistentDataType.STRING, questId.toString());
            });
        }

        if (difficulty == 1) {
            world.spawn(location, Camel.class, camel -> {
                camel.setAdult();
                camel.setTamed(true);
                camel.getInventory().setSaddle(new ItemStack(Material.SADDLE));
                camel.getPersistentDataContainer().set(QUEST_ID_KEY, PersistentDataType.STRING, questId.toString());
            });
        }

        if (difficulty >= 2) {
            world.spawn(location, Pig.class, pig -> {
                pig.setAdult();
                pig.setSaddle(true);
                pig.getPersistentDataContainer().set(QUEST_ID_KEY, PersistentDataType.STRING, questId.toString());
            });
        }

        return null;
    }

    public static Entity getNearbyDeliveryEntity(Location location, UUID questId) {
        World world = location.getWorld();
        Collection<Entity> entities = world.getNearbyEntities(location, 10, 10, 10, entity -> {
            if (!entity.getPersistentDataContainer().has(QUEST_ID_KEY)) return false;
            String value = entity.getPersistentDataContainer().get(QUEST_ID_KEY, PersistentDataType.STRING);
            return questId.toString().equalsIgnoreCase(value);
        });

        if (entities.isEmpty()) return null;

        return entities.iterator().next();
    }

    public static boolean hasRequiredItems(Player player, List<ItemStack> requiredItems) {
        Inventory inv = player.getInventory();

        for (ItemStack required : requiredItems) {
            if (required == null) continue;

            int needed = required.getAmount();
            int found = 0;

            for (ItemStack content : inv.getContents()) {
                if (content == null) continue;

                // Match type + meta (name, lore, enchants, etc.)
                if (content.isSimilar(required)) {
                    found += content.getAmount();

                    if (found >= needed) {
                        break;
                    }
                }
            }

            // Not enough of this required item
            if (found < needed) {
                return false;
            }
        }

        return true;
    }

    public static void removeRequiredItems(Player player, List<ItemStack> requiredItems) {
        Inventory inv = player.getInventory();

        for (ItemStack required : requiredItems) {
            if (required == null) continue;

            int toRemove = required.getAmount();

            for (int slot = 0; slot < inv.getSize(); slot++) {
                ItemStack content = inv.getItem(slot);
                if (content == null) continue;

                // Match type + meta (name, lore, enchants, etc.)
                if (!content.isSimilar(required)) continue;

                int amount = content.getAmount();

                if (amount <= toRemove) {
                    // Remove entire stack
                    inv.setItem(slot, null);
                    toRemove -= amount;
                } else {
                    // Remove part of the stack
                    content.setAmount(amount - toRemove);
                    inv.setItem(slot, content);
                    toRemove = 0;
                }

                if (toRemove <= 0) break;
            }
        }
    }

    public static @NotNull String toComponent(List<ItemStack> cost) {
        return cost.stream()
                .map(QuestItemDeliveryUtil::formatItem)
                .map(HexUtils::stripColorCodes)
                .collect(Collectors.joining(", "));
    }

    private static String formatItem(ItemStack item) {
        if (item == null) return "";

        String name;

        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            name = meta.getDisplayName(); // custom name
        } else {
            name = formatMaterialName(item.getType().name());
        }

        return item.getAmount() + "x " + name;
    }

    private static String formatMaterialName(String materialName) {
        String lower = materialName.toLowerCase().replace("_", " ");
        String[] words = lower.split(" ");

        StringBuilder result = new StringBuilder();
        for (String word : words) {
            result.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
        }

        return result.toString().trim();
    }

}
