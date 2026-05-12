package se.fusion1013.cobaltKingdoms.commands.quest.bounty;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.PlayerProfileArgument;
import dev.jorel.commandapi.arguments.SafeSuggestions;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.quest.bounty.BountyManager;

import java.util.List;

public class BountyCreateCommand {

    private static final Argument<?> noSelectorSuggestions = new PlayerProfileArgument("target")
            .replaceSafeSuggestions(SafeSuggestions.suggest(info ->
                    Bukkit.getOnlinePlayers().stream().map(Player::getPlayerProfile).toArray(PlayerProfile[]::new)
            ));

    public static CommandAPICommand register() {
        return new CommandAPICommand("create")
                .withArguments(noSelectorSuggestions)
                .withArguments(new GreedyStringArgument("reason"))
                .executesPlayer(BountyCreateCommand::createBounty);
    }

    private static void createBounty(Player player, CommandArguments args) {
        List<PlayerProfile> targetPlayers = (List<PlayerProfile>) args.get("target");
        String reason = (String) args.get("reason");

        if (targetPlayers == null || targetPlayers.isEmpty()) return;
        if (targetPlayers.size() > 1) return;

        tryCreateBounty(player, targetPlayers.getFirst(), reason, player.getInventory().getItemInMainHand());
    }

    private static void tryCreateBounty(Player owner, PlayerProfile target, String reason, ItemStack reward) {
        Response response = BountyManager.getInstance().create(owner, target, reason, reward);
        if (response.ok()) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), owner, "kingdoms.quests.bounty.create", StringPlaceholders.builder()
                    .addPlaceholder("player", target.getName())
                    .build());
            owner.getInventory().setItemInMainHand(ItemStack.empty());
        } else {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), owner, "kingdoms.quests.bounty.create_fail", StringPlaceholders.builder()
                    .addPlaceholder("reason", response.message())
                    .build());
        }
    }

}
