package se.fusion1013.cobaltKingdoms.commands.kingdom.town;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownManager;

public class TownModifyCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("modify")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "modify"))
                .withSubcommand(TownModifyCommand.modifySkinCommand())
                .withSubcommand(TownModifyCommand.modifyTextureCommand())
                .withSubcommand(TownModifyCommand.modifyChatGreetingCommand())
                .withSubcommand(TownModifyCommand.modifyTitleGreetingCommand());
    }

    // ##%%##%%## SKIN ##%%##%%## //

    private static CommandAPICommand modifySkinCommand() {
        return new CommandAPICommand("skin")
                .withArguments(TownCommand.TOWN_NAME_ARGUMENT)
                .withOptionalArguments(new StringArgument("skin"))
                .executesPlayer(TownModifyCommand::tryModifySkin);
    }

    private static void tryModifySkin(Player player, CommandArguments args) {
        String townName = (String) args.get("town");
        String skin = args.get("skin") != null ? (String) args.get("skin") : "";

        Response response = TownManager.getInstance().modifySkin(player, townName, skin);
        printResponse(response, player, townName, "Skin", skin);
    }

    // ##%%##%%## TEXTURE ##%%##%%## //

    private static CommandAPICommand modifyTextureCommand() {
        return new CommandAPICommand("texture")
                .withArguments(TownCommand.TOWN_NAME_ARGUMENT)
                .withOptionalArguments(new StringArgument("texture"))
                .executesPlayer(TownModifyCommand::tryModifyTexture);
    }

    private static void tryModifyTexture(Player player, CommandArguments args) {
        String townName = (String) args.get("town");
        String texture = args.get("texture") != null ? (String) args.get("texture") : "";

        Response response = TownManager.getInstance().modifyTexture(player, townName, texture);
        printResponse(response, player, townName, "Texture", texture);
    }

    // ##%%##%%## CHAT GREETING ##%%##%%## //

    private static CommandAPICommand modifyChatGreetingCommand() {
        return new CommandAPICommand("chat_greeting")
                .withArguments(TownCommand.TOWN_NAME_ARGUMENT)
                .withArguments(new StringArgument("greeting"))
                .executesPlayer(TownModifyCommand::tryModifyChatGreeting);
    }

    private static void tryModifyChatGreeting(Player player, CommandArguments args) {
        String townName = (String) args.get("town");
        String greeting = (String) args.get("greeting");

        Response response = TownManager.getInstance().modifyChatGreeting(player, townName, greeting);
        printResponse(response, player, townName, "Chat Greeting", greeting);
    }

    // ##%%##%%## TITLE GREETING ##%%##%%## //

    private static CommandAPICommand modifyTitleGreetingCommand() {
        return new CommandAPICommand("title_greeting")
                .withArguments(TownCommand.TOWN_NAME_ARGUMENT)
                .withArguments(new StringArgument("greeting"))
                .executesPlayer(TownModifyCommand::tryModifyTitleGreeting);
    }

    private static void tryModifyTitleGreeting(Player player, CommandArguments args) {
        String townName = (String) args.get("town");
        String greeting = (String) args.get("greeting");

        Response response = TownManager.getInstance().modifyTitleGreeting(player, townName, greeting);
        printResponse(response, player, townName, "Title Greeting", greeting);
    }

    // ##%%##%%## UTIL ##%%##%%## //

    private static void printResponse(Response response, Player player, String townName, String key, String value) {
        if (response.ok()) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.town.modify.set", StringPlaceholders.builder()
                    .addPlaceholder("action", key)
                    .addPlaceholder("town", townName)
                    .addPlaceholder("value", value)
                    .build());
        } else {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.town.modify.set_fail", StringPlaceholders.builder()
                    .addPlaceholder("reason", response.message())
                    .build());
        }
    }

}
