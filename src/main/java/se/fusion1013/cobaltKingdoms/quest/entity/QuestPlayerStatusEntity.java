package se.fusion1013.cobaltKingdoms.quest.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import se.fusion1013.cobaltKingdoms.quest.model.QuestStatus;
import se.fusion1013.cobaltKingdoms.quest.model.QuestType;

@DatabaseTable(tableName = "quest_player_status")
public class QuestPlayerStatusEntity {

    @DatabaseField(columnName = "id")
    private Long questId;

    @DatabaseField(columnName = "player_name")
    private String playerName;

    @DatabaseField(columnName = "status")
    private QuestStatus status;

    @DatabaseField(columnName = "quest_type")
    private QuestType questType;

    public QuestPlayerStatusEntity() {

    }

    public Long getQuestId() {
        return questId;
    }

    public void setQuestId(Long questId) {
        this.questId = questId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public QuestStatus getStatus() {
        return status;
    }

    public void setStatus(QuestStatus status) {
        this.status = status;
    }

    public QuestType getQuestType() {
        return questType;
    }

    public void setQuestType(QuestType questType) {
        this.questType = questType;
    }
}
