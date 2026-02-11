package se.fusion1013.cobaltKingdoms.events;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.item.ICustomItem;

import java.util.Random;

public class ItemEvents implements Listener {

    private static final Random random = new Random();

    @EventHandler
    public void onPlayerClickFlower(PlayerInteractEvent event) {
        tryBoneMealFlower(event);
        blockHatPlace(event);
    }

    private static void blockHatPlace(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;
        if (item.getType() != Material.CARVED_PUMPKIN) return;
        ItemMeta meta = item.getItemMeta();
        if (!meta.getItemModel().namespace().contains("thegreatwork")) return;
        if (!meta.getItemModel().getKey().contains("hat")) return;
        event.setCancelled(true);
    }

    private static void tryBoneMealFlower(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) return;

        if (!isFlowerThatCantBeBonemealed(block)) return;
        if (block.getType() == Material.WITHER_ROSE) return;
        ItemStack heldItem = event.getPlayer().getInventory().getItem(event.getHand());
        if (heldItem.getType() != Material.BONE_MEAL) return;

        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) heldItem.setAmount(heldItem.getAmount() - 1);
        event.getPlayer().swingHand(event.getHand());

        // Spawn flower item

        World world = block.getWorld();
        ItemStack item = new ItemStack(block.getType(), 1);
        world.dropItemNaturally(event.getInteractionPoint(), item);
        world.playSound(event.getInteractionPoint(), Sound.ITEM_BONE_MEAL_USE, 1, 1);
        world.spawnParticle(Particle.HAPPY_VILLAGER, event.getInteractionPoint(), 10, .3, .3, .3, 0);
    }

    @EventHandler
    public void onItemPickUpEvent(PlayerAttemptPickupItemEvent event) {
        ItemStack itemStack = event.getItem().getItemStack();
        ICustomItem customItem = CustomItemManager.getCustomItem(itemStack);
        if (customItem == null) return;
        if (customItem.getInternalName().contains("_coin")) {
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1, random.nextFloat(1.8f, 2.2f));
        }
    }

    private static boolean isFlowerThatCantBeBonemealed(Block block) {
        Material type = block.getType();

        return Tag.SMALL_FLOWERS.isTagged(type);
    }
}
