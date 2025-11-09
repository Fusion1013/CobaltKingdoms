package se.fusion1013.cobaltKingdoms.events;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;

import java.util.List;

public class PortalEvents implements Listener {

    @EventHandler
    public void onPortalCreate(PortalCreateEvent event) {
        World world = event.getWorld();
        List<BlockState> blocks = event.getBlocks();

        for (BlockState block : blocks) {
            block.setType(Material.AIR);
            world.setBlockData(block.getLocation(), block.getBlockData());
            world.createExplosion(block.getLocation().toCenterLocation(), 3);
        }

        event.setCancelled(true);
    }

}
