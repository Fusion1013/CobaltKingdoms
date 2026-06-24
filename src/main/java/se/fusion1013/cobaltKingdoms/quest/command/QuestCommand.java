package se.fusion1013.cobaltKingdoms.quest.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.quest.command.artifact_hunt.QuestArtifactHuntCommand;
import se.fusion1013.cobaltKingdoms.quest.service.QuestManager;

public class QuestCommand {

    public static void register() {
        new CommandAPICommand("quest")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "quest"))
                .withSubcommand(QuestArtifactHuntCommand.register())
                .withSubcommand(QuestDebugCommand.register())
                .withSubcommand(QuestHistoryCommand.register())
                .withSubcommand(QuestCommand.cleanup())
                .register();
    }

    private static CommandAPICommand cleanup() {
        return new CommandAPICommand("cleanup")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "quest.cleanup"))
                .executesPlayer(QuestCommand::tryCleanup);
    }

    private static void tryCleanup(Player player, CommandArguments args) {
        int count = QuestManager.getInstance().cleanupDespawnedQuests();
        player.sendMessage("Deleted " + count + " despawned quests");
    }

}
