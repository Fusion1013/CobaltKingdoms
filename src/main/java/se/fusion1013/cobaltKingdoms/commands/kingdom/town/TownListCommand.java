package se.fusion1013.cobaltKingdoms.commands.kingdom.town;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownEntity;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownManager;

public class TownListCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("list")
                .executesPlayer(TownListCommand::listTowns);
    }

    private static void listTowns(Player player, CommandArguments args) {
        LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.town.list.header");

        for (TownEntity town : TownManager.getInstance().getTowns()) {
            LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.town.list.item", StringPlaceholders.builder()
                    .addPlaceholder("town", town.getDisplayName())
                    .addPlaceholder("location", town.getLocation().toBlockLocation().toVector().toString())
                    .build());
        }
    }

}
