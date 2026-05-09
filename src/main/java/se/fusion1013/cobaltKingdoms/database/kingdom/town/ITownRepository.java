package se.fusion1013.cobaltKingdoms.database.kingdom.town;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.database.system.IDao;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownEntity;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownMemberEntity;

import java.util.List;
import java.util.UUID;

public interface ITownRepository extends IDao {

    void updateTown(TownEntity townData);

    void createTown(Player owner, TownEntity townEntity);

    List<TownEntity> getTowns();

    void deleteTown(UUID id);

    @Override
    default String getId() {
        return "town";
    }

    TownEntity getTownByOwner(UUID ownerUuid);

    TownEntity getTownByName(String townName);

    void increaseTownXp(UUID uuid, int xpValue);

    TownMemberEntity getTownMember(@NotNull UUID uniqueId);

    void addTownMember(UUID uuid, Player invitePlayer);

    void removePlayerMember(Player kickPlayer);

    List<TownMemberEntity> getTownMembersByTownId(UUID townId);
}
