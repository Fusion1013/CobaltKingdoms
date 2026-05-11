package se.fusion1013.cobaltKingdoms.commands.kingdom.town.jail;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownJailEntity;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownManager;

import java.util.List;

public class TownJailListCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("list")
                .executesPlayer(TownJailListCommand::listJails);
    }

    private static void listJails(Player player, CommandArguments args) {
        List<TownJailEntity> jails = TownManager.getInstance().getJails(player);

        LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.town.jail.list.header");
        for (TownJailEntity jail : jails) {
            LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.town.jail.list.item", StringPlaceholders.builder()
                    .addPlaceholder("jail", jail.getName())
                    .addPlaceholder("location", jail.getLocation().toVector().toString())
                    .build());
        }
    }

}
