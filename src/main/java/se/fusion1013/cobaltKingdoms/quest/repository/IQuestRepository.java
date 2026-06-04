package se.fusion1013.cobaltKingdoms.quest.repository;

import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.database.system.IDao;
import se.fusion1013.cobaltKingdoms.quest.entity.QuestEntity;
import se.fusion1013.cobaltKingdoms.quest.model.AbstractQuest;
import se.fusion1013.cobaltKingdoms.quest.model.PlayerQuest;
import se.fusion1013.cobaltKingdoms.quest.model.QuestPlayerStatus;
import se.fusion1013.cobaltKingdoms.quest.model.QuestStatus;
import se.fusion1013.cobaltKingdoms.town.model.Town;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IQuestRepository extends IDao {

    @Override
    default String getId() {
        return "quest";
    }

    List<AbstractQuest> getQuests();

    List<AbstractQuest> getQuestsWithStatus(QuestStatus status);

    List<AbstractQuest> getQuestsWithoutStatus(QuestStatus status);

    List<AbstractQuest> getQuests(Town town);

    Optional<AbstractQuest> getQuest(Long questId);

    void updateStatus(Long id, QuestStatus questStatus);


    void createPlayerQuest(PlayerQuest activeQuest);

    Optional<PlayerQuest> getPlayerQuestByQuestId(Long questId);

    @Deprecated
    void removePlayerQuestById(Long id);

    List<PlayerQuest> getPlayerQuestsByPlayer(Player player);

    List<PlayerQuest> getPlayerQuests();

    QuestEntity getQuestEntity(Long id);

    void createQuest(AbstractQuest quest);

    List<QuestPlayerStatus> getPlayerQuestStatus(UUID playerId);

}
