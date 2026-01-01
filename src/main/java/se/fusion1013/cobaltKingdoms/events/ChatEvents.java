package se.fusion1013.cobaltKingdoms.events;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.meta.ItemMeta;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.HexUtils;
import se.fusion1013.cobaltCore.util.PlayerUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;

public class ChatEvents implements Listener {

    @EventHandler
    public void anvilNameEvent(InventoryClickEvent event) {

        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getType() == Material.AIR) return;

        if (event.getInventory().getType() == InventoryType.ANVIL) {
            if (event.getSlotType() == InventoryType.SlotType.RESULT) {
                ItemMeta meta = event.getCurrentItem().getItemMeta();
                if (meta == null) return;

                meta.setDisplayName(HexUtils.colorify(meta.getDisplayName()));
                event.getCurrentItem().setItemMeta(meta);
            }
        }
    }

    @EventHandler
    public void onChatEvent(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        if (!sender.hasPermission("cobalt.kingdoms.chat")) {
            event.setCancelled(true);
            sender.sendMessage(Component.text("You are not allowed to do that"));
        }

        event.setCancelled(true); // Cancel event and handle message handling separately to prevent lag spikes

        String message = event.getMessage();
        Player player = event.getPlayer();

        LocaleManager localeManager = LocaleManager.getInstance();

        // Color message
        message = HexUtils.colorify(message);

        // -- Player Mentions

        // Staff Chat
        if (player.hasPermission("cobalt.kingdoms.staffchat") && message.charAt(0) == ',' && message.length() > 1) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("player", player.getName())
                    .addPlaceholder("message", message.substring(1)).build();

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("cobalt.kingdoms.staffchat")) {
                    localeManager.sendMessage("", p, "message.staffchat", placeholders);
                    if (p != player) p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 100, 1);
                }
            }
        } else {
            // Format personal message & Send Message to all recipients
            for (Player p : event.getRecipients()) {
                String playerMessage = message;

                // Split the message into words for analysis
                String[] words = ChatColor.stripColor(message).split("[^a-zA-Z0-9_]");

                // Analyze the words of the message to apply settings to
                for (String word : words) {
                    if (PlayerUtil.isMatch(p, word, 3)) {
                        // If name_highlighting is set to true then highlight the matched words
                        String regex = "(?i)(?<=\\b|[^A-Z0-9_]|§[0-9A-FK-OR])" + word + "(?=[^A-Z0-9_]|\\b)";
                        playerMessage = playerMessage.replaceAll(regex, HexUtils.colorify("&b" + word + "&r"));
                        // If alert_sounds is set to true then make a sound ONCE if a match was found
                        p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 0);
                    }
                }

                // Send the message
                p.sendMessage(HexUtils.colorify("<" + sender.getName() + "&r> ") + playerMessage);
            }
        }
    }
}
