package se.fusion1013.cobaltKingdoms.quest.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import se.fusion1013.cobaltKingdoms.database.ItemStackListPersister;
import se.fusion1013.cobaltKingdoms.util.ItemStackList;

@DatabaseTable(tableName = "quests_item_delivery")
public class ItemDeliveryQuestEntity {

    @DatabaseField(generatedId = true, columnName = "uuid")
    private Long id;

    @DatabaseField(columnName = "required_items", persisterClass = ItemStackListPersister.class)
    private ItemStackList requiredItems;

    @DatabaseField(columnName = "rewards", persisterClass = ItemStackListPersister.class)
    private ItemStackList rewards;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, columnName = "quest")
    private QuestEntity quest;

    public ItemDeliveryQuestEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QuestEntity getQuest() {
        return quest;
    }

    public void setQuest(QuestEntity quest) {
        this.quest = quest;
    }

    public ItemStackList getRequiredItems() {
        return requiredItems;
    }

    public void setRequiredItems(ItemStackList requiredItems) {
        this.requiredItems = requiredItems;
    }

    public ItemStackList getRewards() {
        return rewards;
    }

    public void setRewards(ItemStackList rewards) {
        this.rewards = rewards;
    }
}
