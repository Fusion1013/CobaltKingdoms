package se.fusion1013.cobaltKingdoms.commands.quest.artifact_hunt;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.quest.artifact_hunt.ArtifactHuntQuestManager;

public class QuestArtifactHuntGoalCreateCommand {
    public static CommandAPICommand register() {
        return new CommandAPICommand("create")
                .withArguments(new StringArgument("name"))
                .withArguments(new IntegerArgument("difficulty"))
                .withArguments(new LocationArgument("location", LocationType.BLOCK_POSITION))
                .withArguments(new StringArgument("item").replaceSuggestions(ArgumentSuggestions.strings(k -> CustomItemManager.getCustomItemNames())))
                .withOptionalArguments(new GreedyStringArgument("description"))
                .executesPlayer(QuestArtifactHuntGoalCreateCommand::createGoal);
    }

    private static void createGoal(Player player, CommandArguments args) {
        String name = (String) args.get("name");
        Integer difficulty = (Integer) args.get("difficulty");
        Location location = (Location) args.get("location");
        String itemName = (String) args.get("item");
        String description = args.get("description") != null ? (String) args.get("description") : "";

        if (name == null || difficulty == null || location == null || itemName == null) return;

        tryCreateGoal(player, name, difficulty, location, itemName, description);
    }

    private static void tryCreateGoal(Player player, String name, Integer difficulty, Location location, String itemName, String description) {
        Response response = ArtifactHuntQuestManager.getInstance().createQuestGoal(name, difficulty, location, itemName, description);

        if (response.ok()) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.quest.gather.goal.create", StringPlaceholders.builder()
                    .addPlaceholder("name", name)
                    .addPlaceholder("location", location.toVector().toString())
                    .build());
        } else {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.quest.gather.goal.create_fail", StringPlaceholders.builder()
                    .addPlaceholder("reason", response.message())
                    .build());
        }
    }
}
