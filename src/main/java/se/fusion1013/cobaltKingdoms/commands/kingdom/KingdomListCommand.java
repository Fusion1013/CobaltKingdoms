package se.fusion1013.cobaltKingdoms.commands.kingdom;

import dev.jorel.commandapi.CommandAPICommand;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomInfo;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomManager;

import java.util.Comparator;
import java.util.List;

public class KingdomListCommand {

    private static final KingdomManager KINGDOM_MANAGER = CobaltCore.getInstance().getManager(CobaltKingdoms.getInstance(), KingdomManager.class);
    private static final LocaleManager LOCALE = CobaltCore.getInstance().getManager(CobaltCore.getInstance(), LocaleManager.class);

    public static CommandAPICommand register() {
        return new CommandAPICommand("list")
                .withPermission("cobalt.kingdom.commands.kingdom.list")
                .executesPlayer((sender, args) -> {
                    LOCALE.sendMessage("", sender, "kingdoms.commands.kingdom.list.header");
                    List<KingdomInfo> kingdomInfos = KINGDOM_MANAGER.getKingdomInfo();
                    kingdomInfos = kingdomInfos.stream().sorted(Comparator.comparingInt(k -> k.members().size())).toList().reversed();

                    for (KingdomInfo kingdomInfo : kingdomInfos) {
                        StringPlaceholders placeholders = StringPlaceholders.builder()
                                .addPlaceholder("kingdom", kingdomInfo.name())
                                .addPlaceholder("member_count", kingdomInfo.members().size())
                                .build();
                        LOCALE.sendMessage("", sender, "kingdoms.commands.kingdom.list.kingdom_item", placeholders);
                    }
                });
    }

}
