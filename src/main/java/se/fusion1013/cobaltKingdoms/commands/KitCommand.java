package se.fusion1013.cobaltKingdoms.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.items.kit.KitManager;

public class KitCommand {
    public static void register() {
        new CommandAPICommand("kit")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "kit"))
                .withSubcommand(KitCommand.createKitGetCommand())
                .register();

    }

    private static CommandAPICommand createKitGetCommand() {
        return new CommandAPICommand("get")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "kit.get"))
                .withArguments(new StringArgument("id").replaceSuggestions(ArgumentSuggestions.strings(k -> KitManager.getKitIds())))
                .executesPlayer(KitCommand::giveKit);
    }

    private static void giveKit(Player player, CommandArguments commandArguments) {
        String kitId = (String) commandArguments.get("id");
        boolean success = KitManager.applyKit(player, kitId);

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("kit", kitId)
                .build();

        if (success) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.kit.success", placeholders);
        } else {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.kit.fail", placeholders);
        }
    }

}
