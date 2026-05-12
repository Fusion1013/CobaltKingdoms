package se.fusion1013.cobaltKingdoms.commands.quest.gather;

import dev.jorel.commandapi.CommandAPICommand;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

public class QuestGatherCommand {
    public static CommandAPICommand register() {
        return new CommandAPICommand("gather")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "quest.gather"))
                .withSubcommand(QuestGatherGoalCommand.register());
    }
}
