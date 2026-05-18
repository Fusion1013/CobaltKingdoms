package se.fusion1013.cobaltKingdoms.database.kingdom.town.mapper;

import se.fusion1013.cobaltKingdoms.database.kingdom.town.TownJailEntity;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownJail;

import java.util.List;

public class TownJailMapper {

    public static TownJail toModel(TownJailEntity entity) {
        if (entity == null) return null;

        TownJail model = new TownJail(entity.getId());

        model.setName(entity.getName());
        model.setLocation(entity.getLocation());

        return model;
    }

    public static TownJailEntity toEntity(TownJail model) {
        if (model == null) return null;

        TownJailEntity entity = new TownJailEntity();

        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setLocation(model.getLocation());

        return entity;
    }

    public static List<TownJail> toModels(List<TownJailEntity> entities) {
        return entities.stream().map(TownJailMapper::toModel).toList();
    }
}
