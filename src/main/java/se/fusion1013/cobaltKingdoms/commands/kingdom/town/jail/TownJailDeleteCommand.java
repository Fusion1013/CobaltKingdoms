package se.fusion1013.cobaltKingdoms.commands.kingdom.town.jail;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownManager;

public class TownJailDeleteCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("delete")
                .withArguments(TownJailCommand.JAIL_NAME_ARGUMENT)
                .executesPlayer(TownJailDeleteCommand::deleteJail);
    }

    private static void deleteJail(Player player, CommandArguments args) {
        String jailName = (String) args.get("jail");
        tryDeleteJail(player, jailName);
    }

    private static void tryDeleteJail(Player player, String jailName) {
        Response response = TownManager.getInstance().deleteJail(player, jailName);

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
