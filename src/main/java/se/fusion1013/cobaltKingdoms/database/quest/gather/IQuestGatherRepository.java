package se.fusion1013.cobaltKingdoms.database.quest.gather;

import se.fusion1013.cobaltCore.database.system.IDao;
import se.fusion1013.cobaltKingdoms.quest.item_gather.GatherQuestEntity;
import se.fusion1013.cobaltKingdoms.quest.item_gather.GatherQuestGoalEntity;

import java.util.List;

public interface IQuestGatherRepository extends IDao {

    @Override
    default String getId() {
        return "quest_gather";
    }

    void createGoal(GatherQuestGoalEntity goal);

    List<GatherQuestGoalEntity> getGoals();

    List<GatherQuestGoalEntity> getGoals(int difficulty);

    GatherQuestGoalEntity getGoal(Long id);

    void insertQuest(GatherQuestEntity quest);

    GatherQuestEntity getQuest(Long questId);
}
