package se.fusion1013.cobaltKingdoms.quest.mapper;

import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltKingdoms.quest.entity.PlayerQuestEntity;
import se.fusion1013.cobaltKingdoms.quest.model.HeadlessQuest;
import se.fusion1013.cobaltKingdoms.quest.model.PlayerQuest;
import se.fusion1013.cobaltKingdoms.quest.repository.IQuestRepository;

import java.util.List;

public final class PlayerQuestMapper {

    private static final IQuestRepository questRepository = DataManager.getInstance().getDao(IQuestRepository.class);

    public static PlayerQuest toModel(PlayerQuestEntity entity) {
        PlayerQuest model = new PlayerQuest(entity.getId());

        if (entity.getQuest() != null) {
            HeadlessQuest quest = new HeadlessQuest(entity.getQuest().getQuestType());
            QuestMapper.toModel(entity.getQuest(), quest);
            model.setQuest(quest);
        }

        model.setPlayerId(entity.getPlayerId());
        model.setPlayerName(entity.getPlayerName());
        model.setStartTime(entity.getStartTime());
        model.setExpiryTime(entity.getExpiryTime());
        return model;
    }

    public static PlayerQuestEntity toEntity(PlayerQuest model) {
        PlayerQuestEntity entity = new PlayerQuestEntity();

        if (model.getId() != null) entity.setId(model.getId());

        if (model.getQuest() != null) {
            entity.setQuest(QuestMapper.toEntity(model.getQuest()));
        }

        entity.setPlayerId(model.getPlayerId());
        entity.setPlayerName(model.getPlayerName());
        entity.setStartTime(model.getStartTime());
        entity.setExpiryTime(model.getExpiryTime());

        return entity;
    }

    public static List<PlayerQuest> toModels(List<PlayerQuestEntity> entities) {
        return entities.stream().map(PlayerQuestMapper::toModel).toList();
    }

}
