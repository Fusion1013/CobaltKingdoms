package se.fusion1013.cobaltKingdoms.database.quest.artifact_hunt;

import se.fusion1013.cobaltCore.database.system.IDao;
import se.fusion1013.cobaltKingdoms.quest.artifact_hunt.ArtifactHuntEntity;
import se.fusion1013.cobaltKingdoms.quest.artifact_hunt.ArtifactHuntQuestGoalEntity;

import java.util.List;

public interface IQuestArtifactHuntRepository extends IDao {

    @Override
    default String getId() {
        return "quest_gather";
    }

    void createGoal(ArtifactHuntQuestGoalEntity goal);

    List<ArtifactHuntQuestGoalEntity> getGoals();

    List<ArtifactHuntQuestGoalEntity> getGoals(int difficulty);

    ArtifactHuntQuestGoalEntity getGoal(Long id);

    void insertQuest(ArtifactHuntEntity quest);

    ArtifactHuntEntity getQuest(Long questId);

    int getHighestDifficulty();

}
