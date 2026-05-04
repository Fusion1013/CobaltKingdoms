package se.fusion1013.cobaltKingdoms.commands.kingdom.town;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownManager;

public class TownDeleteCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("delete")
                .withArguments(new StringArgument("town").replaceSuggestions(ArgumentSuggestions.strings(k -> TownManager.getInstance().getTownNames())))
                .executesPlayer(TownDeleteCommand::deleteTown);
    }

    private static void deleteTown(Player player, CommandArguments args) {
        String townName = (String) args.get("town");
        boolean deleted = TownManager.getInstance().deleteTown(player, townName);

        if (deleted) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.town.delete", StringPlaceholders.builder()
                    .addPlaceholder("town", townName)
                    .build());
        } else {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.town.delete.fail", StringPlaceholders.builder()
                    .addPlaceholder("town", townName)
                    .build());
        }
    }

}
