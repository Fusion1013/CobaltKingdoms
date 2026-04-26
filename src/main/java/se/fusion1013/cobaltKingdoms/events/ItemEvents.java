package se.fusion1013.cobaltKingdoms.events;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.item.ICustomItem;
import se.fusion1013.cobaltCore.util.BlockUtil;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

import java.util.Random;

public class ItemEvents implements Listener {

    private static final Random random = new Random();

    @EventHandler
    public void onArmorStandInteract(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof ArmorStand stand)) return;

        Player player = event.getPlayer();

        if (!player.isSneaking()) return;

        event.setCancelled(true);

        EntityEquipment standEq = stand.getEquipment();
        EntityEquipment playerEq = player.getEquipment();

        if (standEq == null || playerEq == null) return;

        swapIfUnlocked(stand, standEq, playerEq, EquipmentSlot.HEAD);
        swapIfUnlocked(stand, standEq, playerEq, EquipmentSlot.CHEST);
        swapIfUnlocked(stand, standEq, playerEq, EquipmentSlot.LEGS);
        swapIfUnlocked(stand, standEq, playerEq, EquipmentSlot.FEET);
        swapIfUnlocked(stand, standEq, playerEq, EquipmentSlot.HAND);
        swapIfUnlocked(stand, standEq, playerEq, EquipmentSlot.OFF_HAND);
    }

    private void swapIfUnlocked(ArmorStand stand, EntityEquipment standEq, EntityEquipment playerEq, EquipmentSlot slot) {
        // If the slot is locked, do nothing
        if (stand.hasEquipmentLock(slot, ArmorStand.LockType.REMOVING_OR_CHANGING)) {
            return;
        }

        ItemStack standItem = standEq.getItem(slot);
        ItemStack playerItem = playerEq.getItem(slot);

        standEq.setItem(slot, playerItem);
        playerEq.setItem(slot, standItem);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        tryBoneMealFlower(event);
        blockHatPlace(event);
        tryCopyLectern(event);
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        tryDropMannequin(event);
    }

    private void tryDropMannequin(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;

        for (int i = event.getPlayer().getPassengers().size() - 1; i >= 0; i--) {
            Entity entity = event.getPlayer().getPassengers().get(i);
            if (entity instanceof Mannequin mannequin) {
                if (!mannequin.getScoreboardTags().contains("passenger")) continue;
                event.getPlayer().removePassenger(mannequin);
                mannequin.setPose(Pose.SWIMMING);
            }
        }
    }

    @EventHandler
    public void tryPickUpMannequin(PlayerInteractAtEntityEvent event) {
        if (!event.getPlayer().isSneaking()) return;
        if (!(event.getRightClicked() instanceof Mannequin mannequin)) return;
        if (mannequin.getPose() != Pose.SLEEPING && mannequin.getPose() != Pose.SWIMMING) return;
        if (!mannequin.getScoreboardTags().contains("passenger")) return;

        event.getPlayer().addPassenger(mannequin);
        mannequin.setPose(Pose.SWIMMING);
    }

    private void tryCopyLectern(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!event.getPlayer().isSneaking()) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.LECTERN) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.WRITABLE_BOOK) return;

        ItemStack lecternItem = BlockUtil.getLecternItem(block);
        if (lecternItem == null) return;

        event.getPlayer().getInventory().setItemInMainHand(lecternItem);
        event.getPlayer().playSound(block.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        event.setCancelled(true);
        Bukkit.getScheduler().runTaskLater(CobaltKingdoms.getInstance(), () -> {
            event.getPlayer().closeInventory();
        }, 1);
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
