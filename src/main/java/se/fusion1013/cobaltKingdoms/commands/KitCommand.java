package se.fusion1013.cobaltKingdoms.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltCore.util.FileUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.items.kit.IKit;
import se.fusion1013.cobaltKingdoms.items.kit.Kit;
import se.fusion1013.cobaltKingdoms.items.kit.KitManager;

public class KitCommand {
    public static void register() {
        new CommandAPICommand("kit")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "kit"))
                .withSubcommand(KitCommand.createKitGetCommand())
                .withSubcommand(createKitPastebinCommand())
                .register();

    }

    private static CommandAPICommand createKitPastebinCommand() {
        return new CommandAPICommand("pastebin")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "kit.pastebin"))
                .withSubcommand(createKitPastebinTestCommand())
                .withSubcommand(createKitPastebinSaveCommand());
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

    private static CommandAPICommand createKitPastebinTestCommand() {
        return new CommandAPICommand("test")
                .withPermission("cobalt.core.commands.kit.pastebin.test")
                .withArguments(new StringArgument("id"))
                .executesPlayer((player, commandArguments) -> {
                    String pasteId = (String) commandArguments.args()[0];

                    try {
                        YamlConfiguration yaml = FileUtil.loadFromPastebin(pasteId);
                        IKit kit = Kit.create(yaml);
                        kit.apply(player);

                        StringPlaceholders placeholders = StringPlaceholders.builder()
                                .addPlaceholder("kit", kit.getInternalName())
                                .addPlaceholder("pastebin", pasteId)
                                .build();
                        LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "kingdoms.commands.kit.pastebin.test.success", placeholders);

                    } catch (Exception e) {
                        StringPlaceholders placeholders = StringPlaceholders.builder()
                                .addPlaceholder("pastebin", pasteId)
                                .addPlaceholder("error", e.getMessage())
                                .build();
                        LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "kingdoms.commands.kit.pastebin.test.error", placeholders);
                    }
                });
    }

    private static CommandAPICommand createKitPastebinSaveCommand() {
        return new CommandAPICommand("save")
                .withPermission("cobalt.core.commands.kit.pastebin.save")
                .withArguments(new StringArgument("id"))
                .executesPlayer((player, commandArguments) -> {
                    String pasteId = (String) commandArguments.get("id");

                    try {
                        YamlConfiguration yaml = FileUtil.loadFromPastebin(pasteId);
                        IKit kit = Kit.create(yaml);
                        FileUtil.saveYamlFile(CobaltKingdoms.getInstance(), "kit", kit.getInternalName(), yaml);
                        KitManager.reloadKits();

                        StringPlaceholders placeholders = StringPlaceholders.builder()
                                .addPlaceholder("kit", kit.getInternalName())
                                .addPlaceholder("pastebin", pasteId)
                                .build();
                        LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.kit.pastebin.save.success", placeholders);

                    } catch (Exception e) {
                        StringPlaceholders placeholders = StringPlaceholders.builder()
                                .addPlaceholder("pastebin", pasteId)
                                .addPlaceholder("error", e.getMessage())
                                .build();
                        LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.kit.pastebin.save.error", placeholders);
                    }
                });
    }
}
