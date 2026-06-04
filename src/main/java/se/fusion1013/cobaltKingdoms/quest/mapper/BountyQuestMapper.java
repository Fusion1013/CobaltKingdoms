package se.fusion1013.cobaltKingdoms.quest.mapper;

import se.fusion1013.cobaltKingdoms.quest.entity.BountyQuestEntity;
import se.fusion1013.cobaltKingdoms.quest.model.BountyQuest;

import java.util.List;

public class BountyQuestMapper {

    public static BountyQuest toModel(BountyQuestEntity entity) {
        BountyQuest model = new BountyQuest();
        QuestMapper.toModel(entity.getQuest(), model);

        model.setId(entity.getId());
        model.setOwnerPlayerId(entity.getOwnerPlayerId());
        model.setOwnerPlayerName(entity.getOwnerPlayerName());
        model.setTargetPlayerId(entity.getTargetPlayerId());
        model.setTargetPlayerName(entity.getTargetPlayerName());
        model.setReason(entity.getReason());
        model.setReward(entity.getReward());

        return model;
    }

    public static BountyQuestEntity toEntity(BountyQuest model) {
        BountyQuestEntity entity = new BountyQuestEntity();

        if (model.getId() != null) entity.setId(model.getId());
        entity.setId(model.getId());
        entity.setOwnerPlayerId(model.getOwnerPlayerId());
        entity.setOwnerPlayerName(model.getOwnerPlayerName());
        entity.setTargetPlayerId(model.getTargetPlayerId());
        entity.setTargetPlayerName(model.getTargetPlayerName());
        entity.setReason(model.getReason());
        entity.setReward(model.getReward());

        return entity;
    }

    public static List<BountyQuest> toModels(List<BountyQuestEntity> entities) {
        return entities.stream().map(BountyQuestMapper::toModel).toList();
    }

}
