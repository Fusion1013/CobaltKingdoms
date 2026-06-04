package se.fusion1013.cobaltKingdoms.town.command.jail;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.town.model.TownJail;
import se.fusion1013.cobaltKingdoms.town.service.TownJailManager;

import java.util.List;

public class TownJailListCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("list")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "town.jail.list"))
                .executesPlayer(TownJailListCommand::listJails);
    }

    private static void listJails(Player player, CommandArguments args) {
        List<TownJail> jails = TownJailManager.getInstance().getJails(player);

        LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.town.jail.list.header");
        for (TownJail jail : jails) {
            LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.town.jail.list.item", StringPlaceholders.builder()
                    .addPlaceholder("jail", jail.getName())
                    .addPlaceholder("location", jail.getLocation().toBlockLocation().toVector().toString())
                    .build());
        }
    }

}
