package se.fusion1013.cobaltKingdoms.quest.model;

import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.item.CustomItemManager;

import java.util.Objects;

public record QuestItem(String itemName, String category, int minQuantity, int maxQuantity, double valuePerItem) {

    public ItemStack item() {
        return getItem().clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestItem tradeItem = (QuestItem) o;
        return minQuantity == tradeItem.minQuantity &&
                maxQuantity == tradeItem.maxQuantity &&
                Double.compare(tradeItem.valuePerItem, valuePerItem) == 0 &&
                getItem().isSimilar(tradeItem.getItem()) &&
                Objects.equals(category, tradeItem.category);
    }

    public ItemStack getItem() {
        return CustomItemManager.getItemStack(itemName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemName, category, minQuantity, maxQuantity, valuePerItem);
    }

}
