package se.fusion1013.cobaltKingdoms.town.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.town.service.TownManager;

public class TownSetCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("set")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "town.set"))
                .withSubcommand(createLevelCommand());
    }

    private static CommandAPICommand createLevelCommand() {
        return new CommandAPICommand("level")
                .withArguments(new StringArgument("town").replaceSuggestions(ArgumentSuggestions.strings(k -> TownManager.getInstance().getTownNames())))
                .withArguments(new IntegerArgument("level"))
                .executesPlayer(TownSetCommand::setLevel);
    }

    private static void setLevel(Player player, CommandArguments args) {
        String townName = (String) args.get("town");
        int level = (int) args.get("level");

        TownManager.getInstance().setTownLevel(townName, level);
    }

}
