package se.fusion1013.cobaltKingdoms.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

public class GetCompassCommand {

    public static void register() {
        new CommandAPICommand("compass")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "compass"))
                .executesPlayer(GetCompassCommand::getCompass)
                .register();
    }

    private static void getCompass(Player player, CommandArguments args) {
        Block block = RandomLootCommand.getTargetBlock(player, 8);
        if (block == null) return;

        if (block.getType() != Material.LODESTONE) return;

        ItemStack item = new ItemStack(Material.COMPASS);
        CompassMeta meta = (CompassMeta) item.getItemMeta();
        meta.setLodestone(block.getLocation());
        meta.setLodestoneTracked(true);
        item.setItemMeta(meta);
        player.give(item);
    }

}
