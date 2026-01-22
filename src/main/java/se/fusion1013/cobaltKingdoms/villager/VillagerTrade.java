package se.fusion1013.cobaltKingdoms.villager;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

import java.util.List;

public record VillagerTrade(List<TradeEntry> ingredients, TradeEntry result) {

    public MerchantRecipe createRecipe() {
        // Convert result item
        CostItemData result = makeItem(result());
        ItemStack resultItem = result.item();
        if (resultItem.isEmpty()) {
            CobaltKingdoms.getInstance().getLogger().warning("Result item empty, using default: " + result.item() + ". Cost Multiplier: " + result.costMultiplier());
            resultItem = new ItemStack(Material.TEST_INSTANCE_BLOCK, 1);
        }

        return getMerchantRecipe(resultItem, result);
    }

    private @NotNull MerchantRecipe getMerchantRecipe(ItemStack resultItem, CostItemData result) {
        MerchantRecipe recipe = new MerchantRecipe(resultItem, 2); // max uses

        // Convert ingredient items
        for (TradeEntry ingredient : ingredients()) {
            ItemStack ingredientStack = makeItem(ingredient).item();
            ingredientStack.setAmount(Math.max(ingredientStack.getAmount() * result.costMultiplier(), 1));

            if (ingredientStack.isEmpty()) {
                ingredientStack = new ItemStack(Material.TEST_BLOCK, 64);
                CobaltKingdoms.getInstance().getLogger().warning("Could not get itemstack for trade " + this + ". Using test blocks instead.");
            }

            recipe.addIngredient(ingredientStack);
        }
        return recipe;
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
            if (stack != null && !stack.isEmpty()) {
                stack = stack.clone();
                stack.setAmount(entry.amount());
                return new CostItemData(1, stack);
            }
            CobaltKingdoms.getInstance().getLogger().warning("Could not find item " + entry.item() + ". Using test block instead");
            mat = Material.TEST_BLOCK; // fallback to avoid null
        }

        return new CostItemData(1, new ItemStack(mat, entry.amount()));
    }

    @Override
    public String toString() {
        return "VillagerTrade{" +
                "ingredients=" + ingredients +
                ", result=" + result +
                '}';
    }
}