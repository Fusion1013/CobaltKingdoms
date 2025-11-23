package se.fusion1013.cobaltKingdoms.events;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.item.CustomItemManager;

import java.util.Random;

public class FishingEvents implements Listener {

    private static final Random random = new Random();

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {

        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;

        Entity caught = event.getCaught();
        if (!(caught instanceof Item item)) return;

        Material type = item.getItemStack().getType();

        // Remove enchanted books & bows
        if (type == Material.ENCHANTED_BOOK) {
            ItemStack stack = CustomItemManager.getCustomItemStack("lun_coin");
            stack.setAmount(random.nextInt(0, 4));
            item.setItemStack(stack);
        }

        if (type == Material.BOW) {
            ItemStack stack = CustomItemManager.getCustomItemStack("sol_coin");
            stack.setAmount(random.nextInt(0, 4));
            item.setItemStack(stack);
        }
    }
}
