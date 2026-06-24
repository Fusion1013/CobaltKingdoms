package se.fusion1013.cobaltKingdoms.town.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.config.KingdomsConfig;
import se.fusion1013.cobaltKingdoms.town.config.TownLevelConfig;
import se.fusion1013.cobaltKingdoms.town.model.Town;
import se.fusion1013.cobaltKingdoms.town.model.TownMember;
import se.fusion1013.cobaltKingdoms.town.service.TownManager;

import java.util.List;

public class TownInfoCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("info")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "town.info"))
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
        Town town = TownManager.getInstance().getTown(townName);
        if (town == null) return Response.error("Could not find town");

        List<TownMember> townMembers = TownManager.getInstance().getTownMembers(town.getId());

        LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.town.info.header", StringPlaceholders.builder()
                .addPlaceholder("town", town.getDisplayName())
                .build());

        sendItem(player, "Position", town.getLocation().toBlockLocation().toVector().toString());
        sendItem(player, "Experience", String.valueOf(town.getExperience()));
        TownLevelConfig townLevelConfig = KingdomsConfig.getTownConfig().getTownLevelConfig(town.getExperience());
        sendItem(player, "Level", String.valueOf(townLevelConfig.getLevel()));

        for (TownMember townMember : townMembers) {
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
