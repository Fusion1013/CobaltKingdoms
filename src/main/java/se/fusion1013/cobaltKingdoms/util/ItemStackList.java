package se.fusion1013.cobaltKingdoms.util;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemStackList {

    private final List<ItemStack> items = new ArrayList<>();

    public ItemStackList(List<ItemStack> items) {
        this.items.addAll(items);
    }

    public List<ItemStack> list() {
        return items;
    }

}
