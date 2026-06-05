package se.fusion1013.cobaltKingdoms.quest.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.quest.model.PlayerQuest;
import se.fusion1013.cobaltKingdoms.quest.model.QuestStatus;
import se.fusion1013.cobaltKingdoms.quest.model.QuestType;
import se.fusion1013.cobaltKingdoms.quest.service.QuestManager;

import java.util.List;

public class QuestHistoryCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("history")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "quest.history"))
                .withOptionalArguments(new EntitySelectorArgument.OnePlayer("player"))
                .executesPlayer(QuestHistoryCommand::showPlayerHistory);
    }

    private static void showPlayerHistory(Player player, CommandArguments args) {
        Player target = args.get("player") != null ? (Player) args.get("player") : player;
        List<PlayerQuest> playerQuests = QuestManager.getInstance().getPlayerQuests(target);

        for (QuestType questType : QuestType.values()) {
            displayQuestTypeInfo(player, playerQuests.stream().filter(quest -> quest.getQuest().getQuestType() == questType).toList(), questType);
        }
    }

    private static void displayQuestTypeInfo(Player player, List<PlayerQuest> playerQuests, QuestType questType) {
        player.sendMessage("### " + questType.name() + " ###");

        for (QuestStatus status : QuestStatus.values()) {
            long count = playerQuests.stream().filter(quest -> quest.getQuest().getQuestStatus() == status).count();
            player.sendMessage(status.name() + ": " + count);
        }
    }

}
