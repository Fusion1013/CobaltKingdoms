package se.fusion1013.cobaltKingdoms.loot;

import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.manager.registry.FileLoadedRegistry;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

import java.util.List;

public class LootManager extends Manager<CobaltKingdoms> {

    private static final FileLoadedRegistry<ILootDistribution> LOOT_DISTRIBUTIONS = new FileLoadedRegistry<>(
            CobaltKingdoms.getInstance(),
            "loot_distributions",
            LootDistribution::new,
            LootDistribution::new,
            (p, l) -> {
            }
    );

    public LootManager(CobaltKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        LOOT_DISTRIBUTIONS.reload();
    }

    @Override
    public void disable() {

    }

    public static List<ItemStack> getItems(String key) {
        return LOOT_DISTRIBUTIONS.get(key).getItems();
    }

    public static String[] getLootDistributionNames() {
        return LOOT_DISTRIBUTIONS.getNames();
    }
}
