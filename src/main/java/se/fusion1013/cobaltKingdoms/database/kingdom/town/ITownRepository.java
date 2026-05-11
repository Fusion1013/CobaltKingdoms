package se.fusion1013.cobaltKingdoms.database.kingdom.town;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.database.system.IDao;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownEntity;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownJailEntity;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownMemberEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITownRepository extends IDao {

    void updateTown(TownEntity townData);

    void createTown(Player owner, TownEntity townEntity);

    List<TownEntity> getTowns();

    void deleteTown(Long id);

    @Override
    default String getId() {
        return "town";
    }

    TownEntity getTownByOwner(UUID ownerUuid);

    TownEntity getTownByName(String townName);

    void increaseTownXp(Long townId, int xpValue);

    List<TownMemberEntity> getTownMember(@NotNull UUID playerId);

    void addTownMember(Long uuid, Player invitePlayer);

    void removePlayerMember(Player kickPlayer);

    List<TownMemberEntity> getTownMembersByTownId(Long townId);

    void createJail(TownJailEntity jail);

    boolean deleteJail(Long townId, String jailName);

    List<TownJailEntity> getJails(Long townId);

    Optional<TownJailEntity> getJailByName(Long townId, String jailName);
}
