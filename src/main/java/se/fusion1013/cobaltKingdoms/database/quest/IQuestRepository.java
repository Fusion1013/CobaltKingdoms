package se.fusion1013.cobaltKingdoms.database.quest;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.database.system.IDao;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownEntity;
import se.fusion1013.cobaltKingdoms.quest.*;
import se.fusion1013.cobaltKingdoms.quest.bounty.BountyQuestEntity;
import se.fusion1013.cobaltKingdoms.quest.item_delivery.ItemDeliveryQuestEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IQuestRepository extends IDao {

    @Override
    default String getId() {
        return "quest";
    }

    List<QuestEntity> getQuests();

    List<QuestEntity> getQuests(TownEntity town);

    void insertQuest(ItemDeliveryQuestEntity quest);

    QuestEntity getQuest(Long questId);

    void updateStatus(Long id, QuestStatus questStatus);

    IQuestData getQuestData(Long questId, QuestType questType);

    void insertActiveQuest(ActivePlayerQuestEntity activeQuest);

    Optional<ActivePlayerQuestEntity> getActivePlayerQuestByQuestId(Long questId);

    void removeActivePlayerQuestById(Long id);

    Optional<List<ActivePlayerQuestEntity>> getActivePlayerQuestsByPlayer(Player player);

    List<ActivePlayerQuestEntity> getActiveQuests();

    void insertQuest(BountyQuestEntity quest);

    List<BountyQuestEntity> getBounties(Player owner, PlayerProfile target);

    List<BountyQuestEntity> getBounties(UUID targetId);
}
