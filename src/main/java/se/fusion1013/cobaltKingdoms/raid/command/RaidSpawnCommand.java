package se.fusion1013.cobaltKingdoms.raid.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.raid.service.RaidService;

public class RaidSpawnCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("spawn")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "spawn"))
                .withArguments(RaidCommand.RAID_DEFINITION_ARGUMENT)
                .withArguments(new LocationArgument("location", LocationType.BLOCK_POSITION))
                .executes(RaidSpawnCommand::spawnRaid);
    }

    private static void spawnRaid(CommandSender sender, CommandArguments args) {
        String raidDefinitionName = (String) args.get("raid");
        Location location = (Location) args.get("location");

        Response response = RaidService.getInstance().startRaid(raidDefinitionName, location);
        if (!(sender instanceof Player player)) return;

        if (response.ok()) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.raid.spawn");
        } else {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.raid.spawn_fail", StringPlaceholders.builder()
                    .addPlaceholder("reason", response.message())
                    .build());
        }
    }

}
