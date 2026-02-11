package se.fusion1013.cobaltKingdoms.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

public class ColorsCommand {

    public static void register() {
        new CommandAPICommand("colors")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "colors"))
                .executesPlayer(ColorsCommand::showColors)
                .register();
    }

    private static void showColors(Player player, CommandArguments args) {
        LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.colors.header");
        LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.colors.color_codes_description");
        LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.colors.color_codes");
        LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.colors.formatting_codes");
    }

}
