package se.fusion1013.cobaltKingdoms.quest.repository;

import se.fusion1013.cobaltCore.database.system.IDao;
import se.fusion1013.cobaltKingdoms.quest.model.ItemDeliveryQuest;

import java.util.List;

public interface IQuestItemDeliveryRepository extends IDao {

    @Override
    default String getId() {
        return "item_delivery";
    }

    void createQuest(ItemDeliveryQuest quest);

    List<ItemDeliveryQuest> getQuests();

    void deleteQuestsWithIds(List<Long> ids);
}
