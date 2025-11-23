package se.fusion1013.cobaltKingdoms.items;

import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.item.CobaltItem;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.item.ICustomItem;

public class KingdomItems {

    public static final ICustomItem GOLD_COIN = CustomItemManager.register(new CobaltItem.Builder("gold_coin").itemModel("thegreatwork:gold_coin").itemName("Gold Coin").build()); // TODO

    public static ItemStack goldCoins(int amount) {
        ItemStack item = GOLD_COIN.getItemStack();
        item.setAmount(amount);
        return item;
    }

    public static void register() {
    }

}
