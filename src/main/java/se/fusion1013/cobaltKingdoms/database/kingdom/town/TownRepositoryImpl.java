package se.fusion1013.cobaltKingdoms.database.kingdom.town;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.database.system.DataStorageType;
import se.fusion1013.cobaltCore.database.system.implementations.SQLiteImplementation;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.database.kingdom.town.mapper.TownJailMapper;
import se.fusion1013.cobaltKingdoms.database.kingdom.town.mapper.TownMapper;
import se.fusion1013.cobaltKingdoms.database.kingdom.town.mapper.TownMemberMapper;
import se.fusion1013.cobaltKingdoms.kingdom.town.Town;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownJail;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownMember;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownMemberRole;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class TownRepositoryImpl implements ITownRepository {

    private static final Logger logger = CobaltKingdoms.getInstance().getLogger();

    private Dao<TownEntity, Long> townDao;
    private Dao<TownMemberEntity, Long> townMemberDao;
    private Dao<TownAppearanceEntity, Long> townAppearanceDao;
    private Dao<TownJailEntity, Long> townJailDao;

    @Override
    public void init() {
        try {
            ConnectionSource connectionSource = SQLiteImplementation.getConnectionSource();

            townAppearanceDao = DaoManager.createDao(connectionSource, TownAppearanceEntity.class);
            townDao = DaoManager.createDao(connectionSource, TownEntity.class);
            townMemberDao = DaoManager.createDao(connectionSource, TownMemberEntity.class);
            townJailDao = DaoManager.createDao(connectionSource, TownJailEntity.class);

            TableUtils.createTableIfNotExists(connectionSource, TownAppearanceEntity.class);
            TableUtils.createTableIfNotExists(connectionSource, TownEntity.class);
            TableUtils.createTableIfNotExists(connectionSource, TownMemberEntity.class);
            TableUtils.createTableIfNotExists(connectionSource, TownJailEntity.class);

        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error initializing Town DAO: " + e.getMessage());
        }
    }

    @Override
    public void updateTown(Town town) {
        TownEntity townEntity = TownMapper.toEntity(town);
        try {
            townDao.update(townEntity);
            townAppearanceDao.createOrUpdate(townEntity.getAppearance());
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error updating town: " + e.getMessage());
        }
    }

    @Override
    public void createTown(Player owner, Town town) {
        TownEntity townEntity = TownMapper.toEntity(town);

        try {
            townDao.createOrUpdate(townEntity);

            // Insert jails if there are any
            if (town.getJails() != null) {
                for (TownJail jail : town.getJails()) {
                    TownJailEntity jailEntity = TownJailMapper.toEntity(jail);
                    jailEntity.setTown(townEntity);
                    townJailDao.createOrUpdate(jailEntity);
                }
            }

            // Insert town members if there are any
            if (town.getTownMembers() != null) {
                for (TownMember townMember : town.getTownMembers()) {
                    TownMemberEntity townMemberEntity = TownMemberMapper.toEntity(townMember);
                    townMemberEntity.setTown(townEntity);
                    townMemberDao.createOrUpdate(townMemberEntity);
                }
            }

        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error inserting town: " + e.getMessage());
        }
    }

    @Override
    public List<Town> getTowns() {
        try {
            return TownMapper.toModels(townDao.queryForAll());

        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error getting towns from database: " + e.getMessage());
        }

        return List.of();
    }

    @Override
    public void deleteTown(Long townId) {
        try {
            List<TownMemberEntity> townMemberList = townMemberDao.queryForAll().stream().filter(tm -> tm.getTown().getId().equals(townId)).toList();
            for (TownMemberEntity townMemberEntity : townMemberList) {
                townMemberDao.deleteById(townMemberEntity.getId());
            }
            townDao.deleteById(townId);
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error deleting town: " + e.getMessage());
        }
    }

    @Override
    public Town getTownByOwner(UUID ownerUuid) {
        try {
            List<TownEntity> results = townDao.queryForEq("owner_id", ownerUuid.toString());

            if (results.isEmpty()) {
                return null;
            }

            return TownMapper.toModel(results.getFirst());

        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error fetching town by owner: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Town getTownByName(String townName) {
        try {
            List<TownEntity> results = townDao.queryForEq("name", townName);

            if (results.isEmpty()) {
                return null;
            }

            return TownMapper.toModel(results.getFirst());

        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error fetching town by name: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Town getTown(Long id) {
        try {
            return TownMapper.toModel(townDao.queryForId(id));
        } catch (SQLException e) {
            logger.severe("Error getting town: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void increaseTownXp(Long townId, int xpValue) {
        try {
            TownEntity townEntity = townDao.queryForId(townId);
            townEntity.setExperience(townEntity.getExperience() + xpValue);
            townDao.update(townEntity);
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error updating town experience: " + e.getMessage());
        }
    }

    @Override
    public List<Town> getTownsWithMember(@NotNull UUID playerId) {
        try {
            QueryBuilder<TownMemberEntity, Long> memberQb = townMemberDao.queryBuilder();
            memberQb.where().eq("player_id", playerId);

            QueryBuilder<TownEntity, Long> townQb = townDao.queryBuilder();

            townQb.join(memberQb);

            return TownMapper.toModels(townQb.query());
        } catch (SQLException e) {
            logger.severe("Error getting towns with member: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<TownMember> getTownMember(@NotNull UUID playerId) {
        try {
            return TownMemberMapper.toModels(townMemberDao.queryForEq("player_id", playerId));
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error getting town member: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void addTownMember(Long townId, Player invitePlayer) {
        try {
            TownEntity town = townDao.queryForId(townId);
            if (town == null) return;

            // Remove any existing player town memberships
            List<TownMemberEntity> playerMemberEntities = townMemberDao.queryForEq("player_id", invitePlayer.getUniqueId());
            for (TownMemberEntity playerMemberEntity : playerMemberEntities) {
                townMemberDao.deleteById(playerMemberEntity.getId());
            }

            TownMemberEntity townMemberEntity = new TownMemberEntity();
            townMemberEntity.setPlayerId(invitePlayer.getUniqueId());
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
        List<TownMember> townMembers = getTownMember(kickPlayer.getUniqueId());
        if (townMembers == null || townMembers.isEmpty()) return;

        try {
            for (TownMember townMember : townMembers) {
                townMemberDao.deleteById(townMember.getId());
            }
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Failed to remove player member: " + e.getMessage());
        }
    }

    @Override
    public List<TownMember> getTownMembersByTownId(Long townId) {
        try {
            return TownMemberMapper.toModels(townMemberDao.queryForEq("town", townId));
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Failed to get town members: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public void createJail(TownJail jail) {
        TownJailEntity jailEntity = TownJailMapper.toEntity(jail);
        try {
            townJailDao.create(jailEntity);
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Failed to create jail: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteJail(Long townId, String jailName) {
        try {
            List<TownJail> townJails = getJails(townId).stream().filter(t -> t.getName().equalsIgnoreCase(jailName)).toList();
            for (TownJail townJail : townJails) {
                townJailDao.deleteById(townJail.getId());
            }
            return true;
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Failed to delete jail: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<TownJail> getJails(Long townId) {
        try {
            return TownJailMapper.toModels(townJailDao.queryForEq("town", townId));
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Failed to get jails: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public Optional<TownJail> getJailByName(Long townId, String jailName) {
        return getJails(townId).stream().filter(tj -> tj.getName().equalsIgnoreCase(jailName)).findFirst();
    }

    @Override
    public DataStorageType getDataStorageType() {
        return DataStorageType.SQLITE;
    }
}
