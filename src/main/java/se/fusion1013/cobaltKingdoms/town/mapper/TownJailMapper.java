package se.fusion1013.cobaltKingdoms.town.mapper;

import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltKingdoms.town.entity.TownEntity;
import se.fusion1013.cobaltKingdoms.town.entity.TownJailEntity;
import se.fusion1013.cobaltKingdoms.town.model.TownJail;
import se.fusion1013.cobaltKingdoms.town.repository.ITownRepository;

import java.util.List;

public class TownJailMapper {

    public static TownJail toModel(TownJailEntity entity) {
        if (entity == null) return null;

        TownJail model = new TownJail(entity.getId());

        model.setName(entity.getName());
        model.setLocation(entity.getLocation());
        if (entity.getTown() != null) {
            model.setTownId(entity.getTown().getId());
        }

        return model;
    }

    public static TownJailEntity toEntity(TownJail model) {
        if (model == null) return null;

        TownJailEntity entity = new TownJailEntity();

        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setLocation(model.getLocation());

        if (model.getTownId() != null) {
            TownEntity town = DataManager.getInstance().getDao(ITownRepository.class).getTownEntity(model.getTownId());
            entity.setTown(town);
        }

        return entity;
    }

    public static List<TownJail> toModels(List<TownJailEntity> entities) {
        return entities.stream().map(TownJailMapper::toModel).toList();
    }
}
