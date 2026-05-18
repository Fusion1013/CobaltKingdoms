package se.fusion1013.cobaltKingdoms.quest;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.kingdom.town.Town;

public interface IQuestData {

    boolean tryComplete(Player player, @NotNull Location location, Town clickedTown);

    void start(@NotNull Player player, @NotNull Location location);

    ItemStack getInstructionsItem();

    boolean validateQuest(Player player);

    String getTitle();

    String getSymbol();

    ItemStack getButtonItem();

    int getXpValue();

    boolean shouldShowInMenu(Town town, Player player);

    boolean isValid();

    void fail(Player player, QuestFailReason reason);

    Response canClaim(Player player);

    int getDuration();
}
