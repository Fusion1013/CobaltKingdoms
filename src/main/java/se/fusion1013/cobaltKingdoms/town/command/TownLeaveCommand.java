package se.fusion1013.cobaltKingdoms.town.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.town.service.TownManager;

public class TownLeaveCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("leave")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "town.leave"))
                .executesPlayer(TownLeaveCommand::leaveTown);
    }

    private static void leaveTown(Player player, CommandArguments args) {
        Response response = tryLeaveTown(player);
        if (response.ok()) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.town.leave");
        } else {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.town.leave_error", StringPlaceholders.builder()
                    .addPlaceholder("reason", response.message())
                    .build());
        }
    }

    private static Response tryLeaveTown(Player player) {
        return TownManager.getInstance().removeTownPlayer(player, player);
    }

}
