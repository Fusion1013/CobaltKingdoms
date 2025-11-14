package se.fusion1013.cobaltKingdoms.commands.kingdom;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomInfo;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomManager;

import java.util.UUID;

public class KingdomInfoCommand {

    private static final KingdomManager KINGDOM_MANAGER = CobaltCore.getInstance().getManager(CobaltKingdoms.getInstance(), KingdomManager.class);
    private static final LocaleManager LOCALE = CobaltCore.getInstance().getManager(CobaltCore.getInstance(), LocaleManager.class);

    public static CommandAPICommand register() {
        return new CommandAPICommand("info")
                .withPermission("cobalt.kingdom.commands.kingdom.info")
                .withArguments(new StringArgument("kingdom_name").replaceSuggestions(ArgumentSuggestions.strings(si -> KINGDOM_MANAGER.getKingdomNames().toArray(new String[0]))))
                .executesPlayer((sender, args) -> {
                    String kingdomName = (String) args.get("kingdom_name");
                    KingdomInfo kingdomInfo = KINGDOM_MANAGER.getKingdomInfo(kingdomName);
                    Player owner = Bukkit.getPlayer(kingdomInfo.owner());

                    StringPlaceholders generalPlaceholder = StringPlaceholders.builder()
                            .addPlaceholder("kingdom", kingdomName)
                            .addPlaceholder("member_count", kingdomInfo.members().size())
                            .addPlaceholder("owner", owner == null ? "" : owner.getName())
                            .build();

                    LOCALE.sendMessage("", sender, "kingdoms.commands.kingdom.info.header", generalPlaceholder);

                    LOCALE.sendMessage("", sender, "kingdoms.commands.kingdom.info.owner", generalPlaceholder);

                    LOCALE.sendMessage("", sender, "kingdoms.commands.kingdom.info.player_list_header", generalPlaceholder);
                    for (UUID playerId : kingdomInfo.members()) {
                        Player player = Bukkit.getPlayer(playerId);
                        if (player == null) continue;

                        StringPlaceholders placeholders = StringPlaceholders.builder()
                                .addPlaceholder("player", player.getName())
                                .build();
                        LOCALE.sendMessage("", sender, "kingdoms.commands.kingdom.info.player_list_item", placeholders);
                    }
                });
    }

}
