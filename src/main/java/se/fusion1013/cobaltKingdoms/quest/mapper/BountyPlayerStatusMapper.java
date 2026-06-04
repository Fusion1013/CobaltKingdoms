package se.fusion1013.cobaltKingdoms.quest.mapper;

import se.fusion1013.cobaltKingdoms.quest.entity.BountyPlayerStatusEntity;
import se.fusion1013.cobaltKingdoms.quest.model.BountyPlayerStatus;

import java.util.List;

public class BountyPlayerStatusMapper {

    public static BountyPlayerStatus toModel(BountyPlayerStatusEntity entity) {
        BountyPlayerStatus model = new BountyPlayerStatus();

        model.setId(entity.getId());
        model.setPlayerId(entity.getPlayerId());
        model.setPlayerName(entity.getPlayerName());
        model.setCompleted(entity.getCompleted());
        model.setFailed(entity.getFailed());
        model.setEvaded(entity.getEvaded());
        model.setKilled(entity.getKilled());
        model.setBountiesEnabled(entity.isBountiesEnabled());
        model.setRating(entity.getRating());
        model.setUpdateTimestamp(entity.getUpdateTimestamp());

        return model;
    }

    public static BountyPlayerStatusEntity toEntity(BountyPlayerStatus model) {
        BountyPlayerStatusEntity entity = new BountyPlayerStatusEntity();

        entity.setId(model.getId());
        entity.setPlayerId(model.getPlayerId());
        entity.setPlayerName(model.getPlayerName());
        entity.setCompleted(model.getCompleted());
        entity.setFailed(model.getFailed());
        entity.setEvaded(model.getEvaded());
        entity.setKilled(model.getKilled());
        entity.setBountiesEnabled(model.isBountiesEnabled());
        entity.setRating(model.getRating());
        entity.setUpdateTimestamp(model.getUpdateTimestamp());

        return entity;
    }

    public static List<BountyPlayerStatus> toModels(List<BountyPlayerStatusEntity> entities) {
        return entities.stream().map(BountyPlayerStatusMapper::toModel).toList();
    }

}
