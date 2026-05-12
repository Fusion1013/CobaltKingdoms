package se.fusion1013.cobaltKingdoms.quest.item_gather;

import org.bukkit.Location;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.database.quest.gather.IQuestGatherRepository;

import java.util.List;
import java.util.Random;

public class GatherQuestManager extends Manager<CobaltKingdoms> {

    private static final Random random = new Random();
    private static final DataManager dataManager = DataManager.getInstance();
    private static final IQuestGatherRepository gatherQuestRepository = dataManager.getDao(IQuestGatherRepository.class);

    public Response createQuestGoal(String name, int difficulty, Location location, String itemName) {
        GatherQuestGoalEntity goal = new GatherQuestGoalEntity();
        goal.setName(name);
        goal.setDifficulty(difficulty);
        goal.setLocation(location);
        goal.setItemName(itemName);
        gatherQuestRepository.createGoal(goal);
        return Response.ok("Created new goal");
    }

    public GatherQuestManager(CobaltKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    private static GatherQuestManager INSTANCE;

    public static GatherQuestManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GatherQuestManager(CobaltKingdoms.getInstance());
        }
        return INSTANCE;
    }

    // ##%%##%%## GETTERS / SETTERS ##%%##%%## //

    public List<GatherQuestGoalEntity> getGoals() {
        return gatherQuestRepository.getGoals();
    }

    public GatherQuestGoalEntity getRandomGoal(int difficulty) {
        List<GatherQuestGoalEntity> goals = gatherQuestRepository.getGoals(difficulty);
        if (goals.isEmpty()) return null;
        return goals.get(random.nextInt(goals.size()));
    }
}
