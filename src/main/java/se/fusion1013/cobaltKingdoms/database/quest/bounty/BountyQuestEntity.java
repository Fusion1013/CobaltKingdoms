package se.fusion1013.cobaltKingdoms.database.quest.bounty;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltKingdoms.database.ItemStackPersister;
import se.fusion1013.cobaltKingdoms.database.quest.QuestEntity;

import java.util.UUID;

@DatabaseTable(tableName = "quest_bounty")
public class BountyQuestEntity {

    @DatabaseField(generatedId = true, columnName = "id")
    private Long id;

    @DatabaseField(columnName = "owner_player_id")
    private UUID ownerPlayerId;

    @DatabaseField(columnName = "owner_player_name")
    private String ownerPlayerName;

    @DatabaseField(columnName = "target_player_id")
    private UUID targetPlayerId;

    @DatabaseField(columnName = "target_player_name")
    private String targetPlayerName;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, columnName = "quest")
    private QuestEntity quest;

    @DatabaseField(columnName = "reason")
    private String reason;

    @DatabaseField(columnName = "reward", persisterClass = ItemStackPersister.class)
    private ItemStack reward;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public UUID getTargetPlayerId() {
        return targetPlayerId;
    }

    public void setTargetPlayerId(UUID targetPlayerId) {
        this.targetPlayerId = targetPlayerId;
    }

    public QuestEntity getQuest() {
        return quest;
    }

    public void setQuest(QuestEntity quest) {
        this.quest = quest;
    }

    public UUID getOwnerPlayerId() {
        return ownerPlayerId;
    }

    public void setOwnerPlayerId(UUID ownerPlayerId) {
        this.ownerPlayerId = ownerPlayerId;
    }

    public String getOwnerPlayerName() {
        return ownerPlayerName;
    }

    public void setOwnerPlayerName(String ownerPlayerName) {
        this.ownerPlayerName = ownerPlayerName;
    }

    public String getTargetPlayerName() {
        return targetPlayerName;
    }

    public void setTargetPlayerName(String targetPlayerName) {
        this.targetPlayerName = targetPlayerName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ItemStack getReward() {
        return reward;
    }

    public void setReward(ItemStack reward) {
        this.reward = reward;
    }
}
