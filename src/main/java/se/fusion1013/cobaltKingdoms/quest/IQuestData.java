package se.fusion1013.cobaltKingdoms.quest;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownEntity;

public interface IQuestData {

    boolean tryComplete(Player player, @NotNull Location location, TownEntity clickedTown);

    void start(@NotNull Player player, @NotNull Location location);

    ItemStack getInstructionsItem();

    boolean validateQuest(Player player);

    Component getTitle();

    String getSymbol();

    ItemStack getButtonItem();

    int getXpValue();

    boolean shouldShowInMenu(TownEntity town, Player player);

    boolean isValid();
}
