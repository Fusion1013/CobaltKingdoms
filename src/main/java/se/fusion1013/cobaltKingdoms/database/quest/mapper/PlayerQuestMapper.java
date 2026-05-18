package se.fusion1013.cobaltKingdoms.database.quest.mapper;

import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltKingdoms.database.quest.IQuestRepository;
import se.fusion1013.cobaltKingdoms.database.quest.PlayerQuestEntity;
import se.fusion1013.cobaltKingdoms.quest.AbstractQuest;
import se.fusion1013.cobaltKingdoms.quest.PlayerQuest;

import java.util.List;
import java.util.Optional;

public final class PlayerQuestMapper {

    private static final IQuestRepository questRepository = DataManager.getInstance().getDao(IQuestRepository.class);

    public static PlayerQuest toModel(PlayerQuestEntity entity) {
        PlayerQuest model = new PlayerQuest(entity.getId());

        if (entity.getQuest() != null) {
            Optional<AbstractQuest> quest = questRepository.getQuest(entity.getQuest().getId());
            quest.ifPresent(model::setQuest);
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
