package se.fusion1013.cobaltKingdoms.quest;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.quest.gui.Button;
import se.fusion1013.cobaltKingdoms.quest.gui.Menu;

public class QuestMenuListener extends Manager<CobaltKingdoms> implements Listener {

    public static final String IN_MENU_METADATA = "InTradeRoutesMenu";

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final int slot = event.getSlot();

        if (player.hasMetadata(IN_MENU_METADATA)) {
            final Menu menu = (Menu) player.getMetadata(IN_MENU_METADATA).getFirst().value();
            for (final Button button : menu.getButtons())
                if (button.getSlot() == slot) {
                    button.onClick(player);
                    event.setCancelled(true);
                }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        if (player.hasMetadata(IN_MENU_METADATA)) {
            player.removeMetadata(IN_MENU_METADATA, CobaltKingdoms.getInstance());
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (player.hasMetadata(IN_MENU_METADATA)) {
            player.removeMetadata(IN_MENU_METADATA, CobaltKingdoms.getInstance());
        }
    }

    public QuestMenuListener(CobaltKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltKingdoms.getInstance());
    }

    @Override
    public void disable() {

    }
}
