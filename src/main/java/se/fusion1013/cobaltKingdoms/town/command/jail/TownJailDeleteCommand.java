package se.fusion1013.cobaltKingdoms.town.command.jail;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.town.service.TownJailManager;

public class TownJailDeleteCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("delete")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "town.jail.delete"))
                .withArguments(TownJailCommand.JAIL_NAME_ARGUMENT)
                .executesPlayer(TownJailDeleteCommand::deleteJail);
    }

    private static void deleteJail(Player player, CommandArguments args) {
        String jailName = (String) args.get("jail");
        tryDeleteJail(player, jailName);
    }

    private static void tryDeleteJail(Player player, String jailName) {
        Response response = TownJailManager.getInstance().deleteJail(player, jailName);

        if (response.ok()) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.town.jail.delete", StringPlaceholders.builder()
                    .addPlaceholder("jail", jailName)
                    .build());
        } else {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.town.jail.delete_fail", StringPlaceholders.builder()
                    .addPlaceholder("reason", response.message())
                    .build());
        }
    }

}
