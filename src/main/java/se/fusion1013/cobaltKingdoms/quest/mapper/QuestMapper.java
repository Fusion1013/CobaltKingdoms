package se.fusion1013.cobaltKingdoms.quest.mapper;

import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltKingdoms.quest.entity.QuestEntity;
import se.fusion1013.cobaltKingdoms.quest.model.AbstractQuest;
import se.fusion1013.cobaltKingdoms.town.mapper.TownMapper;
import se.fusion1013.cobaltKingdoms.town.repository.ITownRepository;

public class QuestMapper {

    public static AbstractQuest toModel(QuestEntity entity, AbstractQuest model) {
        if (entity == null) return null;

        model.setQuestId(entity.getId());
        model.setQuestType(entity.getQuestType());
        model.setCreatedTimestamp(entity.getCreatedTimestamp());
        model.setMinRequirementValue(entity.getMinRequirementValue());
        model.setMaxRequirementValue(entity.getMaxRequirementValue());
        model.setMinRewardValue(entity.getMinRewardValue());
        model.setMaxRewardValue(entity.getMaxRewardValue());
        model.setQuestStatus(entity.getStatus());
        model.setCanDespawn(entity.canDespawn());

//        ITownRepository townRepository = DataManager.getInstance().getDao(ITownRepository.class);
//
//        if (entity.getStartTown() != null) model.setStartTown(townRepository.getTown(entity.getStartTown().getId()));
//        if (entity.getEndTown() != null) model.setEndTown(townRepository.getTown(entity.getEndTown().getId()));

        model.setStartTown(TownMapper.toModel(entity.getStartTown()));
        model.setEndTown(TownMapper.toModel(entity.getEndTown()));

        return model;
    }

    public static QuestEntity toEntity(AbstractQuest model) {
        QuestEntity entity = new QuestEntity();

        if (model.getQuestId() != null) entity.setId(model.getQuestId());
        entity.setQuestType(model.getQuestType());
        entity.setCreatedTimestamp(model.getCreatedTimestamp());
        entity.setMinRequirementValue(model.getMinRequirementValue());
        entity.setMaxRequirementValue(model.getMaxRequirementValue());
        entity.setMinRewardValue(model.getMinRewardValue());
        entity.setMaxRewardValue(model.getMaxRewardValue());
        entity.setStatus(model.getQuestStatus());
        entity.setCanDespawn(model.canDespawn());

        ITownRepository townRepository = DataManager.getInstance().getDao(ITownRepository.class);

        if (model.getStartTown() != null)
            entity.setStartTown(TownMapper.toEntity(townRepository.getTown(model.getStartTown().getId())));
        if (model.getEndTown() != null)
            entity.setEndTown(TownMapper.toEntity(townRepository.getTown(model.getEndTown().getId())));

        return entity;
    }

}
