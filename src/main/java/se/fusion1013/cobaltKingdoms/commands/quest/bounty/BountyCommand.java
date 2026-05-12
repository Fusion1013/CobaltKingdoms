package se.fusion1013.cobaltKingdoms.commands.quest.bounty;

import dev.jorel.commandapi.CommandAPICommand;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

public class BountyCommand {

    public static void register() {
        new CommandAPICommand("bounty")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "bounty"))
                .withSubcommand(BountyCreateCommand.register())
                .withSubcommand(BountyRecallCommand.register())
                .register();
    }

}
