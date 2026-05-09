package se.fusion1013.cobaltKingdoms.database.kingdom.town;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.database.system.DataStorageType;
import se.fusion1013.cobaltCore.database.system.implementations.SQLiteImplementation;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownEntity;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownMemberEntity;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownMemberRole;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class TownRepositoryImpl implements ITownRepository {

    private Dao<TownEntity, String> townDao;
    private Dao<TownMemberEntity, Long> townMemberDao;

    @Override
    public void init() {
        try {
            ConnectionSource connectionSource = SQLiteImplementation.getConnectionSource();

            townDao = DaoManager.createDao(connectionSource, TownEntity.class);
            townMemberDao = DaoManager.createDao(connectionSource, TownMemberEntity.class);

            TableUtils.createTableIfNotExists(connectionSource, TownEntity.class);
            TableUtils.createTableIfNotExists(connectionSource, TownMemberEntity.class);

        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error initializing Town DAO: " + e.getMessage());
        }
    }

    @Override
    public void updateTown(TownEntity townData) {
        try {
            townDao.update(townData);
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error updating town: " + e.getMessage());
        }
    }

    @Override
    public void createTown(Player owner, TownEntity townEntity) {
        try {
            townDao.createOrUpdate(townEntity);

            TownMemberEntity townMemberEntity = new TownMemberEntity();
            townMemberEntity.setPlayerUuid(owner.getUniqueId());
            townMemberEntity.setPlayerName(owner.getName());
            townMemberEntity.setTown(townEntity);
            townMemberEntity.setRole(TownMemberRole.OWNER);
            townMemberDao.createOrUpdate(townMemberEntity);
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error inserting town: " + e.getMessage());
        }
    }

    @Override
    public List<TownEntity> getTowns() {
        try {
            return townDao.queryForAll();

        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error getting towns from database: " + e.getMessage());
        }

        return List.of();
    }

    @Override
    public void deleteTown(UUID id) {
        try {
            List<TownMemberEntity> townMemberList = townMemberDao.queryForAll().stream().filter(tm -> tm.getTown().getUuid().equals(id)).toList();
            for (TownMemberEntity townMemberEntity : townMemberList) {
                townMemberDao.deleteById(townMemberEntity.getId());
            }
            townDao.deleteById(id.toString());
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error deleting town: " + e.getMessage());
        }
    }

    @Override
    public TownEntity getTownByOwner(UUID ownerUuid) {
        try {
            List<TownEntity> results = townDao.queryForEq("owner_id", ownerUuid.toString());

            if (results.isEmpty()) {
                return null;
            }

            return results.getFirst();

        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error fetching town by owner: " + e.getMessage());
            return null;
        }
    }

    @Override
    public TownEntity getTownByName(String townName) {
        try {
            List<TownEntity> results = townDao.queryForEq("name", townName);

            if (results.isEmpty()) {
                return null;
            }

            return results.getFirst();

        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error fetching town by owner: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void increaseTownXp(UUID uuid, int xpValue) {
        try {
            TownEntity townEntity = townDao.queryForId(uuid.toString());
            townEntity.setExperience(townEntity.getExperience() + xpValue);
            townDao.update(townEntity);
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error updating town experience: " + e.getMessage());
        }
    }

    @Override
    public TownMemberEntity getTownMember(@NotNull UUID playerId) {
        try {
            return townMemberDao.queryForEq("player_uuid", playerId).getFirst();
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error getting town member: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void addTownMember(UUID townId, Player invitePlayer) {
        try {
            TownEntity town = townDao.queryForId(townId.toString());
            if (town == null) return;

            // Remove any existing player town memberships
            List<TownMemberEntity> playerMemberEntities = townMemberDao.queryForEq("player_uuid", invitePlayer.getUniqueId());
            for (TownMemberEntity playerMemberEntity : playerMemberEntities) {
                townMemberDao.deleteById(playerMemberEntity.getId());
            }

            TownMemberEntity townMemberEntity = new TownMemberEntity();
            townMemberEntity.setPlayerUuid(invitePlayer.getUniqueId());
            townMemberEntity.setPlayerName(invitePlayer.getName());
            townMemberEntity.setTown(town);
            townMemberEntity.setRole(TownMemberRole.MEMBER);
            townMemberDao.createOrUpdate(townMemberEntity);

        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Failed to add town member: " + e.getMessage());
        }
    }

    @Override
    public void removePlayerMember(Player kickPlayer) {
        TownMemberEntity townMember = getTownMember(kickPlayer.getUniqueId());
        if (townMember == null) return;

        try {
            townMemberDao.deleteById(townMember.getId());
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Failed to remove player member: " + e.getMessage());
        }
    }

    @Override
    public List<TownMemberEntity> getTownMembersByTownId(UUID townId) {
        try {
            return townMemberDao.queryForEq("town", townId);
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Failed to get town members: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public DataStorageType getDataStorageType() {
        return DataStorageType.SQLITE;
    }
}
