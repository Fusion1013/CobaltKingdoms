package se.fusion1013.cobaltKingdoms.villager;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
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

        // This is dumb
        if (event.getProfession() == Villager.Profession.ARMORER)
            event.getEntity().customName(Component.text("Armorer"));
        if (event.getProfession() == Villager.Profession.BUTCHER)
            event.getEntity().customName(Component.text("Butcher"));
        if (event.getProfession() == Villager.Profession.CARTOGRAPHER)
            event.getEntity().customName(Component.text("Banker"));
        if (event.getProfession() == Villager.Profession.FARMER) event.getEntity().customName(Component.text("Farmer"));
        if (event.getProfession() == Villager.Profession.FISHERMAN)
            event.getEntity().customName(Component.text("Fisherman"));
        if (event.getProfession() == Villager.Profession.FLETCHER)
            event.getEntity().customName(Component.text("Fletcher"));
        if (event.getProfession() == Villager.Profession.LEATHERWORKER)
            event.getEntity().customName(Component.text("Leatherworker"));
        if (event.getProfession() == Villager.Profession.LIBRARIAN)
            event.getEntity().customName(Component.text("Librarian"));
        if (event.getProfession() == Villager.Profession.MASON) event.getEntity().customName(Component.text("Mason"));
        if (event.getProfession() == Villager.Profession.SHEPHERD)
            event.getEntity().customName(Component.text("Shepherd"));
        if (event.getProfession() == Villager.Profession.TOOLSMITH)
            event.getEntity().customName(Component.text("Toolsmith"));
        if (event.getProfession() == Villager.Profession.WEAPONSMITH)
            event.getEntity().customName(Component.text("Weaponsmith"));

        event.getEntity().setCustomNameVisible(false);

        Bukkit.getScheduler().runTask(CobaltKingdoms.getInstance(), () -> {
            if (event.getEntity().getVillagerLevel() > 2) return;
            setTrades(event.getEntity(), event.getProfession().getKey().getKey());
            event.getEntity().setVillagerLevel(5);
            event.getEntity().setVillagerExperience(250);
        });
    }

    @EventHandler
    public void onTraderSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof WanderingTrader trader)) return;

        Bukkit.getScheduler().runTask(CobaltKingdoms.getInstance(), () -> {
            setTrades(trader, "wandering_trader");
        });
    }

    private void setTrades(AbstractVillager villager, String key) {
        List<VillagerTrade> trades = VillagerManager.getTrades(key);
        if (trades == null || trades.isEmpty()) return;
        List<MerchantRecipe> recipes = new ArrayList<>();

        for (VillagerTrade trade : trades) {

            // Convert result item
            CostItemData result = makeItem(trade.result);

            MerchantRecipe recipe = new MerchantRecipe(result.item(), 2); // max uses

            // Convert ingredient items
            for (TradeEntry ingredient : trade.ingredients) {
                ItemStack ingredientStack = makeItem(ingredient).item();
                ingredientStack.setAmount(ingredientStack.getAmount() * result.costMultiplier());
                recipe.addIngredient(ingredientStack);
            }
            recipes.add(recipe);
        }

        // Apply trades to villager
        villager.setRecipes(recipes);
    }

    private CostItemData makeItem(TradeEntry entry) {

        // If this entry represents a function call
        if (entry.isFunction()) {
            String functionName = entry.item().substring(1, entry.item().length() - 1); // strip {}
            ITradeFunction fn = VillagerManager.getTradeFunction(functionName);
            if (fn != null) {
                CostItemData costItemData = fn.createItem();
                costItemData.item().setAmount(entry.amount());
                return costItemData;
            }
        }

        Material mat = Material.matchMaterial(entry.item());
        if (mat == null) {
            ItemStack stack = CustomItemManager.getCustomItemStack(entry.item());
            if (stack != null) {
                stack = stack.clone();
                stack.setAmount(entry.amount());
                return new CostItemData(1, stack);
            }
            mat = Material.STONE; // fallback to avoid null
        }

        return new CostItemData(1, new ItemStack(mat, entry.amount()));
    }
}
