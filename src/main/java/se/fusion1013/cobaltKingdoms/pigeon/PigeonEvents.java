package se.fusion1013.cobaltKingdoms.pigeon;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

import java.util.HashMap;
import java.util.Random;

public class PigeonEvents implements Listener {

    private static final Random rnd = new Random();

    @EventHandler
    public void onSendPigeonLetter(PlayerEditBookEvent event) {
        if (!event.isSigning()) return;

        Player sendingPlayer = event.getPlayer();
        BookMeta bookMeta = event.getNewBookMeta();

        sendLetter(event, bookMeta, sendingPlayer);
    }

    private static void sendLetter(PlayerEditBookEvent event, BookMeta bookMeta, Player sendingPlayer) {
        if (bookMeta.getItemModel() == null) return;
        if (!bookMeta.getItemModel().getKey().contains("letter")) return;

        String title = bookMeta.getTitle();
        if (title == null) return;

        // Try to find the player to send the mail to
        Player player = Bukkit.getPlayer(title);
        if (player == null) {
            // Save the letter for offline usage
            return;
        }

        if (sendingPlayer == player) {
            // Set the old book meta and reply with a fail message
            event.setNewBookMeta(event.getPreviousBookMeta());
            event.setSigning(false);
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), sendingPlayer, "kingdoms.pigeon.fail.send_to_self");
            return;
        }

        // Remove item from player inventory
        sendingPlayer.getInventory().getItemInMainHand().setAmount(0);
        sendLetter(sendingPlayer, player, bookMeta);
    }

    public static void sendLetter(Player senderPlayer, Player receiverPlayer, BookMeta content) {
        Location senderPlayerLocation = senderPlayer.getLocation();
        Location senderParrotTarget = senderPlayerLocation.clone().add(rnd.nextFloat(-10, 10), 6, rnd.nextFloat(-10, 10));

        // Send two parrots
        sendPigeon(senderPlayerLocation, senderParrotTarget);

        // Create a copy of the letter and change the title
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        content.setTitle("Letter from " + senderPlayer.getName());
        item.setItemMeta(content);

        Bukkit.getScheduler().runTaskLater(CobaltKingdoms.getInstance(), () -> {
            receiveLetter(receiverPlayer, item);
        }, 5 * 20L);
    }

    public static void sendSenderPigeon(Player senderPlayer) {
        Location senderPlayerLocation = senderPlayer.getLocation();
        Location senderParrotTarget = senderPlayerLocation.clone().add(rnd.nextFloat(-10, 10), 6, rnd.nextFloat(-10, 10));
        sendPigeon(senderPlayerLocation, senderParrotTarget);
    }

    public static void receiveLetter(Player player, ItemStack letter) {
        Location receiverPlayerLocation = player.getLocation();
        Location receiverParrotSpawn = receiverPlayerLocation.clone().add(rnd.nextFloat(-5, 5), 3, rnd.nextFloat(-5, 5));

        sendPigeon(receiverParrotSpawn, receiverPlayerLocation);

        // Give the player the letter
        Bukkit.getScheduler().runTaskLater(CobaltKingdoms.getInstance(), () -> {
            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(letter);
            if (!leftover.isEmpty()) {
                leftover.values().forEach(item -> {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                });
            }
            player.playSound(receiverPlayerLocation, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        }, 5 * 20L);
    }

    private static void sendPigeon(Location spawnLocation, Location targetLocation) {
        World world = spawnLocation.getWorld();

        Parrot parrot = (Parrot) world.spawnEntity(spawnLocation, EntityType.PARROT);
        parrot.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 10 * 20, 0, true, false));
        parrot.getPathfinder().moveTo(targetLocation);

        Bukkit.getScheduler().runTaskLater(CobaltKingdoms.getInstance(), () -> {
            removePigeon(parrot);
        }, 5 * 20L);
    }

    private static void removePigeon(Parrot parrot) {
        if (parrot.isDead()) return;

        Location location = parrot.getLocation();
        World world = location.getWorld();

        world.spawnParticle(Particle.CLOUD, location, 10, .4, .4, .4, 0.01);
        world.playSound(parrot, Sound.BLOCK_DECORATED_POT_INSERT, 1, 1);

        parrot.remove();
    }

}
