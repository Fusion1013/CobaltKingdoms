package se.fusion1013.cobaltKingdoms.quest.mapper;

import se.fusion1013.cobaltKingdoms.quest.entity.QuestPlayerStatusEntity;
import se.fusion1013.cobaltKingdoms.quest.model.QuestPlayerStatus;

import java.util.List;

public class QuestPlayerStatusMapper {

    public static QuestPlayerStatus toModel(QuestPlayerStatusEntity entity) {
        return new QuestPlayerStatus(
                entity.getQuestId(),
                entity.getPlayerName(),
                entity.getStatus(),
                entity.getQuestType()
        );
    }

    public static QuestPlayerStatusEntity toEntity(QuestPlayerStatus model) {
        QuestPlayerStatusEntity entity = new QuestPlayerStatusEntity();
        entity.setQuestId(model.questId());
        entity.setPlayerName(model.playerName());
        entity.setStatus(model.status());
        entity.setQuestType(model.questType());
        return entity;
    }

    public static List<QuestPlayerStatus> toModels(List<QuestPlayerStatusEntity> entities) {
        return entities.stream().map(QuestPlayerStatusMapper::toModel).toList();
    }

}
