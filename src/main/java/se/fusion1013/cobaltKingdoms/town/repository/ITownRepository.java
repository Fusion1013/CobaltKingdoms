package se.fusion1013.cobaltKingdoms.town.repository;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.database.system.IDao;
import se.fusion1013.cobaltKingdoms.town.entity.TownEntity;
import se.fusion1013.cobaltKingdoms.town.model.Town;
import se.fusion1013.cobaltKingdoms.town.model.TownJail;
import se.fusion1013.cobaltKingdoms.town.model.TownMember;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITownRepository extends IDao {

    void updateTown(Town townData);

    void createTown(Player owner, Town town);

    List<Town> getTowns();

    void deleteTown(Long id);

    @Override
    default String getId() {
        return "town";
    }

    Town getTownByOwner(UUID ownerUuid);

    Town getTownByName(String townName);

    Town getTown(Long id);

    TownEntity getTownEntity(Long id);

    void increaseTownXp(Long townId, int xpValue);

    List<Town> getTownsWithMember(@NotNull UUID playerId);

    List<TownMember> getTownMember(@NotNull UUID playerId);

    void addTownMember(Long uuid, Player invitePlayer);

    void removePlayerMember(Player kickPlayer);

    List<TownMember> getTownMembersByTownId(Long townId);

    void createJail(TownJail jail);

    boolean deleteJail(Long townId, String jailName);

    List<TownJail> getJails(Long townId);

    Optional<TownJail> getJailByName(Long townId, String jailName);
}
