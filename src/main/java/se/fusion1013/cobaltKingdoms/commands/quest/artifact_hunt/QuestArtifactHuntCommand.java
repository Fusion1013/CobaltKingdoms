package se.fusion1013.cobaltKingdoms.commands.quest.artifact_hunt;

import dev.jorel.commandapi.CommandAPICommand;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

public class QuestArtifactHuntCommand {
    public static CommandAPICommand register() {
        return new CommandAPICommand("artifact")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "quest.artifact_hunt"))
                .withSubcommand(QuestArtifactHuntGoalCommand.register());
    }
}
