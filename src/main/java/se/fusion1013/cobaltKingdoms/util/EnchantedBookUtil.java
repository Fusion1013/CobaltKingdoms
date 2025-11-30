package se.fusion1013.cobaltKingdoms.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.*;

/**
 * Utilities for creating or editing books.
 */
public class EnchantedBookUtil {

    private static final Random RANDOM = new Random();

    public static ItemStack getRandomEnchantedBook(int maxLevel) {
        Enchantment enchantment = Enchantment.values()[RANDOM.nextInt(Enchantment.values().length)];
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        meta.addStoredEnchant(enchantment, 1 + RANDOM.nextInt(Math.min(maxLevel, enchantment.getMaxLevel())), true);
        book.setItemMeta(meta);
        return book;
    }

    /**
     * Creates a random enchanted book with one enchantment.
     *
     * @param maxRarity the maximum rarity of the enchanted book.
     * @return An enchanted book item.
     */
    public ItemStack getRandomEnchantedBook(BookRarity maxRarity) {
        // All enchantments by rarity (Paper/Spigot compatible)
        Map<BookRarity, List<Enchantment>> enchantments = new HashMap<>();

        enchantments.put(
                BookRarity.COMMON,
                Arrays.asList(
                        Enchantment.UNBREAKING,
                        Enchantment.EFFICIENCY,
                        Enchantment.SHARPNESS,
                        Enchantment.LOOTING));

        enchantments.put(
                BookRarity.UNCOMMON,
                Arrays.asList(
                        Enchantment.SILK_TOUCH,
                        Enchantment.FORTUNE,
                        Enchantment.FIRE_ASPECT,
                        Enchantment.SWEEPING_EDGE,
                        Enchantment.KNOCKBACK));

        enchantments.put(
                BookRarity.RARE,
                Arrays.asList(
                        Enchantment.MENDING,
                        Enchantment.FROST_WALKER,
                        Enchantment.SOUL_SPEED,
                        Enchantment.CHANNELING));

        // --- Weighted rarity selection ---
        // The trick: higher rarity = better weighted odds
        // But never exceeding the maximum requested rarity

        List<BookRarity> weighted = new ArrayList<>();

        switch (maxRarity) {
            case COMMON:
                Collections.addAll(weighted, BookRarity.COMMON, BookRarity.COMMON, BookRarity.COMMON);
                break;
            case UNCOMMON:
                Collections.addAll(
                        weighted,
                        BookRarity.COMMON,
                        BookRarity.COMMON,
                        BookRarity.UNCOMMON,
                        BookRarity.UNCOMMON);
                break;
            case RARE:
                Collections.addAll(
                        weighted,
                        BookRarity.COMMON,
                        BookRarity.COMMON,
                        BookRarity.UNCOMMON,
                        BookRarity.UNCOMMON,
                        BookRarity.RARE,
                        BookRarity.RARE,
                        BookRarity.RARE);
                break;
            default:
                break;
        }

        // Pick the rarity for THIS book
        BookRarity chosenRarity = weighted.get((int) (Math.random() * weighted.size()));

        // Pick a random enchantment from that rarity group
        List<Enchantment> pool = enchantments.get(chosenRarity);
        Enchantment chosen = pool.get((int) (Math.random() * pool.size()));

        // Random level between 1 and the enchantment's max level
        int level = 1 + (int) (Math.random() * chosen.getMaxLevel());

        // Create the enchanted book
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();

        meta.addStoredEnchant(chosen, level, true);
        book.setItemMeta(meta);

        return book;
    }
}
