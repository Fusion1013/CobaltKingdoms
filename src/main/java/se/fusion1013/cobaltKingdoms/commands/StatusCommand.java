package se.fusion1013.cobaltKingdoms.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.player.PlayerManager;
import se.fusion1013.cobaltKingdoms.player.PlayerStatus;

import java.util.Arrays;

public class StatusCommand {

    private static final LocaleManager LOCALE = CobaltCore.getInstance().getManager(CobaltCore.getInstance(), LocaleManager.class);

    public static void register() {
        String[] values = Arrays.stream(PlayerStatus.values()).map(Enum::name).toList().toArray(new String[0]);
        new CommandAPICommand("status")
                .withPermission("cobalt.kingdoms.command.status")
                .withArguments(new MultiLiteralArgument("status", values))
                .executesPlayer((sender, args) -> {
                    PlayerManager playerManager = CobaltCore.getInstance().getManager(CobaltKingdoms.getInstance(), PlayerManager.class);

                    String statusName = (String) args.get("status");
                    PlayerStatus status = PlayerStatus.valueOf(statusName);

                    playerManager.setPlayerStatus(sender.getUniqueId(), status);

                    StringPlaceholders placeholders = StringPlaceholders.builder()
                            .addPlaceholder("status", status.name())
                            .build();
                    LOCALE.sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.status.change", placeholders);

                }).register();
    }

}
