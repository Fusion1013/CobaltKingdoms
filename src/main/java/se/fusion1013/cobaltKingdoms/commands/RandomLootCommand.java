package se.fusion1013.cobaltKingdoms.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomLootCommand {

    public static void register() {
        new CommandAPICommand("random_loot")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "random_loot"))
                .withArguments(new DoubleArgument("scaling"))
                .withOptionalArguments(new BooleanArgument("clear"))
                .withOptionalArguments(new LocationArgument("location", LocationType.BLOCK_POSITION))
                .executesPlayer(RandomLootCommand::insertRandomLoot)
                .register();
    }

    private static void insertRandomLoot(Player player, CommandArguments args) {
        Location location = args.get("location") != null ? (Location) args.get("location") : getTargetBlock(player, 5).getLocation();
        double scaling = (double) args.get("scaling");
        boolean clear = (boolean) args.getOrDefault("clear", false);
        int amount = distributeLootToChest(location, player, scaling, clear);
        LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.random_loot.insert", StringPlaceholders.builder()
                .addPlaceholder("amount", amount).build());
    }

    private static int distributeLootToChest(Location chestLocation, Player player, double lootFactor, boolean clearInventory) {
        if (chestLocation == null || player == null) return 0;

        Block block = chestLocation.getBlock();
        if (!(block.getState() instanceof InventoryHolder chest)) return 0;

        if (clearInventory) chest.getInventory().clear();

        Inventory chestInv = chest.getInventory();
        Inventory playerInv = player.getInventory();

        // Collect hotbar items (slots 0–8)
        List<ItemStack> hotbarItems = new ArrayList<>();
        int totalWeight = 0;

        for (int i = 0; i < 9; i++) {
            ItemStack item = playerInv.getItem(i);
            if (item != null && item.getType() != Material.AIR && item.getAmount() > 0) {
                hotbarItems.add(item.clone());
                totalWeight += item.getAmount(); // weight = stack size
            }
        }

        if (hotbarItems.isEmpty() || totalWeight == 0) return 0;

        Random random = new Random();

        // Determine number of loot rolls based on lootFactor
        int rolls = Math.max(1, (int) Math.round(lootFactor * hotbarItems.size()));

        int itemAmount = 0;

        for (int i = 0; i < rolls; i++) {
            int r = random.nextInt(totalWeight);
            int cumulative = 0;

            for (ItemStack item : hotbarItems) {
                cumulative += item.getAmount();

                if (r < cumulative) {
                    // Determine how many items to add (scaled by lootFactor)
                    int maxAmount = Math.max(1, (int) Math.round(item.getAmount() * lootFactor));
                    int amountToAdd = 1 + random.nextInt(maxAmount);

                    ItemStack lootItem = item.clone();
                    int amount = Math.min(amountToAdd, lootItem.getMaxStackSize());
                    itemAmount += amount;
                    lootItem.setAmount(amount);

                    addItemRandomly(chestInv, lootItem, random);
                    break;
                }
            }
        }

        return itemAmount;
    }

    private static void addItemRandomly(Inventory inventory, ItemStack item, Random random) {
        if (inventory == null || item == null || item.getType() == Material.AIR) return;

        int remaining = item.getAmount();
        int maxStack = item.getMaxStackSize();

        List<Integer> availableSlots = new ArrayList<>();

        // Collect all valid slots (empty or same type & not full)
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack current = inventory.getItem(i);

            if (current == null || current.getType() == Material.AIR) {
                availableSlots.add(i);
            } else if (current.isSimilar(item) && current.getAmount() < current.getMaxStackSize()) {
                availableSlots.add(i);
            }
        }

        if (availableSlots.isEmpty()) return;

        Collections.shuffle(availableSlots, random);

        for (int slot : availableSlots) {
            if (remaining <= 0) break;

            ItemStack current = inventory.getItem(slot);

            int space;
            if (current == null || current.getType() == Material.AIR) {
                space = maxStack;
            } else {
                space = current.getMaxStackSize() - current.getAmount();
            }

            if (space <= 0) continue;

            // Randomize how much goes into this slot
            int amountToPlace = 1 + random.nextInt(Math.min(space, remaining));

            if (current == null || current.getType() == Material.AIR) {
                ItemStack newStack = item.clone();
                newStack.setAmount(amountToPlace);
                inventory.setItem(slot, newStack);
            } else {
                current.setAmount(current.getAmount() + amountToPlace);
            }

            remaining -= amountToPlace;
        }
    }

    public static Block getTargetBlock(Player player, double maxDistance) {
        if (player == null) return null;

        RayTraceResult result = player.rayTraceBlocks(maxDistance);

        if (result == null) return null;

        return result.getHitBlock();
    }

}
