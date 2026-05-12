package se.fusion1013.cobaltKingdoms.database.quest.gather;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.slf4j.LoggerFactory;
import se.fusion1013.cobaltCore.database.system.DataStorageType;
import se.fusion1013.cobaltCore.database.system.implementations.SQLiteImplementation;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.quest.item_gather.GatherQuestEntity;
import se.fusion1013.cobaltKingdoms.quest.item_gather.GatherQuestGoalEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class QuestGatherRepository implements IQuestGatherRepository {

    private static final Logger logger = CobaltKingdoms.getInstance().getLogger();
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(QuestGatherRepository.class);

    private Dao<GatherQuestGoalEntity, Long> gatherGoalDao;
    private Dao<GatherQuestEntity, Long> gatherQuestDao;

    @Override
    public void init() {
        try {
            ConnectionSource connectionSource = SQLiteImplementation.getConnectionSource();

            gatherGoalDao = DaoManager.createDao(connectionSource, GatherQuestGoalEntity.class);
            gatherQuestDao = DaoManager.createDao(connectionSource, GatherQuestEntity.class);

            TableUtils.createTableIfNotExists(connectionSource, GatherQuestGoalEntity.class);
            TableUtils.createTableIfNotExists(connectionSource, GatherQuestEntity.class);

        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error initializing Gather Quest DAO: " + e.getMessage());
        }
    }

    @Override
    public DataStorageType getDataStorageType() {
        return DataStorageType.SQLITE;
    }

    @Override
    public void createGoal(GatherQuestGoalEntity goal) {
        try {
            gatherGoalDao.create(goal);
        } catch (SQLException e) {
            logger.severe("Failed to create new goal: " + e.getMessage());
        }
    }

    @Override
    public List<GatherQuestGoalEntity> getGoals() {
        try {
            return gatherGoalDao.queryForAll();
        } catch (SQLException e) {
            logger.severe("Failed to get goals: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<GatherQuestGoalEntity> getGoals(int difficulty) {
        try {
            return gatherGoalDao.queryForEq("difficulty", difficulty);
        } catch (SQLException e) {
            logger.severe("Failed to get goals: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public GatherQuestGoalEntity getGoal(Long id) {
        try {
            return gatherGoalDao.queryForId(id);
        } catch (SQLException e) {
            logger.severe("Failed to get goal: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void insertQuest(GatherQuestEntity quest) {
        try {
            gatherQuestDao.create(quest);
        } catch (SQLException e) {
            logger.severe("Failed to create gather quest: " + e.getMessage());
        }
    }

    @Override
    public GatherQuestEntity getQuest(Long questId) {
        try {
            return gatherQuestDao.queryForEq("quest_id", questId).getFirst();
        } catch (SQLException e) {
            logger.severe("Failed to get quest: " + e.getMessage());
            return null;
        }
    }
}
