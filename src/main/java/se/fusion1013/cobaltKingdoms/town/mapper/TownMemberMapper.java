package se.fusion1013.cobaltKingdoms.town.mapper;

import se.fusion1013.cobaltKingdoms.town.entity.TownMemberEntity;
import se.fusion1013.cobaltKingdoms.town.model.TownMember;

import java.util.List;

public final class TownMemberMapper {

    public static TownMember toModel(TownMemberEntity entity) {
        if (entity == null) return null;

        TownMember model = new TownMember(entity.getId());

        model.setPlayerId(entity.getPlayerId());
        model.setPlayerName(entity.getPlayerName());
        model.setRole(entity.getRole());
        model.setTownId(entity.getTown().getId());

        return model;
    }

    public static TownMemberEntity toEntity(TownMember model) {
        if (model == null) return null;

        TownMemberEntity entity = new TownMemberEntity();

        entity.setId(model.getId());
        entity.setPlayerId(model.getPlayerId());
        entity.setPlayerName(model.getPlayerName());
        entity.setRole(model.getRole());

        return entity;
    }

    public static List<TownMember> toModels(List<TownMemberEntity> entities) {
        return entities.stream().map(TownMemberMapper::toModel).toList();
    }
}
