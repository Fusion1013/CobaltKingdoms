package se.fusion1013.cobaltKingdoms.database.quest;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.database.system.DataStorageType;
import se.fusion1013.cobaltCore.database.system.implementations.SQLiteImplementation;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownEntity;
import se.fusion1013.cobaltKingdoms.quest.*;
import se.fusion1013.cobaltKingdoms.quest.item_delivery.ItemDeliveryQuestEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class QuestRepositoryImpl implements IQuestRepository {

    private Dao<QuestEntity, Long> questDao;
    private Dao<ItemDeliveryQuestEntity, Long> itemDeliveryQuestDao;
    private Dao<ActivePlayerQuestEntity, Long> activePlayerQuestDao;

    @Override
    public void init() {
        try {
            ConnectionSource connectionSource = SQLiteImplementation.getConnectionSource();

            questDao = DaoManager.createDao(connectionSource, QuestEntity.class);
            itemDeliveryQuestDao = DaoManager.createDao(connectionSource, ItemDeliveryQuestEntity.class);
            activePlayerQuestDao = DaoManager.createDao(connectionSource, ActivePlayerQuestEntity.class);

            TableUtils.createTableIfNotExists(connectionSource, QuestEntity.class);
            TableUtils.createTableIfNotExists(connectionSource, ItemDeliveryQuestEntity.class);
            TableUtils.createTableIfNotExists(connectionSource, ActivePlayerQuestEntity.class);

        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error initializing Town DAO: " + e.getMessage());
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
                    return itemDeliveryQuestDao.queryForId(questId);
                }
                case Collect -> {
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
    public DataStorageType getDataStorageType() {
        return DataStorageType.SQLITE;
    }
}
