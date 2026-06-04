package se.fusion1013.cobaltKingdoms.town.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.town.service.TownManager;

public class TownDeleteCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("delete")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "town.delete"))
                .withArguments(new GreedyStringArgument("town").replaceSuggestions(ArgumentSuggestions.strings(k -> TownManager.getInstance().getTownNames())))
                .executesPlayer(TownDeleteCommand::deleteTown);
    }

    private static void deleteTown(Player player, CommandArguments args) {
        String townName = (String) args.get("town");
        Response response = TownManager.getInstance().deleteTown(player, townName);

        if (response.ok()) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.town.delete", StringPlaceholders.builder()
                    .addPlaceholder("town", townName)
                    .build());
        } else {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.town.delete.fail", StringPlaceholders.builder()
                    .addPlaceholder("town", townName)
                    .addPlaceholder("reason", response.message())
                    .build());
        }
    }

}
