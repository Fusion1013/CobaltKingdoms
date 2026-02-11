package se.fusion1013.cobaltKingdoms.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.player.PlayerManager;
import se.fusion1013.cobaltKingdoms.player.status.PlayerStatus;

import java.util.Arrays;

public class StatusCommand {

    private static final PlayerManager PLAYER_MANAGER = CobaltCore.getInstance().getManager(CobaltKingdoms.getInstance(), PlayerManager.class);
    private static final LocaleManager LOCALE = CobaltCore.getInstance().getManager(CobaltCore.getInstance(), LocaleManager.class);

    public static void register() {
        String[] values = Arrays.stream(PlayerStatus.values()).map(Enum::name).toList().toArray(new String[0]);
        new CommandAPICommand("status")
                .withPermission("cobalt.kingdoms.command.status")
                .withArguments(new MultiLiteralArgument("status", values))
                .executesPlayer(StatusCommand::updatePlayerStatus).register();

        new CommandAPICommand("afk")
                .withPermission("cobalt.kingdoms.command.status")
                .executesPlayer((sender, args) -> {
                    updateStatus(sender, PlayerStatus.AFK);
                }).register();

        new CommandAPICommand("ic")
                .withPermission("cobalt.kingdoms.command.status")
                .executesPlayer((sender, args) -> {
                    updateStatus(sender, PlayerStatus.IN_CHARACTER);
                }).register();

        new CommandAPICommand("ooc")
                .withPermission("cobalt.kingdoms.command.status")
                .executesPlayer((sender, args) -> {
                    updateStatus(sender, PlayerStatus.OUT_OF_CHARACTER);
                }).register();

        new CommandAPICommand("oic")
                .withPermission("cobalt.kingdoms.command.status")
                .executesPlayer((sender, args) -> {
                    updateStatus(sender, PlayerStatus.OPEN_FOR_IN_CHARACTER);
                }).register();
    }

    private static void updatePlayerStatus(Player sender, CommandArguments args) {
        String statusName = (String) args.get("status");
        PlayerStatus status = PlayerStatus.valueOf(statusName);
        updateStatus(sender, status);
    }

    private static void updateStatus(Player sender, PlayerStatus newPlayerStatus) {
        PLAYER_MANAGER.setPlayerStatus(sender.getUniqueId(), newPlayerStatus);

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("status", newPlayerStatus.name())
                .build();
        LOCALE.sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.status.change", placeholders);
    }

}
