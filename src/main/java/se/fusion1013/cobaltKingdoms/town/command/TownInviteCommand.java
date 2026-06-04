package se.fusion1013.cobaltKingdoms.town.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.commands.AcceptCommand;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.town.service.TownManager;

public class TownInviteCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("invite")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "town.invite"))
                .withArguments(new StringArgument("town").replaceSuggestions(ArgumentSuggestions.strings(k -> TownManager.getInstance().getTownNames())))
                .withArguments(new EntitySelectorArgument.OnePlayer("player"))
                .executesPlayer(TownInviteCommand::invitePlayer);
    }

    private static void invitePlayer(Player player, CommandArguments args) {
        String townName = (String) args.get("town");
        Player invitePlayer = (Player) args.get("player");
        Response response = tryInvitePlayer(player, townName, invitePlayer);

        if (response.ok()) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.town.invite", StringPlaceholders.builder()
                    .addPlaceholder("town", townName)
                    .addPlaceholder("player", invitePlayer.getName())
                    .build());
        } else {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.town.invite.fail", StringPlaceholders.builder()
                    .addPlaceholder("reason", response.message())
                    .build());
        }
    }

    private static Response tryInvitePlayer(Player player, String townName, Player invitePlayer) {
        if (invitePlayer == null) return Response.error("Invalid player");

        Response hasEditPermissions = TownManager.getInstance().hasTownEditPermissions(player, townName);
        if (hasEditPermissions.error()) return hasEditPermissions;

        LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), invitePlayer, "kingdoms.commands.town.invite.invite_message", StringPlaceholders.builder()
                .addPlaceholder("player", player.getName())
                .addPlaceholder("town", townName)
                .build());

        AcceptCommand.setPendingAcceptRequest(invitePlayer, sender -> {
            Response response = TownManager.getInstance().addPlayer(townName, invitePlayer);
            if (response.ok()) {
                LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), invitePlayer, "kingdoms.commands.town.invite.join", StringPlaceholders.builder()
                        .addPlaceholder("player", player.getName())
                        .addPlaceholder("town", townName)
                        .build());
                LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.town.invite.join_other", StringPlaceholders.builder()
                        .addPlaceholder("player", invitePlayer.getName())
                        .addPlaceholder("town", townName)
                        .build());
            } else {
                LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), invitePlayer, "kingdoms.commands.town.invite.join_fail", StringPlaceholders.builder()
                        .addPlaceholder("player", player.getName())
                        .addPlaceholder("town", townName)
                        .addPlaceholder("reason", response.message())
                        .build());
            }
        });

        return Response.ok("Invited player");
    }

}
