package se.fusion1013.cobaltKingdoms.database.quest.item_delivery.mapper;

import se.fusion1013.cobaltKingdoms.database.quest.item_delivery.ItemDeliveryQuestEntity;
import se.fusion1013.cobaltKingdoms.database.quest.mapper.QuestMapper;
import se.fusion1013.cobaltKingdoms.quest.item_delivery.ItemDeliveryQuest;
import se.fusion1013.cobaltKingdoms.util.ItemStackList;

import java.util.List;

public final class ItemDeliveryQuestMapper {

    public static ItemDeliveryQuest toModel(ItemDeliveryQuestEntity entity) {
        ItemDeliveryQuest model = new ItemDeliveryQuest();

        QuestMapper.toModel(entity.getQuest(), model);

        model.setId(entity.getId());
        model.setRequiredItems(entity.getRequiredItems().list());
        model.setRewards(entity.getRewards().list());

        return model;
    }

    public static ItemDeliveryQuestEntity toEntity(ItemDeliveryQuest model) {
        ItemDeliveryQuestEntity entity = new ItemDeliveryQuestEntity();

        entity.setId(model.getId());
        entity.setRequiredItems(new ItemStackList(model.getRequiredItems()));
        entity.setRewards(new ItemStackList(model.getRewards()));

        return entity;
    }

    public static List<ItemDeliveryQuest> toModels(List<ItemDeliveryQuestEntity> entities) {
        return entities.stream().map(ItemDeliveryQuestMapper::toModel).toList();
    }

}
