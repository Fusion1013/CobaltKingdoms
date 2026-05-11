package se.fusion1013.cobaltKingdoms.commands.kingdom.town.jail;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownJailEntity;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownManager;

public class TownJailInfoCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("info")
                .withArguments(TownJailCommand.JAIL_NAME_ARGUMENT)
                .executesPlayer(TownJailInfoCommand::info);
    }

    private static void info(Player player, CommandArguments args) {
        String jailName = (String) args.get("jail");
        TownJailEntity jail = TownManager.getInstance().getJail(player, jailName);
        if (jail == null) {
            LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.town.jail.info_fail", StringPlaceholders.builder()
                    .addPlaceholder("jail", jailName)
                    .build());
            return;
        }

        LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.town.jail.info.header", StringPlaceholders.builder()
                .addPlaceholder("jail", jailName)
                .build());

        printItem(player, "Name", jail.getName());
        printItem(player, "Location", jail.getLocation().toVector().toString());
    }

    private static void printItem(Player player, String title, Object value) {
        LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.town.jail.info.item", StringPlaceholders.builder()
                .addPlaceholder("title", title)
                .addPlaceholder("value", value)
                .build());
    }

}
