package se.fusion1013.cobaltKingdoms.town.command.jail;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.town.service.TownJailManager;

public class TownJailCommand {

    public static Argument<String> JAIL_NAME_ARGUMENT = new StringArgument("jail")
            .replaceSuggestions(ArgumentSuggestions.strings(k -> TownJailManager.getInstance().getJailNames((Player) k.sender())));

    public static CommandAPICommand register() {
        return new CommandAPICommand("jail")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "town.jail"))
                .withSubcommand(TownJailDeleteCommand.register())
                .withSubcommand(TownJailInfoCommand.register())
                .withSubcommand(TownJailListCommand.register())
                .withSubcommand(TownJailCreateCommand.register());
    }
}
