package se.fusion1013.cobaltKingdoms.quest.command.bounty;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.quest.service.BountyManager;

public class BountyEnableDisableCommand {

    public static CommandAPICommand registerEnable() {
        return new CommandAPICommand("enable")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "bounty.enable"))
                .executesPlayer(BountyEnableDisableCommand::enable);
    }

    private static void enable(Player player, CommandArguments args) {
        setStatus(player, true);
    }

    public static CommandAPICommand registerDisable() {
        return new CommandAPICommand("disable")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "bounty.disable"))
                .executesPlayer(BountyEnableDisableCommand::disable);
    }

    private static void disable(Player player, CommandArguments args) {
        setStatus(player, false);
    }

    private static void setStatus(Player player, boolean enabled) {
        Response response = BountyManager.getInstance().setPlayerBountiesEnabled(player, enabled);

        if (response.ok()) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.quests.bounty.change_status", StringPlaceholders.builder()
                    .addPlaceholder("status", enabled)
                    .build());
        } else {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.quests.bounty.change_status_fail", StringPlaceholders.builder()
                    .addPlaceholder("reason", response.message())
                    .build());
        }
    }

}
