package se.fusion1013.cobaltKingdoms.town.mapper;

import se.fusion1013.cobaltKingdoms.town.entity.TownEntity;
import se.fusion1013.cobaltKingdoms.town.model.Town;

import java.util.List;

public class TownMapper {

    public static Town toModel(TownEntity entity) {
        if (entity == null) return null;

        Town model = new Town(entity.getId());

        model.setOwnerId(entity.getOwnerId());
        model.setName(entity.getName());
        model.setDisplayName(entity.getDisplayName());
        model.setKingdomId(entity.getKingdomId());
        model.setLocation(entity.getLocation());
        model.setExperience(entity.getExperience());

        if (entity.getAppearance() != null) {
            model.setAppearance(TownAppearanceMapper.toModel(entity.getAppearance()));
        }

        return model;
    }

    public static TownEntity toEntity(Town model) {
        if (model == null) return null;

        TownEntity entity = new TownEntity();

        if (model.getId() != null) entity.setId(model.getId());
        entity.setOwnerId(model.getOwnerId());
        entity.setName(model.getName());
        entity.setDisplayName(model.getDisplayName());
        entity.setKingdomId(model.getKingdomId());
        entity.setLocation(model.getLocation());
        entity.setExperience(model.getExperience());

        entity.setAppearance(TownAppearanceMapper.toEntity(model.getAppearance()));

        return entity;
    }

    public static List<Town> toModels(List<TownEntity> entities) {
        return entities.stream().map(TownMapper::toModel).toList();
    }
}
