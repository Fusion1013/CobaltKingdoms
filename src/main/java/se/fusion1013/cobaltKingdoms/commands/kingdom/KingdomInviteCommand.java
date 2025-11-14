package se.fusion1013.cobaltKingdoms.commands.kingdom;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.commands.AcceptCommand;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomInfo;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomManager;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomPermission;

public class KingdomInviteCommand {

    private static final KingdomManager KINGDOM_MANAGER = CobaltCore.getInstance().getManager(CobaltKingdoms.getInstance(), KingdomManager.class);
    private static final LocaleManager LOCALE = CobaltCore.getInstance().getManager(CobaltCore.getInstance(), LocaleManager.class);

    public static CommandAPICommand register() {
        return new CommandAPICommand("invite")
                .withPermission("cobalt.kingdom.commands.kingdom.invite")
                .withArguments(new StringArgument("kingdom_name").replaceSuggestions(ArgumentSuggestions.strings(si -> KINGDOM_MANAGER.getKingdomNames(((Player) si.sender()).getUniqueId()).toArray(new String[0]))))
                .withArguments(new EntitySelectorArgument.OnePlayer("player"))
                .executesPlayer((sender, args) -> {
                    String kingdomName = (String) args.get("kingdom_name");
                    Player receiverPlayer = (Player) args.get("player");

                    StringPlaceholders placeholders = StringPlaceholders.builder()
                            .addPlaceholder("receiver", receiverPlayer == null ? "?" : receiverPlayer.getName())
                            .addPlaceholder("kingdom", kingdomName)
                            .addPlaceholder("sender", sender.getName())
                            .addPlaceholder("permission", KingdomPermission.INVITE)
                            .build();

                    boolean hasPermission = KINGDOM_MANAGER.hasPermission(sender.getUniqueId(), KingdomPermission.INVITE);
                    if (!hasPermission) {
                        LOCALE.sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.kingdom.permission_denied", placeholders);
                        return;
                    }

                    if (receiverPlayer == null) {
                        LOCALE.sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.kingdom.invite.sender_fail", placeholders);
                        return;
                    }

                    KingdomInfo receiverPlayerKingdomInfo = KINGDOM_MANAGER.getPlayerKingdomInfo(receiverPlayer.getUniqueId());
                    if (receiverPlayerKingdomInfo != null) {
                        LOCALE.sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.kingdom.invite.fail_already_in_kingdom", placeholders);
                        return;
                    }

                    LOCALE.sendMessage(CobaltKingdoms.getInstance(), receiverPlayer, "kingdoms.commands.kingdom.invite.receiver_message", placeholders);
                    receiverPlayer.playSound(receiverPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    LOCALE.sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.kingdom.invite.sender_succeed", placeholders);

                    AcceptCommand.setPendingAcceptRequest(receiverPlayer, s -> {
                        boolean playerJoined = KINGDOM_MANAGER.addPlayerToKingdom(kingdomName, receiverPlayer.getUniqueId());
                        if (playerJoined) {
                            LOCALE.sendMessage(CobaltKingdoms.getInstance(), receiverPlayer, "kingdoms.commands.kingdom.invite.receiver_succeed", placeholders);
                            KingdomInfo kingdomInfo = KINGDOM_MANAGER.getKingdomInfo(kingdomName);
                            kingdomInfo.members().forEach(m -> {
                                Player memberPlayer = Bukkit.getPlayer(m);
                                if (memberPlayer == null || !memberPlayer.isOnline()) return;
                                if (m == receiverPlayer.getUniqueId()) return;
                                LOCALE.sendMessage(CobaltKingdoms.getInstance(), memberPlayer, "kingdoms.commands.kingdom.invite.player_joined", placeholders);
                                memberPlayer.playSound(memberPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                            });
                        }
                        else LOCALE.sendMessage(CobaltKingdoms.getInstance(), receiverPlayer, "kingdoms.commands.kingdom.invite.receiver_fail", placeholders);
                    });
                });
    }

}
