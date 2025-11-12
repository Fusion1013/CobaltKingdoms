package se.fusion1013.cobaltKingdoms.commands.kingdom;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomManager;

public class KingdomModifyCommand {

    private static final KingdomManager KINGDOM_MANAGER = CobaltCore.getInstance().getManager(CobaltKingdoms.getInstance(), KingdomManager.class);
    private static final LocaleManager LOCALE = CobaltCore.getInstance().getManager(CobaltCore.getInstance(), LocaleManager.class);

    public static CommandAPICommand register() {
        return new CommandAPICommand("modify")
                .withPermission("cobalt.kingdom.commands.kingdom.modify")
                .withSubcommand(modifyColorCommand());
    }

    private static CommandAPICommand modifyColorCommand() {
        return new CommandAPICommand("color")
                .withPermission("cobalt.kingdom.commands.kingdom.modify.color")
                .withArguments(new StringArgument("kingdom_name").replaceSuggestions(ArgumentSuggestions.strings(si -> KINGDOM_MANAGER.getKingdomNames(((Player) si.sender()).getUniqueId()).toArray(new String[0]))))
                .withArguments(new GreedyStringArgument("color"))
                .executesPlayer((sender, args) -> {
                    String kingdomName = (String) args.get("kingdom_name");
                    String colorPrefix = (String) args.get("color");
                    KINGDOM_MANAGER.setKingdomColor(kingdomName, colorPrefix);
                });
    }

}
