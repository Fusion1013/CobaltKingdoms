package se.fusion1013.cobaltKingdoms.villager;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.util.FileUtil;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.util.EnchantedBookUtil;
import se.fusion1013.cobaltKingdoms.util.ItemVariantUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class VillagerManager extends Manager<CobaltKingdoms> {

    private static final Map<String, List<VillagerTrade>> PROFESSION_TRADES = new HashMap<>();
    private static final Map<String, ITradeFunction> TRADE_FUNCTIONS = new HashMap<>();

    public static ITradeFunction getTradeFunction(String name) {
        return TRADE_FUNCTIONS.getOrDefault(name, null);
    }

    public static List<VillagerTrade> getTrades(Villager.Profession profession) {
        return getTrades(profession.getKey().getKey());
    }

    public static List<VillagerTrade> getTrades(String key) {
        return PROFESSION_TRADES.getOrDefault(key, Collections.emptyList());
    }

    private void loadTrades(String fileName) {
        File file = getTradeFile(fileName);
        List<VillagerTrade> trades = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = reader.readLine()) != null) {
                loadTrade(line, trades);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        PROFESSION_TRADES.put(fileName, trades);
    }

    private static void loadTrade(String line, List<VillagerTrade> trades) {
        line = line.trim();
        if (line.isEmpty() || line.startsWith("#")) return;

        // Example: 20:raw_copper,,16:gold_coin
        String[] parts = line.split(",");

        List<TradeEntry> ingredients = new ArrayList<>();
        TradeEntry result = null;

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            if (part.isEmpty()) continue;

            String[] split = part.split(":");
            if (split.length != 2) continue;

            TradeEntry entry = parseEntry(split);
            // Last section = result
            if (i == parts.length - 1) {
                result = entry;
            } else {
                ingredients.add(entry);
            }
        }

        if (result != null) {
            trades.add(new VillagerTrade(ingredients, result));
        }
    }

    private void loadTradesForProfession(Villager.Profession profession) {
        loadTrades(profession.getKey().getKey());
    }

    private static @NotNull TradeEntry parseEntry(String[] split) {
        int amount = Integer.parseInt(split[0]);
        String item = split[1];
        boolean isFunction = item.startsWith("{") && item.endsWith("}");

        TradeEntry entry = new TradeEntry(amount, item, isFunction);
        return entry;
    }

    private File getTradeFile(String fileName) {
        return FileUtil.getOrCreateFileFromResource(
                CobaltKingdoms.getInstance(), "villager/" + fileName + "_trades.txt");
    }

    public VillagerManager(CobaltKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        registerVillagerProfessionTrades();
        registerTradeFunctions();
    }

    private void registerTradeFunctions() {
        TRADE_FUNCTIONS.clear();
        TRADE_FUNCTIONS.put(
                "random_armor_trim",
                () -> {
                    Material[] possible = {
                            Material.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE,
                            Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE,
                            Material.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE,
                            Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE,
                            Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE,
                            Material.HOST_ARMOR_TRIM_SMITHING_TEMPLATE,
                            Material.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE,
                            Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE,
                            Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE,
                            Material.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE,
                            Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE,
                            Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE,
                            Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE,
                            Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE,
                            Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE,
                            Material.WARD_ARMOR_TRIM_SMITHING_TEMPLATE,
                            Material.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE,
                            Material.WILD_ARMOR_TRIM_SMITHING_TEMPLATE
                    };

                    int[] costMultipliers = {1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 4, 1, 2, 1, 2, 2, 1, 1};

                    int choiceId = new Random().nextInt(possible.length);
                    Material choice = possible[choiceId];
                    return new CostItemData(costMultipliers[choiceId], new ItemStack(choice));
                });
        TRADE_FUNCTIONS.put(
                "random_chainmail",
                () -> {
                    Material[] possible = {
                            Material.CHAINMAIL_BOOTS,
                            Material.CHAINMAIL_CHESTPLATE,
                            Material.CHAINMAIL_HELMET,
                            Material.CHAINMAIL_LEGGINGS
                    };

                    int choiceId = new Random().nextInt(possible.length);
                    Material choice = possible[choiceId];
                    return new CostItemData(1, new ItemStack(choice));
                });
        TRADE_FUNCTIONS.put(
                "random_enchant_diamond_hoe",
                () ->
                        new CostItemData(
                                1,
                                ItemVariantUtil.getRandomEnchantedTool(
                                        Material.DIAMOND_HOE, Set.of(Enchantment.SHARPNESS, Enchantment.MENDING))));
        TRADE_FUNCTIONS.put(
                "random_enchanted_rod",
                () -> {
                    ItemStack rod = new ItemStack(Material.FISHING_ROD);
                    ItemMeta meta = rod.getItemMeta();

                    // Valid enchantments for fishing rods in Minecraft
                    List<Enchantment> possible =
                            new ArrayList<>(
                                    List.of(
                                            Enchantment.LUCK_OF_THE_SEA, // increases treasure chance
                                            Enchantment.LURE, // speeds up bite time
                                            Enchantment.UNBREAKING,
                                            Enchantment.MENDING));

                    Random random = new Random();

                    // 1–3 random enchants
                    int enchantCount = 1 + random.nextInt(3);

                    for (int i = 0; i < enchantCount && !possible.isEmpty(); i++) {

                        // Pick a random enchantment & remove so we don't repeat it
                        Enchantment chosen = possible.remove(random.nextInt(possible.size()));

                        int level = 1 + random.nextInt(chosen.getMaxLevel());

                        meta.addEnchant(chosen, level, true);
                    }

                    rod.setItemMeta(meta);
                    return new CostItemData(1, rod);
                });
        TRADE_FUNCTIONS.put(
                "random_iron_sword_skin",
                () -> new CostItemData(1, ItemVariantUtil.getRandomItem(Material.IRON_SWORD, ItemVariantUtil.ItemRarity.COMMON)));
        TRADE_FUNCTIONS.put(
                "random_iron_sword_skin_rare",
                () -> new CostItemData(1, ItemVariantUtil.getRandomItem(Material.IRON_SWORD, ItemVariantUtil.ItemRarity.RARE)));
        TRADE_FUNCTIONS.put(
                "random_diamond_sword_skin_common",
                () -> new CostItemData(1, ItemVariantUtil.getRandomItem(Material.DIAMOND_SWORD, ItemVariantUtil.ItemRarity.COMMON)));
        TRADE_FUNCTIONS.put(
                "random_iron_axe_skin",
                () -> new CostItemData(1, ItemVariantUtil.getRandomItem(Material.IRON_AXE, ItemVariantUtil.ItemRarity.COMMON)));
        TRADE_FUNCTIONS.put(
                "random_iron_axe_skin_rare",
                () -> new CostItemData(1, ItemVariantUtil.getRandomItem(Material.IRON_AXE, ItemVariantUtil.ItemRarity.RARE)));
        TRADE_FUNCTIONS.put(
                "random_banner_pattern",
                () -> {
                    Material[] possible =
                            new Material[]{
                                    Material.CREEPER_BANNER_PATTERN,
                                    Material.SKULL_BANNER_PATTERN,
                                    Material.FLOWER_BANNER_PATTERN,
                                    Material.MOJANG_BANNER_PATTERN,
                                    Material.GLOBE_BANNER_PATTERN,
                                    Material.PIGLIN_BANNER_PATTERN,
                                    Material.FLOW_BANNER_PATTERN,
                                    Material.GUSTER_BANNER_PATTERN,
                                    Material.BORDURE_INDENTED_BANNER_PATTERN,
                                    Material.FIELD_MASONED_BANNER_PATTERN
                            };
                    int[] costMultipliers = {2, 4, 1, 4, 1, 2, 4, 4, 1, 1};

                    int choiceId = new Random().nextInt(possible.length);
                    Material choice = possible[choiceId];
                    return new CostItemData(costMultipliers[choiceId], new ItemStack(choice));
                });
        TRADE_FUNCTIONS.put(
                "random_enchant_iron_tool",
                () -> {
                    Material[] possible =
                            new Material[]{
                                    Material.IRON_PICKAXE, Material.IRON_SHOVEL, Material.IRON_AXE, Material.IRON_HOE,
                            };
                    int[] costMultipliers = {2, 1, 2, 1};

                    int choiceId = new Random().nextInt(possible.length);
                    Material choice = possible[choiceId];
                    return new CostItemData(
                            costMultipliers[choiceId],
                            ItemVariantUtil.getRandomEnchantedTool(
                                    choice, Set.of(Enchantment.SHARPNESS, Enchantment.MENDING)));
                });
        TRADE_FUNCTIONS.put(
                "random_enchant_diamond_tool",
                () -> {
                    Material[] possible =
                            new Material[]{
                                    Material.DIAMOND_PICKAXE,
                                    Material.DIAMOND_SHOVEL,
                                    Material.DIAMOND_AXE,
                                    Material.DIAMOND_HOE
                            };
                    int[] costMultipliers = {2, 1, 2, 1};

                    int choiceId = new Random().nextInt(possible.length);
                    Material choice = possible[choiceId];
                    return new CostItemData(
                            costMultipliers[choiceId],
                            ItemVariantUtil.getRandomEnchantedTool(
                                    choice, Set.of(Enchantment.SHARPNESS, Enchantment.MENDING)));
                });
        TRADE_FUNCTIONS.put(
                "random_book_common",
                () -> new CostItemData(1, ItemVariantUtil.getRandomItem(Material.WRITABLE_BOOK, ItemVariantUtil.ItemRarity.COMMON))
        );
        TRADE_FUNCTIONS.put(
                "random_book_uncommon",
                () -> new CostItemData(1, ItemVariantUtil.getRandomItem(Material.WRITABLE_BOOK, ItemVariantUtil.ItemRarity.UNCOMMON))
        );
        TRADE_FUNCTIONS.put(
                "random_book_rare",
                () -> new CostItemData(1, ItemVariantUtil.getRandomItem(Material.WRITABLE_BOOK, ItemVariantUtil.ItemRarity.RARE))
        );
        TRADE_FUNCTIONS.put(
                "random_book_enchanted",
                () -> new CostItemData(1, EnchantedBookUtil.getRandomEnchantedBook(2))
        );
        TRADE_FUNCTIONS.put(
                "random_bow_skin",
                () -> new CostItemData(1, ItemVariantUtil.getRandomItem(Material.BOW, ItemVariantUtil.ItemRarity.COMMON))
        );
        TRADE_FUNCTIONS.put(
                "random_crossbow_skin",
                () -> new CostItemData(1, ItemVariantUtil.getRandomItem(Material.CROSSBOW, ItemVariantUtil.ItemRarity.COMMON))
        );
    }

    private void registerVillagerProfessionTrades() {
        loadTradesForProfession(Villager.Profession.ARMORER);
        loadTradesForProfession(Villager.Profession.BUTCHER);
        loadTradesForProfession(Villager.Profession.CARTOGRAPHER);
        loadTradesForProfession(Villager.Profession.FARMER);
        loadTradesForProfession(Villager.Profession.FISHERMAN);
        loadTradesForProfession(Villager.Profession.FLETCHER);
        loadTradesForProfession(Villager.Profession.LEATHERWORKER);
        loadTradesForProfession(Villager.Profession.LIBRARIAN);
        loadTradesForProfession(Villager.Profession.MASON);
        loadTradesForProfession(Villager.Profession.SHEPHERD);
        loadTradesForProfession(Villager.Profession.TOOLSMITH);
        loadTradesForProfession(Villager.Profession.WEAPONSMITH);
        loadTrades("wandering_trader");
    }

    @Override
    public void disable() {
    }
}
