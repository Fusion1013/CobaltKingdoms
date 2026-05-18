package se.fusion1013.cobaltKingdoms.database.quest.bounty;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.database.system.DataStorageType;
import se.fusion1013.cobaltCore.database.system.implementations.SQLiteImplementation;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.database.quest.bounty.mapper.BountyPlayerStatusMapper;
import se.fusion1013.cobaltKingdoms.database.quest.bounty.mapper.BountyQuestMapper;
import se.fusion1013.cobaltKingdoms.database.quest.mapper.QuestMapper;
import se.fusion1013.cobaltKingdoms.quest.bounty.BountyPlayerStatus;
import se.fusion1013.cobaltKingdoms.quest.bounty.BountyQuest;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class BountyRepositoryImpl implements IBountyRepository {

    private static final Logger logger = CobaltKingdoms.getInstance().getLogger();
    private Dao<BountyQuestEntity, Long> bountyQuestDao;
    private Dao<BountyPlayerStatusEntity, Long> bountyPlayerStatusDao;

    @Override
    public void init() {
        try {
            ConnectionSource connectionSource = SQLiteImplementation.getConnectionSource();

            bountyQuestDao = DaoManager.createDao(connectionSource, BountyQuestEntity.class);
            bountyPlayerStatusDao = DaoManager.createDao(connectionSource, BountyPlayerStatusEntity.class);

            TableUtils.createTableIfNotExists(connectionSource, BountyQuestEntity.class);
            TableUtils.createTableIfNotExists(connectionSource, BountyPlayerStatusEntity.class);

        } catch (SQLException e) {
            logger.severe("Error initializing Bounty DAO: " + e.getMessage());
        }
    }

    @Override
    public BountyQuest getBountyByQuest(Long questId) {
        try {
            return BountyQuestMapper.toModel(bountyQuestDao.queryForEq("quest", questId).getFirst());
        } catch (SQLException e) {
            logger.severe("Error getting bounty: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void insertQuest(BountyQuest quest) {
        BountyQuestEntity entity = BountyQuestMapper.toEntity(quest);
        try {
            entity.setQuest(QuestMapper.toEntity(quest));
            bountyQuestDao.create(entity);
        } catch (SQLException e) {
            logger.severe("Error inserting bounty quest: " + e.getMessage());
        }
    }

    @Override
    public List<BountyQuest> getBounties(Player owner, PlayerProfile target) {
        QueryBuilder<BountyQuestEntity, Long> qb = bountyQuestDao.queryBuilder();
        try {
            qb.where()
                    .eq("owner_player_id", owner.getUniqueId())
                    .and()
                    .eq("target_player_id", target.getId());
            return BountyQuestMapper.toModels(qb.query());
        } catch (SQLException e) {
            logger.severe("Error getting bounty: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<BountyQuest> getBounties(UUID targetId) {
        try {
            return BountyQuestMapper.toModels(bountyQuestDao.queryForEq("target_player_id", targetId));
        } catch (SQLException e) {
            logger.severe("Error getting bounty: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void insertPlayerBountyStatus(BountyPlayerStatus bountyPlayerStatusEntity) {
        try {
            bountyPlayerStatusDao.createOrUpdate(BountyPlayerStatusMapper.toEntity(bountyPlayerStatusEntity));
        } catch (SQLException e) {
            logger.severe("Error inserting player bounty status: " + e.getMessage());
        }
    }

    @Override
    public BountyPlayerStatus getPlayerBountyStatus(Player player) {
        return getPlayerBountyStatus(player.getUniqueId(), player.getDisplayName());
    }

    @Override
    public BountyPlayerStatus getPlayerBountyStatus(UUID playerId, String playerName) {
        try {
            List<BountyPlayerStatusEntity> status = bountyPlayerStatusDao.queryForEq("player_id", playerId);
            if (status == null || status.isEmpty()) {
                BountyPlayerStatusEntity newStatus = new BountyPlayerStatusEntity();
                newStatus.setPlayerId(playerId);
                newStatus.setPlayerName(playerName);
                return BountyPlayerStatusMapper.toModel(newStatus);
            }
            return BountyPlayerStatusMapper.toModel(status.getFirst());
        } catch (SQLException e) {
            logger.severe("Error getting player bounty status: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<BountyQuest> getQuests() {
        try {
            return BountyQuestMapper.toModels(bountyQuestDao.queryForAll());
        } catch (SQLException e) {
            logger.severe("Error getting bounties: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public DataStorageType getDataStorageType() {
        return DataStorageType.SQLITE;
    }
}
