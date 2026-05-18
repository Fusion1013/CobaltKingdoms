package se.fusion1013.cobaltKingdoms.database.quest.artifact_hunt;

import se.fusion1013.cobaltCore.database.system.IDao;
import se.fusion1013.cobaltKingdoms.quest.artifact_hunt.ArtifactHuntGoal;
import se.fusion1013.cobaltKingdoms.quest.artifact_hunt.ArtifactHuntQuest;

import java.util.List;

public interface IQuestArtifactHuntRepository extends IDao {

    @Override
    default String getId() {
        return "quest_gather";
    }

    void createGoal(ArtifactHuntGoal goal);

    List<ArtifactHuntGoal> getGoals();

    List<ArtifactHuntGoal> getGoals(int difficulty);

    ArtifactHuntGoal getGoal(Long id);

    void createQuest(ArtifactHuntQuest quest);

    ArtifactHuntQuest getQuest(Long questId);

    List<ArtifactHuntQuest> getQuests();

    int getHighestDifficulty();

}
