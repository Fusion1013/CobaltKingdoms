package se.fusion1013.cobaltKingdoms.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ItemVariantUtil {

    private static final Random RANDOM = new Random();
    private static final Map<Material, List<ItemVariant>> ITEM_VARIANTS = Map.of(
            Material.WRITABLE_BOOK, List.of(
                    new ItemVariant(ItemRarity.COMMON, "book/gray_1", "Writable Book", "Simple Gray"),
                    new ItemVariant(ItemRarity.COMMON, "book/gray_2", "Writable Book", "Decorated Gray"),
                    new ItemVariant(ItemRarity.COMMON, "book/gray_3", "Writable Book", "Ornate Gray"),
                    new ItemVariant(ItemRarity.COMMON, "book/green_1", "Writable Book", "Simple Green"),
                    new ItemVariant(ItemRarity.COMMON, "book/green_2", "Writable Book", "Decorated Green"),
                    new ItemVariant(ItemRarity.COMMON, "book/green_3", "Writable Book", "Ornate Green"),
                    new ItemVariant(ItemRarity.COMMON, "book/orange_1", "Writable Book", "Simple Orange"),
                    new ItemVariant(ItemRarity.COMMON, "book/orange_2", "Writable Book", "Decorated Orange"),
                    new ItemVariant(ItemRarity.COMMON, "book/orange_3", "Writable Book", "Ornate Orange"),
                    new ItemVariant(ItemRarity.COMMON, "book/teal_1", "Writable Book", "Simple Teal"),
                    new ItemVariant(ItemRarity.COMMON, "book/teal_2", "Writable Book", "Decorated Teal"),
                    new ItemVariant(ItemRarity.COMMON, "book/teal_3", "Writable Book", "Ornate Teal"),
                    new ItemVariant(ItemRarity.COMMON, "book/maroon_1", "Writable Book", "Simple Maroon"),
                    new ItemVariant(ItemRarity.COMMON, "book/maroon_2", "Writable Book", "Decorated Maroon"),
                    new ItemVariant(ItemRarity.COMMON, "book/maroon_3", "Writable Book", "Ornate Maroon"),
                    new ItemVariant(ItemRarity.COMMON, "book/letter", "Letter", ""),
                    new ItemVariant(ItemRarity.COMMON, "book/letter_black", "Black Letter", ""),
                    new ItemVariant(ItemRarity.COMMON, "book/letter_blue", "Blue Letter", ""),
                    new ItemVariant(ItemRarity.COMMON, "book/letter_brown", "Brown Letter", ""),
                    new ItemVariant(ItemRarity.COMMON, "book/letter_cyan", "Cyan Letter", ""),
                    new ItemVariant(ItemRarity.COMMON, "book/letter_gray", "Gray Letter", ""),
                    new ItemVariant(ItemRarity.COMMON, "book/letter_green", "Green Letter", ""),
                    new ItemVariant(ItemRarity.COMMON, "book/letter_light_blue", "Light Blue Letter", ""),
                    new ItemVariant(ItemRarity.COMMON, "book/letter_light_gray", "Light Gray Latter", ""),
                    new ItemVariant(ItemRarity.COMMON, "book/letter_lime", "Lime Letter", ""),
                    new ItemVariant(ItemRarity.COMMON, "book/letter_magenta", "Magenta Letter", ""),
                    new ItemVariant(ItemRarity.COMMON, "book/letter_orange", "Orange Letter", ""),
                    new ItemVariant(ItemRarity.COMMON, "book/letter_pink", "Pink Letter", ""),
                    new ItemVariant(ItemRarity.COMMON, "book/letter_purple", "Purple Letter", ""),
                    new ItemVariant(ItemRarity.COMMON, "book/letter_red", "Red Letter", ""),
                    new ItemVariant(ItemRarity.COMMON, "book/letter_white", "White Letter", ""),
                    new ItemVariant(ItemRarity.COMMON, "book/letter_yellow", "Yellow Letter", ""),
                    new ItemVariant(ItemRarity.UNCOMMON, "book/brown_1", "Writable Book", "Brown"),
                    new ItemVariant(ItemRarity.UNCOMMON, "book/brown_yellow", "Writable Book", "Yellow-Brown"),
                    new ItemVariant(ItemRarity.UNCOMMON, "book/burnt", "Writable Book", "Burnt"),
                    new ItemVariant(ItemRarity.UNCOMMON, "book/heavy_bound", "Heavy Notebook", "Heavy"),
                    new ItemVariant(ItemRarity.UNCOMMON, "book/notebook", "Notebook", "Notebook"),
                    new ItemVariant(ItemRarity.UNCOMMON, "book/slime", "Writable Book", "Slimy"),
                    new ItemVariant(ItemRarity.UNCOMMON, "book/spectral", "Writable Book", "Spectral"),
                    new ItemVariant(ItemRarity.UNCOMMON, "book/traveller", "Traveller's Journal", "Traveller"),
                    new ItemVariant(ItemRarity.UNCOMMON, "book/treasure", "Writable Book", "Treasure"),
                    new ItemVariant(ItemRarity.UNCOMMON, "book/worn", "Writable Book", "Worn"),
                    new ItemVariant(ItemRarity.RARE, "book/binding_bound_afrit", "Writable Book", "Warding Binding Book"),
                    new ItemVariant(ItemRarity.RARE, "book/binding_bound_djinni", "Writable Book", "Dark Binding Book"),
                    new ItemVariant(ItemRarity.RARE, "book/binding_bound_foliot", "Writable Book", "Deep Binding Book"),
                    new ItemVariant(ItemRarity.RARE, "book/binding_bound_marid", "Writable Book", "Shimmering Binding Book"),
                    new ItemVariant(ItemRarity.RARE, "book/curse", "Writable Book", "Cursed"),
                    new ItemVariant(ItemRarity.RARE, "book/spellbook", "Writable Book", "Spellbook"),
                    new ItemVariant(ItemRarity.RARE, "book/letter_ashen", "Ashen Letter", ""),
                    new ItemVariant(ItemRarity.RARE, "book/letter_bloodsoaked", "Bloodsoaked Letter", ""),
                    new ItemVariant(ItemRarity.RARE, "book/letter_gilded", "Gilded Letter", ""),
                    new ItemVariant(ItemRarity.RARE, "book/letter_mossy", "Mossy Letter", ""),
                    new ItemVariant(ItemRarity.RARE, "book/letter_ornate", "Ornate Letter", ""),
                    new ItemVariant(ItemRarity.RARE, "book/letter_prismatic", "Prismatic Letter", ""),
                    new ItemVariant(ItemRarity.RARE, "book/letter_royal", "Royal Letter", "")
            ),
            Material.IRON_SWORD, List.of(
                    new ItemVariant(ItemRarity.COMMON, "sword/iron_cutlass", "Iron Cutlass", "Cutlass"),
                    new ItemVariant(ItemRarity.COMMON, "sword/iron_falchion", "Iron Falchion", "Falchion"),
                    new ItemVariant(ItemRarity.COMMON, "sword/iron_gladius", "Iron Gladius", "Gladius"),
                    new ItemVariant(ItemRarity.COMMON, "sword/iron_longsword", "Iron Longsword", "Longsword"),
                    new ItemVariant(ItemRarity.COMMON, "sword/iron_rapier", "Iron Rapier", "Rapier"),
                    new ItemVariant(ItemRarity.COMMON, "sword/iron_shortsword", "Iron Shortsword", "Shortsword"),
                    new ItemVariant(ItemRarity.RARE, "sword/iron_scythe", "Iron Scythe", "Scythe"),
                    new ItemVariant(ItemRarity.RARE, "sword/iron_sickle", "Iron Sickle", "Sickle"),
                    new ItemVariant(ItemRarity.RARE, "sword/iron_zweihander", "Iron Zweihander", "Zweihander")
            ),
            Material.DIAMOND_SWORD, List.of(
                    new ItemVariant(ItemRarity.COMMON, "sword/diamond_cutlass", "Diamond Cutlass", "Cutlass"),
                    new ItemVariant(ItemRarity.COMMON, "sword/diamond_dagger", "Diamond Dagger", "Dagger"),
                    new ItemVariant(ItemRarity.COMMON, "sword/diamond_falchion", "Diamond Falchion", "Falchion"),
                    new ItemVariant(ItemRarity.COMMON, "sword/diamond_greatsword", "Diamond Greatsword", "Greatsword"),
                    new ItemVariant(ItemRarity.COMMON, "sword/diamond_gladius", "Diamond Gladius", "Gladius"),
                    new ItemVariant(ItemRarity.COMMON, "sword/diamond_khopesh", "Diamond Khopesh", "Khopesh"),
                    new ItemVariant(ItemRarity.COMMON, "sword/diamond_longsword", "Diamond Longsword", "Longsword"),
                    new ItemVariant(ItemRarity.COMMON, "sword/diamond_rapier", "Diamond Rapier", "Rapier"),
                    new ItemVariant(ItemRarity.COMMON, "sword/diamond_scythe", "Diamond Scythe", "Scythe"),
                    new ItemVariant(ItemRarity.COMMON, "sword/diamond_shortsword", "Diamond Shortsword", "Shortsword"),
                    new ItemVariant(ItemRarity.COMMON, "sword/diamond_sickle", "Diamond Sickle", "Sickle"),
                    new ItemVariant(ItemRarity.COMMON, "sword/diamond_ornate_sword", "Diamond Ornate Sword", "Ornate"),
                    new ItemVariant(ItemRarity.COMMON, "sword/diamond_zweihander", "Diamond Zweihander", "Zweihander")
            ),
            Material.IRON_AXE, List.of(
                    new ItemVariant(ItemRarity.COMMON, "axe/iron_battleaxe", "Iron Battleaxe", "Battleaxe"),
                    new ItemVariant(ItemRarity.COMMON, "axe/iron_hatchet", "Iron Hatchet", "Hatchet"),
                    new ItemVariant(ItemRarity.COMMON, "axe/iron_waraxe", "Iron Waraxe", "Waraxe"),
                    new ItemVariant(ItemRarity.RARE, "axe/iron_poleaxe", "Iron Poleaxe", "Poleaxe"),
                    new ItemVariant(ItemRarity.RARE, "axe/iron_hammer", "Iron Hammer", "Hammer")
            ),
            Material.BOW, List.of(
                    new ItemVariant(ItemRarity.COMMON, "birchbow", "Birch Bow", "Birch"),
                    new ItemVariant(ItemRarity.COMMON, "junglebow", "Jungle Bow", "Jungle"),
                    new ItemVariant(ItemRarity.COMMON, "sprucebow", "Spruce Bow", "Spruce")
            ),
            Material.CROSSBOW, List.of(
                    new ItemVariant(ItemRarity.COMMON, "stainedcrossbow", "Stained Crossbow", "Stained"),
                    new ItemVariant(ItemRarity.COMMON, "mossycrossbow", "Mossy Crossbow", "Mossy"),
                    new ItemVariant(ItemRarity.COMMON, "bleachedcrossbow", "Bleached Crossbow", "Bleached")
            ),
            Material.CARVED_PUMPKIN, List.of(
                    new ItemVariant(ItemRarity.UNCOMMON, "hat/amber_leather_hat", "Amber Leather Hat", "Amber Leather"),
                    new ItemVariant(ItemRarity.COMMON, "hat/amber_wool_hat", "Amber Wool Hat", "Amber Wool"),
                    new ItemVariant(ItemRarity.UNCOMMON, "hat/banker_hat", "Banker Hat", "Banker"),
                    new ItemVariant(ItemRarity.UNCOMMON, "hat/charcoal_leather_hat", "Charcoal Leather Hat", "Charcoal Leather"),
                    new ItemVariant(ItemRarity.COMMON, "hat/charcoal_wool_hat", "Charcoal Wool Hat", "Charcoal Wool"),
                    new ItemVariant(ItemRarity.UNCOMMON, "hat/emerald_leather_hat", "Emerald Leather Hat", "Emerald Leather"),
                    new ItemVariant(ItemRarity.COMMON, "hat/emerald_wool_hat", "Emerald Wool Hat", "Emerald Wool"),
                    new ItemVariant(ItemRarity.RARE, "hat/farmer_hat", "Farmer Hat", "Farmer"),
                    new ItemVariant(ItemRarity.UNCOMMON, "hat/ivory_leather_hat", "Ivory Leather Hat", "Ivory Leather"),
                    new ItemVariant(ItemRarity.COMMON, "hat/ivory_wool_hat", "Ivory Wool Hat", "Ivory Wool"),
                    new ItemVariant(ItemRarity.UNCOMMON, "hat/maroon_leather_hat", "Maroon Leather Hat", "Maroon Leather"),
                    new ItemVariant(ItemRarity.COMMON, "hat/maroon_wool_hat", "Maroon Wool Hat", "Maroon Wool"),
                    new ItemVariant(ItemRarity.UNCOMMON, "hat/navy_leather_hat", "Navy Leather Hat", "Navy Leather"),
                    new ItemVariant(ItemRarity.COMMON, "hat/navy_wool_hat", "Navy Wool Hat", "Navy Wool"),
                    new ItemVariant(ItemRarity.RARE, "hat/painter_hat", "Painter Hat", "Painter"),
                    new ItemVariant(ItemRarity.UNCOMMON, "hat/rust_leather_hat", "Rust Leather Hat", "Rust Leather"),
                    new ItemVariant(ItemRarity.COMMON, "hat/rust_wool_hat", "Rust Wool Hat", "Rust Wool"),
                    new ItemVariant(ItemRarity.RARE, "hat/top_hat", "Top Hat", "Top"),
                    new ItemVariant(ItemRarity.RARE, "hat/topper_hat", "Topper Hat", "Topper"),
                    new ItemVariant(ItemRarity.UNCOMMON, "hat/violet_leather_hat", "Violet Leather", "Violet Leather"),
                    new ItemVariant(ItemRarity.COMMON, "hat/violet_wool_hat", "Violet Wool", "Violet Wool")
            ),
            Material.IRON_SPEAR, List.of(
                    new ItemVariant(ItemRarity.COMMON, "spear/iron_glaive", "Iron Glaive", "Iron Glaive"),
                    new ItemVariant(ItemRarity.RARE, "spear/iron_halberd", "Iron Halberd", "Iron Halberd"),
                    new ItemVariant(ItemRarity.COMMON, "spear/iron_javelin", "Iron Javelin", "Iron Javelin"),
                    new ItemVariant(ItemRarity.RARE, "spear/iron_lance", "Iron Lance", "Iron Lance"),
                    new ItemVariant(ItemRarity.COMMON, "spear/iron_pike", "Iron Pike", "Iron Pike")
            ),
            Material.DIAMOND_SPEAR, List.of(
                    new ItemVariant(ItemRarity.COMMON, "spear/diamond_glaive", "Diamond Glaive", "Diamond Glaive"),
                    new ItemVariant(ItemRarity.RARE, "spear/diamond_halberd", "Diamond Halberd", "Diamond Halberd"),
                    new ItemVariant(ItemRarity.COMMON, "spear/diamond_javelin", "Diamond Javelin", "Diamond Javelin"),
                    new ItemVariant(ItemRarity.RARE, "spear/diamond_lance", "Diamond Lance", "Diamond Lance"),
                    new ItemVariant(ItemRarity.COMMON, "spear/diamond_pike", "Diamond Pike", "Diamond Pike")
            )
    );

    private static List<ItemVariant> getItemVariants(Material material, ItemRarity rarity) {
        return ITEM_VARIANTS.getOrDefault(material, List.of()).stream().filter(i -> i.rarity == rarity).toList();
    }

    public static ItemStack getRandomItem(Material material, ItemRarity rarity) {
        List<ItemVariant> variants = getItemVariants(material, rarity);
        ItemVariant selected = variants.get(RANDOM.nextInt(variants.size()));
        return selected.getItem(material);
    }

    public static ItemStack getRandomEnchantedTool(
            Material toolMaterial, Set<Enchantment> disallowed) {
        // Ensure it's a valid tool type
        if (!toolMaterial.name().endsWith("_SWORD")
                && !toolMaterial.name().endsWith("_PICKAXE")
                && !toolMaterial.name().endsWith("_AXE")
                && !toolMaterial.name().endsWith("_SHOVEL")
                && !toolMaterial.name().endsWith("_HOE")) {
            throw new IllegalArgumentException("Material is not a tool: " + toolMaterial);
        }

        ItemStack item = new ItemStack(toolMaterial);
        ItemMeta meta = item.getItemMeta();

        List<Enchantment> possible = new ArrayList<>();
        String name = toolMaterial.name();

        // --- Populate enchantments by tool type (Paper compatible) ---
        if (name.endsWith("_SWORD")) {
            possible.add(Enchantment.SHARPNESS);
            possible.add(Enchantment.KNOCKBACK);
            possible.add(Enchantment.FIRE_ASPECT);
            possible.add(Enchantment.LOOTING);
            possible.add(Enchantment.SWEEPING_EDGE);
        }

        if (name.endsWith("_PICKAXE")) {
            possible.add(Enchantment.EFFICIENCY);
            possible.add(Enchantment.SILK_TOUCH);
            possible.add(Enchantment.FORTUNE);
            possible.add(Enchantment.UNBREAKING);
            possible.add(Enchantment.MENDING);
        }

        if (name.endsWith("_AXE")) {
            possible.add(Enchantment.EFFICIENCY);
            possible.add(Enchantment.SHARPNESS);
            possible.add(Enchantment.UNBREAKING);
            possible.add(Enchantment.MENDING);
        }

        if (name.endsWith("_SHOVEL")) {
            possible.add(Enchantment.EFFICIENCY);
            possible.add(Enchantment.SILK_TOUCH);
            possible.add(Enchantment.UNBREAKING);
            possible.add(Enchantment.MENDING);
        }

        if (name.endsWith("_HOE")) {
            possible.add(Enchantment.UNBREAKING);
            possible.add(Enchantment.MENDING);
        }

        // --- Remove disallowed enchants ---
        if (disallowed != null && !disallowed.isEmpty()) {
            possible.removeIf(disallowed::contains);
        }

        // If no enchants remain, just return the tool
        if (possible.isEmpty()) {
            item.setItemMeta(meta);
            return item;
        }

        // --- Random enchant count (1–3) ---
        int enchantCount = 1 + (int) (Math.random() * 3);
        Collections.shuffle(possible);

        for (int i = 0; i < enchantCount && i < possible.size(); i++) {
            Enchantment ench = possible.get(i);
            int level = 1 + (int) (Math.random() * ench.getMaxLevel());
            meta.addEnchant(ench, level, true); // Paper supports unsafe=true
        }

        item.setItemMeta(meta);
        return item;
    }

    private static class ItemVariant {
        public ItemRarity rarity;
        public String path;
        public String name;
        public String lore;

        public ItemVariant(ItemRarity rarity, String path, String name, String lore) {
            this.rarity = rarity;
            this.path = path;
            this.name = name;
            this.lore = lore;
        }

        public ItemStack getItem(Material material) {
            ItemStack stack = new ItemStack(material);
            ItemMeta meta = stack.getItemMeta();
            meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));
            meta.setItemModel(new NamespacedKey("thegreatwork", path));
//            if (!lore.isEmpty())
//                meta.lore(List.of(Component.text("Skin: " + lore).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
            stack.setItemMeta(meta);
            return stack;
        }
    }

    public enum ItemRarity {
        COMMON, UNCOMMON, RARE, LEGENDARY
    }
}
