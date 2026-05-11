package se.fusion1013.cobaltKingdoms.quest;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltKingdoms.database.quest.IQuestRepository;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownEntity;

import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

@DatabaseTable(tableName = "quests")
public class QuestEntity {

    @DatabaseField(generatedId = true, columnName = "id")
    private Long id;

    @DatabaseField(columnName = "quest_type")
    private QuestType questType;

    @DatabaseField(columnName = "created_timestamp")
    private Date createdTimestamp;

    @DatabaseField(columnName = "min_requirement_value")
    private float minRequirementValue;

    @DatabaseField(columnName = "max_requirement_value")
    private float maxRequirementValue;

    @DatabaseField(columnName = "min_reward_value")
    private float minRewardValue;

    @DatabaseField(columnName = "max_reward_value")
    private float maxRewardValue;

    @DatabaseField(columnName = "status")
    private QuestStatus status;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true, columnName = "start_town")
    private TownEntity startTown;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true, columnName = "end_town")
    private TownEntity endTown;

    @DatabaseField(columnName = "can_despawn")
    private boolean canDespawn;

    private final ReentrantLock lock = new ReentrantLock();

    public QuestEntity() {
    }

    public QuestEntity(QuestType questType, Date createdTimestamp, float minRequirementValue, float maxRequirementValue, float minRewardValue, float maxRewardValue, QuestStatus status, TownEntity startTown, TownEntity endTown) {
        this.questType = questType;
        this.createdTimestamp = createdTimestamp;
        this.minRequirementValue = minRequirementValue;
        this.maxRequirementValue = maxRequirementValue;
        this.minRewardValue = minRewardValue;
        this.maxRewardValue = maxRewardValue;
        this.status = status;
        this.startTown = startTown;
        this.endTown = endTown;
    }

    public Long getId() {
        return id;
    }

    public QuestType getQuestType() {
        return questType;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public float getMinRequirementValue() {
        return minRequirementValue;
    }

    public float getMaxRequirementValue() {
        return maxRequirementValue;
    }

    public float getMinRewardValue() {
        return minRewardValue;
    }

    public float getMaxRewardValue() {
        return maxRewardValue;
    }

    public QuestStatus getStatus() {
        return status;
    }

    public TownEntity getStartTown() {
        return startTown;
    }

    public TownEntity getEndTown() {
        return endTown;
    }

    public void setStatus(QuestStatus status) {
        this.status = status;
    }

    public void acquireLock() {
        lock.lock();
    }

    public void releaseLock() {
        lock.unlock();
    }

    public boolean isValid() {
        return getQuestData().isValid();
    }

    public IQuestData getQuestData() {
        return DataManager.getInstance().getDao(IQuestRepository.class).getQuestData(id, questType);
    }

    public boolean canDespawn() {
        return canDespawn;
    }

    public void setCanDespawn(boolean canDespawn) {
        this.canDespawn = canDespawn;
    }
}
