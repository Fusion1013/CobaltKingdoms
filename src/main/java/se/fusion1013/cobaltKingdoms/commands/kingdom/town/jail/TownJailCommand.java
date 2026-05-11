package se.fusion1013.cobaltKingdoms.commands.kingdom.town.jail;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownManager;

public class TownJailCommand {

    public static Argument<String> JAIL_NAME_ARGUMENT = new StringArgument("jail")
            .replaceSuggestions(ArgumentSuggestions.strings(k -> TownManager.getInstance().getJailNames((Player) k.sender())));

    public static CommandAPICommand register() {
        return new CommandAPICommand("jail")
                .withSubcommand(TownJailDeleteCommand.register())
                .withSubcommand(TownJailInfoCommand.register())
                .withSubcommand(TownJailListCommand.register())
                .withSubcommand(TownJailCreateCommand.register());
    }
}
