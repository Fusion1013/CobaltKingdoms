package se.fusion1013.cobaltKingdoms.quest.model;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.town.model.Town;

import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractQuest {

    private Long questId;
    protected QuestType questType;
    protected Date createdTimestamp;
    protected float minRequirementValue;
    protected float maxRequirementValue;
    protected float minRewardValue;
    protected float maxRewardValue;
    protected QuestStatus questStatus;
    protected Town startTown;
    protected Town endTown;
    protected boolean canDespawn;

    private final ReentrantLock lock = new ReentrantLock();

    public AbstractQuest(QuestType questType) {
        this.questType = questType;
    }

    public AbstractQuest(Long questId, QuestType questType) {
        this.questId = questId;
        this.questType = questType;
    }


    public abstract boolean tryComplete(@NotNull Player player, @NotNull Location location, Long locationId);

    public abstract void start(@NotNull Player player, @NotNull Location location);

    public abstract void fail(@NotNull Player player, QuestFailReason reason);


    public abstract ItemStack getButtonItem();

    public abstract String getTitle();

    public abstract Response canClaim(@NotNull Player player);

    public abstract ItemStack getInstructionsItem();


    public abstract int getDuration();

    public abstract int getXpValue();

    public abstract boolean isValid();

    public abstract boolean validateQuest(@NotNull Player player);

    public abstract boolean shouldShowInMenu(Town startTown, Player player);


    public void acquireLock() {
        lock.lock();
    }

    public void releaseLock() {
        lock.unlock();
    }

    // ##%%##%%## GETTERS / SETTERS ##%%##%%## //

    public Long getQuestId() {
        return questId;
    }

    public void setQuestId(Long questId) {
        this.questId = questId;
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

    public QuestStatus getQuestStatus() {
        return questStatus;
    }

    public void setQuestStatus(QuestStatus questStatus) {
        this.questStatus = questStatus;
    }

    public Town getStartTown() {
        return startTown;
    }

    public void setStartTown(Town startTown) {
        this.startTown = startTown;
    }

    public Town getEndTown() {
        return endTown;
    }

    public void setEndTown(Town endTown) {
        this.endTown = endTown;
    }

    public boolean canDespawn() {
        return canDespawn;
    }

    public void setCanDespawn(boolean canDespawn) {
        this.canDespawn = canDespawn;
    }
}
