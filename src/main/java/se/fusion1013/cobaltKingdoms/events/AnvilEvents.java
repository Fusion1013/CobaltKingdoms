package se.fusion1013.cobaltKingdoms.events;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class AnvilEvents implements Listener {

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {

        ItemStack left = event.getInventory().getItem(0);
        ItemStack right = event.getInventory().getItem(1);
        ItemStack result = event.getResult();

        // If there's no result, nothing to check
        if (result == null) return;

        // If either input is missing, let vanilla handle (this also covers rename-only)
        if (isAirOrNull(left) || isAirOrNull(right)) return;

        Map<Enchantment, Integer> leftEnchants = collectAllEnchants(left);
        Map<Enchantment, Integer> rightEnchants = collectAllEnchants(right);
        Map<Enchantment, Integer> resultEnchants = collectAllEnchants(result);

        // For each enchant present on the result, ensure it's not higher than max input level
        for (Map.Entry<Enchantment, Integer> e : resultEnchants.entrySet()) {
            Enchantment ench = e.getKey();
            int resultLevel = e.getValue();

            int leftLevel = leftEnchants.getOrDefault(ench, 0);
            int rightLevel = rightEnchants.getOrDefault(ench, 0);
            int allowedMax = Math.max(leftLevel, rightLevel);

            if (resultLevel > allowedMax) {
                // Disallow producing result that increases enchantment levels
                event.setResult(null);
                return;
            }
        }

        // All checks passed -> allow vanilla result
    }

    private static boolean isAirOrNull(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    /**
     * Collects enchantments from both normal items and enchanted books.
     * If the ItemMeta is an EnchantmentStorageMeta (enchanted book),
     * we use getStoredEnchants(); otherwise we use getEnchantments().
     */
    private static Map<Enchantment, Integer> collectAllEnchants(ItemStack item) {
        Map<Enchantment, Integer> map = new HashMap<>();
        if (item == null) return map;

        // Normal enchantments (this will be empty for enchanted books)
        Map<Enchantment, Integer> normal = item.getEnchantments();
        if (normal != null && !normal.isEmpty()) {
            map.putAll(normal);
        }

        // Stored enchantments (enchanted books, or any EnchantmentStorageMeta)
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta esm = (EnchantmentStorageMeta) meta;
            Map<Enchantment, Integer> stored = esm.getStoredEnchants();
            if (stored != null && !stored.isEmpty()) {
                // If an enchant appears in both maps, prefer the higher value (shouldn't usually happen)
                for (Map.Entry<Enchantment, Integer> e : stored.entrySet()) {
                    map.merge(e.getKey(), e.getValue(), Math::max);
                }
            }
        }

        return map;
    }

}
