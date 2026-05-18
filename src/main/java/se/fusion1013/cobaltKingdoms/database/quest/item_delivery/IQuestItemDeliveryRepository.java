package se.fusion1013.cobaltKingdoms.database.quest.item_delivery;

import se.fusion1013.cobaltCore.database.system.IDao;
import se.fusion1013.cobaltKingdoms.quest.item_delivery.ItemDeliveryQuest;

import java.util.List;

public interface IQuestItemDeliveryRepository extends IDao {

    @Override
    default String getId() {
        return "item_delivery";
    }

    void createQuest(ItemDeliveryQuest quest);

    List<ItemDeliveryQuest> getQuests();
}
