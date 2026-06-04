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

public class TownJailInfoCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("info")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "town.jail.info"))
                .withArguments(TownJailCommand.JAIL_NAME_ARGUMENT)
                .executesPlayer(TownJailInfoCommand::info);
    }

    private static void info(Player player, CommandArguments args) {
        String jailName = (String) args.get("jail");
        TownJail jail = TownJailManager.getInstance().getJail(player, jailName);
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
