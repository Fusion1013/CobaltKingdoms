package se.fusion1013.cobaltKingdoms.database.quest;

import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.database.system.IDao;
import se.fusion1013.cobaltKingdoms.kingdom.town.Town;
import se.fusion1013.cobaltKingdoms.quest.AbstractQuest;
import se.fusion1013.cobaltKingdoms.quest.PlayerQuest;
import se.fusion1013.cobaltKingdoms.quest.QuestStatus;

import java.util.List;
import java.util.Optional;

public interface IQuestRepository extends IDao {

    @Override
    default String getId() {
        return "quest";
    }

    List<AbstractQuest> getQuests();

    List<AbstractQuest> getQuests(Town town);

    Optional<AbstractQuest> getQuest(Long questId);

    void updateStatus(Long id, QuestStatus questStatus);


    void createPlayerQuest(PlayerQuest activeQuest);

    Optional<PlayerQuest> getPlayerQuestByQuestId(Long questId);

    @Deprecated
    void removePlayerQuestById(Long id);

    Optional<List<PlayerQuest>> getPlayerQuestsByPlayer(Player player);

    List<PlayerQuest> getPlayerQuests();

    QuestEntity getQuestEntity(Long id);

    void createQuest(AbstractQuest quest);
}
