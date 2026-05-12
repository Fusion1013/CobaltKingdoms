package se.fusion1013.cobaltKingdoms.commands.quest;

import dev.jorel.commandapi.CommandAPICommand;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.commands.quest.gather.QuestGatherCommand;

public class QuestCommand {

    public static void register() {
        new CommandAPICommand("quest")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "quest"))
                .withSubcommand(QuestGatherCommand.register())
                .register();
    }

}
