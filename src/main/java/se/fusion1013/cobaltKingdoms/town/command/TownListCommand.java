package se.fusion1013.cobaltKingdoms.town.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.town.model.Town;
import se.fusion1013.cobaltKingdoms.town.service.TownManager;

public class TownListCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("list")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "town.list"))
                .executesPlayer(TownListCommand::listTowns);
    }

    private static void listTowns(Player player, CommandArguments args) {
        LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.town.list.header");

        for (Town town : TownManager.getInstance().getTowns()) {
            LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.town.list.item", StringPlaceholders.builder()
                    .addPlaceholder("town", town.getDisplayName())
                    .addPlaceholder("location", town.getLocation().toBlockLocation().toVector().toString())
                    .build());
        }
    }

}
