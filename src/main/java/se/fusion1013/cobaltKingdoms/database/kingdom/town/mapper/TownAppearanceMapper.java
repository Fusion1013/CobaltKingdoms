package se.fusion1013.cobaltKingdoms.database.kingdom.town.mapper;

import se.fusion1013.cobaltKingdoms.database.kingdom.town.TownAppearanceEntity;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownAppearance;

public class TownAppearanceMapper {

    public static TownAppearance toModel(TownAppearanceEntity entity) {
        if (entity == null) return null;

        TownAppearance model = new TownAppearance(entity.getId());

        model.setSkin(entity.getSkin());
        model.setTexture(entity.getTexture());
        model.setTitleGreeting(entity.getTitleGreeting());
        model.setChatGreeting(entity.getChatGreeting());

        return model;
    }

    public static TownAppearanceEntity toEntity(TownAppearance model) {
        if (model == null) return null;

        TownAppearanceEntity entity = new TownAppearanceEntity();

        entity.setId(model.getId());
        entity.setSkin(model.getSkin());
        entity.setTexture(model.getTexture());
        entity.setTitleGreeting(model.getTitleGreeting());
        entity.setChatGreeting(model.getChatGreeting());

        return entity;
    }

}
