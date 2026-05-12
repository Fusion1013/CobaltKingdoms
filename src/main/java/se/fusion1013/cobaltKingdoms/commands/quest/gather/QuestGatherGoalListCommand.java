package se.fusion1013.cobaltKingdoms.commands.quest.gather;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.quest.item_gather.GatherQuestGoalEntity;
import se.fusion1013.cobaltKingdoms.quest.item_gather.GatherQuestManager;

import java.util.List;

public class QuestGatherGoalListCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("list")
                .executesPlayer(QuestGatherGoalListCommand::listGoals);
    }

    private static void listGoals(Player player, CommandArguments args) {
        List<GatherQuestGoalEntity> goals = GatherQuestManager.getInstance().getGoals();

        LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.quest.gather.goal.list.header");

        for (GatherQuestGoalEntity goal : goals) {
            LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.quest.gather.goal.list.item", StringPlaceholders.builder()
                    .addPlaceholder("goal", goal.getName())
                    .addPlaceholder("location", goal.getLocation().toVector().toString())
                    .build());
        }
    }

}
