package se.fusion1013.cobaltKingdoms.commands.quest.artifact_hunt;

import dev.jorel.commandapi.CommandAPICommand;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

public class QuestArtifactHuntGoalCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("goal")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "goal"))
                .withSubcommand(QuestArtifactHuntGoalListCommand.register())
                .withSubcommand(QuestArtifactHuntGoalCreateCommand.register());
    }
}
