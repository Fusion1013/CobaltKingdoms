package se.fusion1013.cobaltKingdoms.loot;

import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.manager.registry.IRegistryItem;

import java.util.List;

public interface ILootDistribution extends IRegistryItem {

    List<ItemStack> getItems();

}
