package se.fusion1013.cobaltKingdoms.quest.command.bounty;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerProfileArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.quest.service.BountyManager;

import java.util.List;

public class BountyRecallCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("recall")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "bounty.recall"))
                .withArguments(new PlayerProfileArgument("target"))
                .executesPlayer(BountyRecallCommand::recall);
    }

    private static void recall(Player player, CommandArguments args) {
        List<PlayerProfile> target = (List<PlayerProfile>) args.get("target");

        if (target == null || target.isEmpty()) return;
        if (target.size() > 1) return;

        tryRecall(player, target.getFirst());
    }

    private static void tryRecall(Player owner, PlayerProfile target) {
        Response response = BountyManager.getInstance().recall(owner, target);

        if (response.ok()) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), owner, "kingdoms.quests.bounty.recall", StringPlaceholders.builder()
                    .addPlaceholder("player", target.getName())
                    .build());
        } else {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), owner, "kingdoms.quests.bounty.recall_fail", StringPlaceholders.builder()
                    .addPlaceholder("reason", response.message())
                    .build());
        }
    }

}
