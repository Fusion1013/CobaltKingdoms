package se.fusion1013.cobaltKingdoms.commands.quest.gather;

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
import se.fusion1013.cobaltKingdoms.quest.item_gather.GatherQuestManager;

public class QuestGatherGoalCreateCommand {
    public static CommandAPICommand register() {
        return new CommandAPICommand("create")
                .withArguments(new StringArgument("name"))
                .withArguments(new IntegerArgument("difficulty"))
                .withArguments(new LocationArgument("location", LocationType.BLOCK_POSITION))
                .withArguments(new StringArgument("item").replaceSuggestions(ArgumentSuggestions.strings(k -> CustomItemManager.getCustomItemNames())))
                .executesPlayer(QuestGatherGoalCreateCommand::createGoal);
    }

    private static void createGoal(Player player, CommandArguments args) {
        String name = (String) args.get("name");
        Integer difficulty = (Integer) args.get("difficulty");
        Location location = (Location) args.get("location");
        String itemName = (String) args.get("item");

        if (name == null || difficulty == null || location == null || itemName == null) return;

        tryCreateGoal(player, name, difficulty, location, itemName);
    }

    private static void tryCreateGoal(Player player, String name, Integer difficulty, Location location, String itemName) {
        Response response = GatherQuestManager.getInstance().createQuestGoal(name, difficulty, location, itemName);

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
