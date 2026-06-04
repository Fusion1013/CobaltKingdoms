package se.fusion1013.cobaltKingdoms.town.command.jail;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.town.service.TownJailManager;

public class TownJailCreateCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("create")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "town.jail.create"))
                .withArguments(new LocationArgument("location"))
                .withArguments(new GreedyStringArgument("name"))
                .executesPlayer(TownJailCreateCommand::createJail);
    }

    private static void createJail(Player player, CommandArguments args) {
        Location location = (Location) args.get("location");
        String jailName = (String) args.get("name");
        tryCreateJail(player, location, jailName);
    }

    private static void tryCreateJail(Player player, Location location, String jailName) {
        Response response = TownJailManager.getInstance().createJail(player, jailName, location);

        if (response.ok()) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.town.jail.create", StringPlaceholders.builder()
                    .addPlaceholder("jail", jailName)
                    .build());
        } else {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.town.jail.create_fail", StringPlaceholders.builder()
                    .addPlaceholder("reason", response.message())
                    .build());
        }
    }

}
