package se.fusion1013.cobaltKingdoms.raid.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.raid.service.RaidService;

public class RaidCommand {

    public static final Argument<String> RAID_DEFINITION_ARGUMENT = new StringArgument("raid").replaceSuggestions(ArgumentSuggestions.strings(k -> RaidService.getInstance().getRaidDefinitionNames()));

    public static void register() {
        new CommandAPICommand("raid")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "raid"))
                .withSubcommand(RaidSpawnCommand.register())
                .register();
    }

}
