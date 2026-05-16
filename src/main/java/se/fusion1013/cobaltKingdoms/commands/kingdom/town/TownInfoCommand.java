package se.fusion1013.cobaltKingdoms.commands.kingdom.town;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownEntity;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownManager;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownMemberEntity;

import java.util.List;

public class TownInfoCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("info")
                .withArguments(new StringArgument("town").replaceSuggestions(ArgumentSuggestions.strings(k -> TownManager.getInstance().getTownNames())))
                .executesPlayer(TownInfoCommand::townInfo);
    }

    private static void townInfo(Player player, CommandArguments args) {
        String townName = (String) args.get("town");
        Response response = tryShowTownInfo(player, townName);

        if (response.error()) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.town.info_fail", StringPlaceholders.builder()
                    .addPlaceholder("reason", response.message())
                    .build());
        }
    }

    private static Response tryShowTownInfo(Player player, String townName) {
        TownEntity town = TownManager.getInstance().getTown(townName);
        if (town == null) return Response.error("Could not find town");

        List<TownMemberEntity> townMembers = TownManager.getInstance().getTownMembers(town.getId());

        LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.town.info.header", StringPlaceholders.builder()
                .addPlaceholder("town", town.getDisplayName())
                .build());

        sendItem(player, "Position", town.getLocation().toVector().toString());
        sendItem(player, "Experience", String.valueOf(town.getExperience()));

        for (TownMemberEntity townMember : townMembers) {
            sendItem(player, townMember.getPlayerName(), townMember.getRole().name());
        }


        return Response.ok("");
    }

    private static void sendItem(Player player, String key, String value) {
        LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.town.info.item", StringPlaceholders.builder()
                .addPlaceholder("key", key)
                .addPlaceholder("value", value)
                .build());
    }

}
