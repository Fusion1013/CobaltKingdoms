package se.fusion1013.cobaltKingdoms.villager;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

import java.util.ArrayList;
import java.util.List;

public class VillagerEvents implements Listener {

    @EventHandler
    public void onVillagerProfessionChange(VillagerCareerChangeEvent event) {

        if (event.getProfession() == Villager.Profession.CARTOGRAPHER) {
            event.getEntity().customName(Component.text("Banker"));
            event.getEntity().setCustomNameVisible(false);
        }

        Bukkit.getScheduler().runTask(CobaltKingdoms.getInstance(), () -> {
            setTrades(event);
        });
    }

    private boolean setTrades(VillagerCareerChangeEvent event) {
        Villager villager = event.getEntity();
        Villager.Profession newProfession = event.getProfession();

        // Lookup trades for this profession
        List<VillagerTrade> trades = VillagerManager.getTrades(newProfession);

        // No custom trades? do nothing
        if (trades == null || trades.isEmpty()) return true;

        // Convert our saved trades into MerchantRecipes
        List<MerchantRecipe> recipes = new ArrayList<>();

        for (VillagerTrade trade : trades) {

            // Convert result item
            ItemStack result = makeItem(trade.result);

            MerchantRecipe recipe = new MerchantRecipe(result, 9999); // max uses

            // Convert ingredient items
            for (TradeEntry ingredient : trade.ingredients) {
                recipe.addIngredient(makeItem(ingredient));
                villager.setVillagerLevel(5);
                villager.setVillagerExperience(250);
            }

            recipes.add(recipe);
        }

        // Apply trades to villager
        villager.setRecipes(recipes);
        return false;
    }

    private ItemStack makeItem(TradeEntry entry) {

        Material mat = Material.matchMaterial(entry.item);
        if (mat == null) {
            ItemStack stack = CustomItemManager.getCustomItemStack(entry.item);
            if (stack != null) {
                stack = stack.clone();
                stack.setAmount(entry.amount);
                return stack;
            }
            mat = Material.STONE; // fallback to avoid null
        }

        return new ItemStack(mat, entry.amount);
    }
}
