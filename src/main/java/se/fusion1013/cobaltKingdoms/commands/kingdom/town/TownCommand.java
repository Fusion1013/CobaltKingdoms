package se.fusion1013.cobaltKingdoms.commands.kingdom.town;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.commands.kingdom.town.jail.TownJailCommand;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownManager;

public class TownCommand {

    public static Argument<String> TOWN_NAME_ARGUMENT = new StringArgument("town")
            .replaceSuggestions(ArgumentSuggestions.strings(k -> TownManager.getInstance().getTownNames()));

    public static void register() {
        new CommandAPICommand("town")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "town"))
                .withSubcommand(TownCreateCommand.register())
                .withSubcommand(TownDeleteCommand.register())
                .withSubcommand(TownListCommand.register())
                .withSubcommand(TownMoveCommand.register())
                .withSubcommand(TownInviteCommand.register())
                .withSubcommand(TownKickCommand.register())
                .withSubcommand(TownLeaveCommand.register())
                .withSubcommand(TownInfoCommand.register())
                .withSubcommand(TownSetCommand.register())
                .withSubcommand(TownModifyCommand.register())

                .withSubcommand(TownJailCommand.register())

                .register();
    }

}
