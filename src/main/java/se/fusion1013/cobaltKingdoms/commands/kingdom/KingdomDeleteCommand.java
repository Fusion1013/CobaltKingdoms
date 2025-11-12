package se.fusion1013.cobaltKingdoms.commands.kingdom;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.commands.AcceptCommand;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.ResponseType;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomManager;

public class KingdomDeleteCommand {

    private static final KingdomManager KINGDOM_MANAGER = CobaltCore.getInstance().getManager(CobaltKingdoms.getInstance(), KingdomManager.class);
    private static final LocaleManager LOCALE = CobaltCore.getInstance().getManager(CobaltCore.getInstance(), LocaleManager.class);

    public static CommandAPICommand register() {
        return new CommandAPICommand("delete")
                .withPermission("cobalt.kingdom.commands.kingdom.delete")
                .withArguments(new StringArgument("kingdom_name").replaceSuggestions(ArgumentSuggestions.strings(si -> KINGDOM_MANAGER.getKingdomNames(((Player) si.sender()).getUniqueId()).toArray(new String[0]))))
                .executesPlayer((sender, args) -> {
                    String kingdomName = (String) args.get("kingdom_name");

                    StringPlaceholders placeholders = StringPlaceholders.builder()
                            .addPlaceholder("kingdom", kingdomName)
                            .build();

                    LOCALE.sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.kingdom.delete.confirmation", placeholders);
                    AcceptCommand.setPendingAcceptRequest(sender, (send) -> {
                        deleteKingdom(sender, kingdomName);
                    });
                });
    }

    private static void deleteKingdom(Player sender, String kingdomName) {
        Response response = KINGDOM_MANAGER.deleteKingdom(kingdomName);

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("kingdom", kingdomName)
                .addPlaceholder("reason", response.message())
                .build();

        if (response.type() == ResponseType.OK) {
            LOCALE.sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.kingdom.delete.success", placeholders);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player == sender) continue;
                LOCALE.sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.kingdom.delete.announce", placeholders);
            }
        } else {
            LOCALE.sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.kingdom.delete.fail", placeholders);
        }
    }

}
