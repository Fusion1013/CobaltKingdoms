package se.fusion1013.cobaltKingdoms.quest.command;

import dev.jorel.commandapi.CommandAPICommand;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.quest.command.artifact_hunt.QuestArtifactHuntCommand;

public class QuestCommand {

    public static void register() {
        new CommandAPICommand("quest")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "quest"))
                .withSubcommand(QuestArtifactHuntCommand.register())
                .withSubcommand(QuestDebugCommand.register())
                .withSubcommand(QuestHistoryCommand.register())
                .register();
    }

}
