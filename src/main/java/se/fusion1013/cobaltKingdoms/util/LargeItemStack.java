package se.fusion1013.cobaltKingdoms.util;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record LargeItemStack(ItemStack item, int amount) {

    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();

        int remaining = amount;

        while (remaining > 0) {
            int stackSize = Math.min(64, remaining);

            ItemStack stack = item.clone();
            stack.setAmount(stackSize);

            items.add(stack);

            remaining -= stackSize;
        }

        return items;
    }

    public static List<LargeItemStack> toLargeItemStacks(List<ItemStack> items) {
        Map<ItemStack, Integer> amounts = new HashMap<>();

        for (ItemStack item : items) {
            ItemStack key = item.clone();
            key.setAmount(1); // ignore stack size when grouping

            amounts.merge(key, item.getAmount(), Integer::sum);
        }

        List<LargeItemStack> result = new ArrayList<>();

        for (Map.Entry<ItemStack, Integer> entry : amounts.entrySet()) {
            result.add(new LargeItemStack(entry.getKey(), entry.getValue()));
        }

        return result;
    }

}
