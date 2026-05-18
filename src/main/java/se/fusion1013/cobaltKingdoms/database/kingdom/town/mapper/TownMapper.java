package se.fusion1013.cobaltKingdoms.database.kingdom.town.mapper;

import se.fusion1013.cobaltKingdoms.database.kingdom.town.TownEntity;
import se.fusion1013.cobaltKingdoms.kingdom.town.Town;

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

        if (entity.getJails() != null) {
//            model.setJails(entity.getJails().stream().map(TownJailMapper::toModel).toList());
        }

        if (entity.getTownMembers() != null) {
//            model.setTownMembers(entity.getTownMembers().stream().map(TownMemberMapper::toModel).toList());
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
