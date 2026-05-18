package se.fusion1013.cobaltKingdoms.database.quest;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import se.fusion1013.cobaltKingdoms.database.kingdom.town.TownEntity;
import se.fusion1013.cobaltKingdoms.quest.QuestStatus;
import se.fusion1013.cobaltKingdoms.quest.QuestType;

import java.util.Date;

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

    public QuestEntity() {
    }

    public QuestEntity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QuestType getQuestType() {
        return questType;
    }

    public void setQuestType(QuestType questType) {
        this.questType = questType;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public float getMinRequirementValue() {
        return minRequirementValue;
    }

    public void setMinRequirementValue(float minRequirementValue) {
        this.minRequirementValue = minRequirementValue;
    }

    public float getMaxRequirementValue() {
        return maxRequirementValue;
    }

    public void setMaxRequirementValue(float maxRequirementValue) {
        this.maxRequirementValue = maxRequirementValue;
    }

    public float getMinRewardValue() {
        return minRewardValue;
    }

    public void setMinRewardValue(float minRewardValue) {
        this.minRewardValue = minRewardValue;
    }

    public float getMaxRewardValue() {
        return maxRewardValue;
    }

    public void setMaxRewardValue(float maxRewardValue) {
        this.maxRewardValue = maxRewardValue;
    }

    public QuestStatus getStatus() {
        return status;
    }

    public void setStatus(QuestStatus status) {
        this.status = status;
    }

    public TownEntity getStartTown() {
        return startTown;
    }

    public void setStartTown(TownEntity startTown) {
        this.startTown = startTown;
    }

    public TownEntity getEndTown() {
        return endTown;
    }

    public void setEndTown(TownEntity endTown) {
        this.endTown = endTown;
    }

    public boolean canDespawn() {
        return canDespawn;
    }

    public void setCanDespawn(boolean canDespawn) {
        this.canDespawn = canDespawn;
    }
}
