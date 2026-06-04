package se.fusion1013.cobaltKingdoms.town.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.ResponseType;
import se.fusion1013.cobaltKingdoms.town.service.TownManager;

public class TownCreateCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("create")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "town.create"))
                .withArguments(new StringArgument("id"))
                .withArguments(new GreedyStringArgument("display_name"))
                .executesPlayer(TownCreateCommand::createTown);
    }

    private static void createTown(Player player, CommandArguments args) {
        String townName = (String) args.get("id");
        String townDisplayName = (String) args.get("display_name");
        Location location = player.getLocation();

        Response response = TownManager.getInstance().createTown(townName, townDisplayName, player, location);

        if (response.type() == ResponseType.OK) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.town.create", StringPlaceholders.builder()
                    .addPlaceholder("town", townName)
                    .build());
        } else {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.town.create.fail", StringPlaceholders.builder()
                    .addPlaceholder("town", townName)
                    .addPlaceholder("reason", response.message())
                    .build());
        }
    }

}
