package se.fusion1013.cobaltKingdoms.commands.kingdom;

import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.commands.AcceptCommand;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.ResponseType;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomInfo;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomManager;

import java.util.UUID;

public class KingdomLeaveCommand {

    private static final KingdomManager KINGDOM_MANAGER = CobaltCore.getInstance().getManager(CobaltKingdoms.getInstance(), KingdomManager.class);
    private static final LocaleManager LOCALE = CobaltCore.getInstance().getManager(CobaltCore.getInstance(), LocaleManager.class);

    public static CommandAPICommand register() {
        return new CommandAPICommand("leave")
                .withPermission("cobalt.kingdom.commands.kingdom.leave")
                .executesPlayer((sender, args) -> {

                    boolean isPlayerInKingdom = KINGDOM_MANAGER.isPlayerInKingdom(sender.getUniqueId());
                    if (!isPlayerInKingdom) {
                        LOCALE.sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.kingdom.leave.fail_not_member");
                        return;
                    }

                    KingdomInfo kingdomInfo = KINGDOM_MANAGER.getPlayerKingdomInfo(sender.getUniqueId());

                    StringPlaceholders placeholders = StringPlaceholders.builder()
                            .addPlaceholder("player", sender.getName())
                            .addPlaceholder("kingdom", kingdomInfo.name())
                            .build();

                    LOCALE.sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.kingdom.leave.confirmation", placeholders);

                    AcceptCommand.setPendingAcceptRequest(sender, s -> {
                        leaveKingdom(sender, placeholders, kingdomInfo);
                    });
                });
    }

    private static void leaveKingdom(Player sender, StringPlaceholders placeholders, KingdomInfo kingdomInfo) {
        Response response = KINGDOM_MANAGER.leaveKingdom(sender.getUniqueId());
        placeholders.addPlaceholder("reason", response.message());

        if (response.type() == ResponseType.OK) {
            LOCALE.sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.kingdom.leave.success", placeholders);

            // Broadcast player leaving to all (online) members of the kingdom
            for (UUID playerId : kingdomInfo.members()) {
                Player player = Bukkit.getPlayer(playerId);
                if (player == null || !player.isOnline() || playerId == sender.getUniqueId()) continue;
                LOCALE.sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.kingdom.leave.announce", placeholders);
            }
        } else {
            LOCALE.sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.kingdom.leave.fail", placeholders);
        }
    }

}
