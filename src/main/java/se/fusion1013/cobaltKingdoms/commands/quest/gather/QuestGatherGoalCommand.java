package se.fusion1013.cobaltKingdoms.commands.quest.gather;

import dev.jorel.commandapi.CommandAPICommand;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

public class QuestGatherGoalCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("goal")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "goal"))
                .withSubcommand(QuestGatherGoalListCommand.register())
                .withSubcommand(QuestGatherGoalCreateCommand.register());
    }
}
