package se.fusion1013.cobaltKingdoms.events;

import org.bukkit.Material;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import se.fusion1013.cobaltKingdoms.items.KingdomItems;

import java.util.List;

public class VillagerEvents implements Listener {

    @EventHandler
    public void onVillagerTradeAcquire(VillagerAcquireTradeEvent event) {
        Villager villager = (Villager) event.getEntity();
        switch (villager.getProfession().getKey().getKey()) {
            case "armorer" -> updateArmorerTrades(event);
            default -> throw new IllegalStateException("Unexpected value: " + villager.getProfession());
        }
    }

    private void updateArmorerTrades(VillagerAcquireTradeEvent event) {
        MerchantRecipe oldRecipe = event.getRecipe();
        List<ItemStack> ingredients = oldRecipe.getIngredients();

        for (int i = 0; i < ingredients.size(); i++) {
            ItemStack stack = ingredients.get(i);
            if (stack.getType() == Material.EMERALD) {
                ItemStack newStack = KingdomItems.GOLD_COIN.getItemStack();
                newStack.setAmount(stack.getAmount());
                ingredients.set(i, newStack);
            }
        }

        MerchantRecipe newRecipe = new MerchantRecipe(oldRecipe);
        newRecipe.setIngredients(ingredients);
        event.setRecipe(newRecipe);
    }

}
