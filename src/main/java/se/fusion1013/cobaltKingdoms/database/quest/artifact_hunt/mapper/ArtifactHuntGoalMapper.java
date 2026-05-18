package se.fusion1013.cobaltKingdoms.database.quest.artifact_hunt.mapper;

import se.fusion1013.cobaltKingdoms.database.quest.artifact_hunt.ArtifactHuntQuestGoalEntity;
import se.fusion1013.cobaltKingdoms.quest.artifact_hunt.ArtifactHuntGoal;

import java.util.List;

public final class ArtifactHuntGoalMapper {

    public static ArtifactHuntGoal toModel(ArtifactHuntQuestGoalEntity entity) {
        ArtifactHuntGoal model = new ArtifactHuntGoal(entity.getId());

        model.setName(entity.getName());
        model.setDifficulty(entity.getDifficulty());
        model.setLocation(entity.getLocation());
        model.setItemName(entity.getItemName());
        model.setDescription(entity.getDescription());

        return model;
    }

    public static ArtifactHuntQuestGoalEntity toEntity(ArtifactHuntGoal model) {
        ArtifactHuntQuestGoalEntity entity = new ArtifactHuntQuestGoalEntity(model.getId());

        entity.setName(model.getName());
        entity.setDifficulty(model.getDifficulty());
        entity.setLocation(model.getLocation());
        entity.setItemName(model.getItemName());
        entity.setDescription(model.getDescription());

        return entity;
    }

    public static List<ArtifactHuntGoal> toModels(List<ArtifactHuntQuestGoalEntity> entities) {
        return entities.stream().map(ArtifactHuntGoalMapper::toModel).toList();
    }

}
