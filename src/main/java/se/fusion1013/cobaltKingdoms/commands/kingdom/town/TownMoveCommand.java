package se.fusion1013.cobaltKingdoms.commands.kingdom.town;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownManager;

public class TownMoveCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("move")
                .withArguments(new StringArgument("town").replaceSuggestions(ArgumentSuggestions.strings(k -> TownManager.getInstance().getTownNames())))
                .withOptionalArguments(new LocationArgument("new_location", LocationType.BLOCK_POSITION))
                .executesPlayer(TownMoveCommand::moveTown);
    }

    private static void moveTown(Player player, CommandArguments args) {
        String townName = (String) args.get("town");
        Location newLocation = args.get("new_location") != null ? (Location) args.get("new_location") : player.getLocation();

        Response response = TownManager.getInstance().moveTown(player, townName, newLocation);

        if (response.ok()) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.town.move", StringPlaceholders.builder()
                    .addPlaceholder("town", townName)
                    .build());
        } else {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.town.move.fail", StringPlaceholders.builder()
                    .addPlaceholder("town", townName)
                    .addPlaceholder("reason", response.message())
                    .build());
        }
    }

}
