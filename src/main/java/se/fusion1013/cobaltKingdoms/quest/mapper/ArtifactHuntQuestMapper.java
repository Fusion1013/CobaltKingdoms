package se.fusion1013.cobaltKingdoms.quest.mapper;

import se.fusion1013.cobaltKingdoms.quest.entity.ArtifactHuntEntity;
import se.fusion1013.cobaltKingdoms.quest.model.ArtifactHuntQuest;

import java.util.List;

public class ArtifactHuntQuestMapper {

    public static ArtifactHuntQuest toModel(ArtifactHuntEntity entity) {
        ArtifactHuntQuest model = new ArtifactHuntQuest(entity.getId());
        QuestMapper.toModel(entity.getQuest(), model);

        model.setRewards(entity.getRewards());

        if (entity.getGoal() != null) {
            model.setGoal(ArtifactHuntGoalMapper.toModel(entity.getGoal()));
        }

        return model;
    }

    public static ArtifactHuntEntity toEntity(ArtifactHuntQuest model) {
        ArtifactHuntEntity entity = new ArtifactHuntEntity();
        if (model.getId() != null) entity.setId(model.getId());
        entity.setRewards(model.getRewards());
        return entity;
    }

    public static List<ArtifactHuntQuest> toModels(List<ArtifactHuntEntity> entities) {
        return entities.stream().map(ArtifactHuntQuestMapper::toModel).toList();
    }
}
