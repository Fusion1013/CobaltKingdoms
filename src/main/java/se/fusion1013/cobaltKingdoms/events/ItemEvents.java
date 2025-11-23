package se.fusion1013.cobaltKingdoms.events;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.item.ICustomItem;

import java.util.Random;

public class ItemEvents implements Listener {

    private static final Random random = new Random();

    @EventHandler
    public void onItemPickUpEvent(PlayerAttemptPickupItemEvent event) {
        ItemStack itemStack = event.getItem().getItemStack();
        ICustomItem customItem = CustomItemManager.getCustomItem(itemStack);
        if (customItem == null) return;
        if (customItem.getInternalName().contains("_coin")) {
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1, random.nextFloat(1.8f, 2.2f));
        }
    }
}
