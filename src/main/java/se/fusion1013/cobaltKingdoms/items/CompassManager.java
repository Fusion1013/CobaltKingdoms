package se.fusion1013.cobaltKingdoms.items;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

public class CompassManager extends Manager<CobaltKingdoms> implements Listener {

    public CompassManager(CobaltKingdoms plugin) {
        super(plugin);
        INSTANCE = this;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        sendLodestoneCompassToChat(event);
    }

    private void sendLodestoneCompassToChat(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;

        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (heldItem.getType() != Material.COMPASS || !player.isSneaking()) return;

        CompassMeta meta = (CompassMeta) heldItem.getItemMeta();
        if (meta == null) return;
        Location lodestoneLocation = meta.getLodestone();
        if (lodestoneLocation == null) return;

        if (lodestoneLocation.getWorld() != player.getWorld()) return;

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.PLAYERS, 1, 1);
        double distance = Math.round(10 * player.getLocation().distance(lodestoneLocation)) / 10.0;
        LocaleManager.getInstance().sendMessage(plugin, player, "kingdoms.items.compass.distance", StringPlaceholders.builder()
                .addPlaceholder("distance", distance)
                .build());
    }

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {

    }

    private static CompassManager INSTANCE;

    public static CompassManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CompassManager(CobaltKingdoms.getInstance());
        }
        return INSTANCE;
    }
}
