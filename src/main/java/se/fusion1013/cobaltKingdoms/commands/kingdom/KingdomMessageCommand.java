package se.fusion1013.cobaltKingdoms.commands.kingdom;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomInfo;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomManager;

import java.util.UUID;

public class KingdomMessageCommand {

    private static final KingdomManager KINGDOM_MANAGER = CobaltCore.getInstance().getManager(CobaltKingdoms.getInstance(), KingdomManager.class);
    private static final LocaleManager LOCALE = CobaltCore.getInstance().getManager(CobaltCore.getInstance(), LocaleManager.class);

    public static CommandAPICommand register() {
        new CommandAPICommand("km")
                .withPermission("cobalt.kingdom.commands.kingdom.message")
                .withArguments(new GreedyStringArgument("message"))
                .executesPlayer(KingdomMessageCommand::sendMessage)
                .register();

        return new CommandAPICommand("message")
                .withPermission("cobalt.kingdom.commands.kingdom.message")
                .withArguments(new GreedyStringArgument("message"))
                .executesPlayer(KingdomMessageCommand::sendMessage);
    }

    private static void sendMessage(Player player, CommandArguments args) {
        KingdomInfo kingdomInfo = KINGDOM_MANAGER.getPlayerKingdomInfo(player.getUniqueId());
        if (kingdomInfo == null) {
            LOCALE.sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.kingdom.message.fail_no_kingdom");
            return;
        }

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("message", args.get("message"))
                .addPlaceholder("player", player.getName())
                .build();
        for (UUID kingdomPlayerId : kingdomInfo.members()) {
            Player kingdomPlayer = Bukkit.getPlayer(kingdomPlayerId);
            if (kingdomPlayer == null) continue;

            LOCALE.sendMessage("", kingdomPlayer, "kingdoms.commands.kingdom.message.send", placeholders);
        }
    }

}
