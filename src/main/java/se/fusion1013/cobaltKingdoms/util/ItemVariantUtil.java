package se.fusion1013.cobaltKingdoms.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class ItemVariantUtil {

    private static final String[] IRON_SWORD_VARIANTS = {"iron_cutlass", "iron_falchion", "iron_glaive", "iron_katana", "iron_longsword", "iron_rapier", "iron_shortsword", "iron_zweihander"};
    private static final String[] IRON_AXE_VARIANTS = {"iron_battleaxe", "iron_hatchet", "iron_waraxe"};

    public static ItemStack getRandomIronSword() {
        return getRandom(IRON_SWORD_VARIANTS, Material.IRON_SWORD);
    }

    public static ItemStack getRandomIronAxe() {
        return getRandom(IRON_AXE_VARIANTS, Material.IRON_AXE);
    }

    private static ItemStack getRandom(String[] variants, Material material) {
        ItemStack stack = new ItemStack(material);
        ItemMeta itemMeta = stack.getItemMeta();
        String variant = variants[new Random().nextInt(variants.length)];
        itemMeta.setItemModel(new NamespacedKey("thegreatwork", variant));
        itemMeta.customName(Component.text(formatName(variant)).decoration(TextDecoration.ITALIC, false));
        stack.setItemMeta(itemMeta);
        return stack;
    }

    public static String formatName(String input) {

        if (input == null || input.isEmpty()) {
            return input;
        }

        // Replace special characters with spaces
        String cleaned = input.replaceAll("[_\\-]+", " ");

        // Split into words
        String[] words = cleaned.split("\\s+");

        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) continue;

            // Capitalize first letter, lowercase the rest
            String formatted =
                    word.substring(0, 1).toUpperCase() +
                            word.substring(1).toLowerCase();

            result.append(formatted).append(" ");
        }

        // Remove final space
        return result.toString().trim();
    }

}
