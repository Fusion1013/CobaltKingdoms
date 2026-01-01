package se.fusion1013.cobaltKingdoms.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;

import java.util.Objects;

public class InvseeCommand {

    public static void register() {
        new CommandAPICommand("invsee")
                .withPermission("cobalt.kingdoms.invsee")
                .withOptionalArguments(new EntitySelectorArgument.OnePlayer("player"))
                .executesPlayer(InvseeCommand::openPlayerInventory)
                .register();
    }

    private static void openPlayerInventory(Player player, CommandArguments commandArguments) {
        Player targetPlayer = (Player) commandArguments.get("player");
        player.openInventory(Objects.requireNonNullElse(targetPlayer, player).getInventory());
    }

}
