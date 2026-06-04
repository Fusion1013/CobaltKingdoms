package se.fusion1013.cobaltKingdoms.quest.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltKingdoms.database.ItemStackListPersister;
import se.fusion1013.cobaltKingdoms.util.ItemStackList;

import java.util.List;

@DatabaseTable(tableName = "quests_artifact_hunt")
public class ArtifactHuntEntity {

    @DatabaseField(generatedId = true, columnName = "id")
    private Long id;

    @DatabaseField(columnName = "rewards", persisterClass = ItemStackListPersister.class)
    private ItemStackList rewards;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private QuestEntity quest;

    @DatabaseField(foreign = true, foreignAutoCreate = false, foreignAutoRefresh = true)
    private ArtifactHuntQuestGoalEntity goal;

    public ArtifactHuntEntity() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public List<ItemStack> getRewards() {
        return rewards.list();
    }

    public void setRewards(List<ItemStack> rewards) {
        this.rewards = new ItemStackList(rewards);
    }

    public QuestEntity getQuest() {
        return quest;
    }

    public void setQuest(QuestEntity quest) {
        this.quest = quest;
    }

    public ArtifactHuntQuestGoalEntity getGoal() {
        return goal;
    }

    public void setGoal(ArtifactHuntQuestGoalEntity goal) {
        this.goal = goal;
    }
}
