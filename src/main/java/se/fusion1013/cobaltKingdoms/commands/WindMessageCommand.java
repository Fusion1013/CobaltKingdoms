package se.fusion1013.cobaltKingdoms.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

public class WindMessageCommand {

    public static void register() {
        new CommandAPICommand("windmsg")
                .withAliases("wind")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "windmsg"))
                .withArguments(new GreedyStringArgument("text"))
                .executes(WindMessageCommand::printWindMessage)
                .register();
    }

    private static void printWindMessage(CommandSender sender, CommandArguments args) {
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.sendMessage(Component.text((String) args.get("text")).decoration(TextDecoration.ITALIC, true).color(NamedTextColor.GRAY));
        });
    }

}
