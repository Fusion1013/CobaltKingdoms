package se.fusion1013.cobaltKingdoms.commands.kingdom.town;

import dev.jorel.commandapi.CommandAPICommand;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

public class TownCommand {

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
                .register();
    }

}
