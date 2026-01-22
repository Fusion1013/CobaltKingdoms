package se.fusion1013.cobaltKingdoms.villager;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.bukkit.inventory.MerchantRecipe;
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

        if (event.getProfession() == Villager.Profession.NONE) return;
        if (event.getProfession() == Villager.Profession.NITWIT) return;

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
        trades.forEach(trade -> createMerchantTrade(trade, recipes));
        villager.setRecipes(recipes);
    }

    private void createMerchantTrade(VillagerTrade trade, List<MerchantRecipe> recipes) {
        try {
            MerchantRecipe recipe = trade.createRecipe();
            recipes.add(recipe);
        } catch (Exception e) {
            CobaltKingdoms.getInstance().getLogger().warning("Error creating villager recipe: " + e.getMessage());
        }
    }
}
