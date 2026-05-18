package se.fusion1013.cobaltKingdoms.quest.artifact_hunt;

import org.bukkit.Location;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.database.quest.artifact_hunt.IQuestArtifactHuntRepository;
import se.fusion1013.cobaltKingdoms.quest.QuestUtil;

import java.util.List;
import java.util.Random;

public class ArtifactHuntQuestManager extends Manager<CobaltKingdoms> {
    private static final Random random = new Random();

    private static final DataManager dataManager = DataManager.getInstance();
    private static final IQuestArtifactHuntRepository artifactHuntQuestRepository = dataManager.getDao(IQuestArtifactHuntRepository.class);

    public Response createQuestGoal(String name, int difficulty, Location location, String itemName, String description) {
        ArtifactHuntGoal goal = new ArtifactHuntGoal();
        goal.setName(name);
        goal.setDifficulty(difficulty);
        goal.setLocation(location);
        goal.setItemName(itemName);
        goal.setDescription(description);
        artifactHuntQuestRepository.createGoal(goal);
        return Response.ok("Created new goal");
    }

    public ArtifactHuntQuestManager(CobaltKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    private static ArtifactHuntQuestManager INSTANCE;

    public static ArtifactHuntQuestManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ArtifactHuntQuestManager(CobaltKingdoms.getInstance());
        }
        return INSTANCE;
    }


    // ##%%##%%## GETTERS / SETTERS ##%%##%%## //
    public List<ArtifactHuntGoal> getGoals() {
        return artifactHuntQuestRepository.getGoals();
    }

    public ArtifactHuntGoal getRandomGoal(int difficulty) {
        List<ArtifactHuntGoal> goals = artifactHuntQuestRepository.getGoals(difficulty);
        if (goals.isEmpty()) return null;
        return goals.get(random.nextInt(goals.size()));
    }

    public ArtifactHuntGoal getRandomGoal() {
        int highestDifficulty = artifactHuntQuestRepository.getHighestDifficulty();
        if (highestDifficulty < 0) return null;

        int selectedDifficulty = QuestUtil.getRandomWeighted(highestDifficulty, 1.5);
        return getRandomGoal(selectedDifficulty);
    }
}
