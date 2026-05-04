package se.fusion1013.cobaltKingdoms.quest;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownData;

import java.util.UUID;

public interface IQuest {

    UUID getId();

    Component getEntityName();

    QuestType getQuestType();

    Long getCreatedTimestamp();

    ItemStack getQuestToken();

    ItemStack getQuestDescriptionItem();

    TownData getHandInTown();


    void start(Player player, Location location);

    boolean tryFinish(Player player, Location location);

}
