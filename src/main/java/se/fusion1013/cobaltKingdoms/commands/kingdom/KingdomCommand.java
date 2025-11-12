package se.fusion1013.cobaltKingdoms.commands.kingdom;

import dev.jorel.commandapi.CommandAPICommand;

public class KingdomCommand {

    public static void register() {
        new CommandAPICommand("kingdom")
                .withPermission("cobalt.kingdom.commands.kingdom")
                .withSubcommand(KingdomCreateCommand.register())
                .withSubcommand(KingdomModifyCommand.register())
                .withSubcommand(KingdomInviteCommand.register())
                .withSubcommand(KingdomListCommand.register())
                .withSubcommand(KingdomInfoCommand.register())
                .withSubcommand(KingdomDeleteCommand.register())
                .withSubcommand(KingdomLeaveCommand.register())
                .withSubcommand(KingdomMessageCommand.register())
                .withSubcommand(KingdomKickCommand.register())
                .register();
    }

}
