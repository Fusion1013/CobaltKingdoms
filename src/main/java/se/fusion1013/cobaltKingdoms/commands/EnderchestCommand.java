package se.fusion1013.cobaltKingdoms.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;

import java.util.Objects;

public class EnderchestCommand {

    public static void register() {
        new CommandAPICommand("enderchest")
                .withPermission("cobalt.kingdoms.enderchest")
                .withOptionalArguments(new EntitySelectorArgument.OnePlayer("player"))
                .executesPlayer(EnderchestCommand::openPlayerEchest)
                .register();
    }

    private static void openPlayerEchest(Player player, CommandArguments commandArguments) {
        Player targetPlayer = (Player) commandArguments.get("player");
        player.openInventory(Objects.requireNonNullElse(targetPlayer, player).getEnderChest());
    }

}
