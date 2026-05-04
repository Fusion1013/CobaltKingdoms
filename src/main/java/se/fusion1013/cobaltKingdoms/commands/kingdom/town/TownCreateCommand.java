package se.fusion1013.cobaltKingdoms.commands.kingdom.town;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownManager;

public class TownCreateCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("create")
                .withArguments(new StringArgument("name"))
                .withArguments(new LocationArgument("town_center", LocationType.BLOCK_POSITION))
                .executesPlayer(TownCreateCommand::createTown);
    }

    private static void createTown(Player player, CommandArguments args) {
        String townName = (String) args.get("name");
        Location location = (Location) args.get("town_center");

        boolean created = TownManager.getInstance().createTown(townName, player, location);

        if (created) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.town.create", StringPlaceholders.builder()
                    .addPlaceholder("town", townName)
                    .build());
        } else {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.town.create.fail", StringPlaceholders.builder()
                    .addPlaceholder("town", townName)
                    .build());
        }
    }

}
