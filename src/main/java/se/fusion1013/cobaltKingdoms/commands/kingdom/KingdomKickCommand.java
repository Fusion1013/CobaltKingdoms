package se.fusion1013.cobaltKingdoms.commands.kingdom;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.PlayerProfileArgument;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.ResponseType;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomInfo;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomManager;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomPermission;

public class KingdomKickCommand {

    private static final KingdomManager KINGDOM_MANAGER = CobaltCore.getInstance().getManager(CobaltKingdoms.getInstance(), KingdomManager.class);
    private static final LocaleManager LOCALE = CobaltCore.getInstance().getManager(CobaltCore.getInstance(), LocaleManager.class);

    public static CommandAPICommand register() {
        return new CommandAPICommand("kick")
                .withPermission("cobalt.kingdom.commands.kingdom.kick")
                .withArguments(new EntitySelectorArgument.OnePlayer("player"))
                .executesPlayer((sender, args) -> {

                    Player kickPlayer = (Player) args.get("player");
                    if (kickPlayer == null) {
                        LOCALE.sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.kingdom.kick.fail_player_not_found");
                        return;
                    }

                    KingdomInfo senderKingdom = KINGDOM_MANAGER.getPlayerKingdomInfo(sender.getUniqueId());
                    KingdomInfo kickPlayerKingdom = KINGDOM_MANAGER.getPlayerKingdomInfo(kickPlayer.getUniqueId());

                    if (senderKingdom == null) {
                        LOCALE.sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.kingdom.kick.fail_no_sender_kingdom");
                        return;
                    }

                    if (kickPlayerKingdom == null) {
                        LOCALE.sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.kingdom.kick.fail_no_kick_kingdom");
                        return;
                    }

                    StringPlaceholders placeholders = StringPlaceholders.builder()
                            .addPlaceholder("sender_kingdom", senderKingdom.name())
                            .addPlaceholder("kick_kingdom", kickPlayerKingdom.name())
                            .addPlaceholder("kick_player", kickPlayer.getName())
                            .addPlaceholder("sender_player", sender.getName())
                            .addPlaceholder("permission", KingdomPermission.KICK.key())
                            .build();

                    boolean hasPermission = KINGDOM_MANAGER.hasPermission(sender.getUniqueId(), KingdomPermission.KICK);
                    if (!hasPermission) {
                        LOCALE.sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.kingdom.permission_denied", placeholders);
                        return;
                    }

                    if (!senderKingdom.name().equalsIgnoreCase(kickPlayerKingdom.name())) {
                        LOCALE.sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.kingdom.kick.fail_not_in_kingdom", placeholders);
                        return;
                    }

                    Response response = KINGDOM_MANAGER.leaveKingdom(kickPlayer.getUniqueId());
                    placeholders.addPlaceholder("reason", response.message());
                    if (response.type() == ResponseType.OK) {
                        LOCALE.sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.kingdom.kick.success", placeholders);
                    } else {
                        LOCALE.sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.kingdom.kick.fail", placeholders);
                    }
                });
    }

}
