package se.fusion1013.cobaltKingdoms.database.quest;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.database.system.DataStorageType;
import se.fusion1013.cobaltCore.database.system.implementations.SQLiteImplementation;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.database.quest.gather.IQuestGatherRepository;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownEntity;
import se.fusion1013.cobaltKingdoms.quest.*;
import se.fusion1013.cobaltKingdoms.quest.bounty.BountyQuestEntity;
import se.fusion1013.cobaltKingdoms.quest.item_delivery.ItemDeliveryQuestEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// TODO: Split into different repositories
public class QuestRepositoryImpl implements IQuestRepository {

    private Dao<QuestEntity, Long> questDao;
    private Dao<ItemDeliveryQuestEntity, Long> itemDeliveryQuestDao;
    private Dao<ActivePlayerQuestEntity, Long> activePlayerQuestDao;
    private Dao<BountyQuestEntity, Long> bountyQuestDao;

    @Override
    public void init() {
        try {
            ConnectionSource connectionSource = SQLiteImplementation.getConnectionSource();

            questDao = DaoManager.createDao(connectionSource, QuestEntity.class);
            itemDeliveryQuestDao = DaoManager.createDao(connectionSource, ItemDeliveryQuestEntity.class);
            activePlayerQuestDao = DaoManager.createDao(connectionSource, ActivePlayerQuestEntity.class);
            bountyQuestDao = DaoManager.createDao(connectionSource, BountyQuestEntity.class);

            TableUtils.createTableIfNotExists(connectionSource, QuestEntity.class);
            TableUtils.createTableIfNotExists(connectionSource, ItemDeliveryQuestEntity.class);
            TableUtils.createTableIfNotExists(connectionSource, ActivePlayerQuestEntity.class);
            TableUtils.createTableIfNotExists(connectionSource, BountyQuestEntity.class);

        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error initializing Quest DAO: " + e.getMessage());
        }
    }

    @Override
    public List<QuestEntity> getQuests() {
        try {
            return questDao.queryForAll();
        } catch (SQLException ex) {
            CobaltKingdoms.getInstance().getLogger().severe("Error getting quests: " + ex.getMessage());
        }
        return List.of();
    }

    @Override
    public List<QuestEntity> getQuests(TownEntity town) {
        try {
            return questDao.queryForEq("start_town", town);
        } catch (SQLException ex) {
            CobaltKingdoms.getInstance().getLogger().severe("Error fetching quest: " + ex.getMessage());
            return null;
        }
    }

    @Override
    public void insertQuest(ItemDeliveryQuestEntity quest) {
        try {
            itemDeliveryQuestDao.create(quest);
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error inserting quest: " + e.getMessage());
        }
    }

    @Override
    public QuestEntity getQuest(Long questId) {
        try {
            return questDao.queryForId(questId);
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error getting quest: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void updateStatus(Long id, QuestStatus questStatus) {
        try {
            QuestEntity questEntity = questDao.queryForId(id);
            questEntity.setStatus(questStatus);
            questDao.update(questEntity);
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error getting quest: " + e.getMessage());
        }
    }

    @Override
    public IQuestData getQuestData(Long questId, QuestType questType) {
        try {
            switch (questType) {
                case Combat -> {
                }
                case Deliver -> {
                    return itemDeliveryQuestDao.queryForEq("quest", questId).getFirst();
                }
                case Gather -> {
                    return DataManager.getInstance().getDao(IQuestGatherRepository.class).getQuest(questId);
                }
                case Bounty -> {
                    return bountyQuestDao.queryForEq("quest", questId).getFirst();
                }
            }
        } catch (SQLException ex) {
            CobaltKingdoms.getInstance().getLogger().severe("Error getting quest data: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public void insertActiveQuest(ActivePlayerQuestEntity activeQuest) {
        try {
            activePlayerQuestDao.create(activeQuest);
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error inserting active quest: " + e.getMessage());
        }
    }

    @Override
    public Optional<ActivePlayerQuestEntity> getActivePlayerQuestByQuestId(Long questId) {
        try {
            return Optional.of(activePlayerQuestDao.queryForEq("quest_id", questId).getFirst());
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error getting active player quest: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void removeActivePlayerQuestById(Long id) {
        try {
            activePlayerQuestDao.deleteById(id);
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error deleting active player quest: " + e.getMessage());
        }
    }

    @Override
    public Optional<List<ActivePlayerQuestEntity>> getActivePlayerQuestsByPlayer(Player player) {
        try {
            return Optional.of(activePlayerQuestDao.queryForEq("playerUUID", player.getUniqueId()));
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error getting active player quest: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<ActivePlayerQuestEntity> getActiveQuests() {
        try {
            return activePlayerQuestDao.queryForAll();
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error getting active player quests: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public void insertQuest(BountyQuestEntity quest) {
        try {
            bountyQuestDao.create(quest);
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error inserting bounty quest: " + e.getMessage());
        }
    }

    @Override
    public List<BountyQuestEntity> getBounties(Player owner, PlayerProfile target) {
        QueryBuilder<BountyQuestEntity, Long> qb = bountyQuestDao.queryBuilder();
        try {
            qb.where()
                    .eq("owner_player_id", owner.getUniqueId())
                    .and()
                    .eq("target_player_id", target.getId());
            return qb.query();
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error getting bounty: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<BountyQuestEntity> getBounties(UUID targetId) {
        try {
            return bountyQuestDao.queryForEq("target_player_id", targetId);
        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().info("Error getting bounty: " + e.getMessage());
            return null;
        }
    }

    @Override
    public DataStorageType getDataStorageType() {
        return DataStorageType.SQLITE;
    }
}
