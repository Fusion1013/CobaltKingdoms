package se.fusion1013.cobaltKingdoms.commands.kingdom.town;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownManager;

public class TownKickCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("kick")
                .withArguments(new EntitySelectorArgument.OnePlayer("player"))
                .executesPlayer(TownKickCommand::kickPlayer);
    }

    private static void kickPlayer(Player player, CommandArguments args) {
        Player kickPlayer = (Player) args.get("player");
        Response response = tryKickPlayer(player, kickPlayer);

        if (response.ok()) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.town.kick", StringPlaceholders.builder()
                    .addPlaceholder("player", kickPlayer.getName())
                    .build());

            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), kickPlayer, "kingdoms.commands.town.kicked");
        } else {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.town.kick_fail", StringPlaceholders.builder()
                    .addPlaceholder("reason", response.message())
                    .build());
        }
    }

    private static Response tryKickPlayer(Player player, Player kickPlayer) {
        return TownManager.getInstance().removeTownPlayer(player, kickPlayer);
    }

}
