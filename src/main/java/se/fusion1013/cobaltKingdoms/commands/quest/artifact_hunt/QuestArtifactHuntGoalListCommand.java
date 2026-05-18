package se.fusion1013.cobaltKingdoms.commands.quest.artifact_hunt;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.quest.artifact_hunt.ArtifactHuntGoal;
import se.fusion1013.cobaltKingdoms.quest.artifact_hunt.ArtifactHuntQuestManager;

import java.util.List;

public class QuestArtifactHuntGoalListCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("list")
                .executesPlayer(QuestArtifactHuntGoalListCommand::listGoals);
    }

    private static void listGoals(Player player, CommandArguments args) {
        List<ArtifactHuntGoal> goals = ArtifactHuntQuestManager.getInstance().getGoals();

        LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.quest.gather.goal.list.header");

        for (ArtifactHuntGoal goal : goals) {
            LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.quest.gather.goal.list.item", StringPlaceholders.builder()
                    .addPlaceholder("goal", goal.getName())
                    .addPlaceholder("location", goal.getLocation().toVector().toString())
                    .addPlaceholder("difficulty", goal.getDifficulty())
                    .build());
        }
    }

}
