package se.fusion1013.cobaltKingdoms.database.quest.artifact_hunt;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.slf4j.LoggerFactory;
import se.fusion1013.cobaltCore.database.system.DataStorageType;
import se.fusion1013.cobaltCore.database.system.implementations.SQLiteImplementation;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.quest.artifact_hunt.ArtifactHuntEntity;
import se.fusion1013.cobaltKingdoms.quest.artifact_hunt.ArtifactHuntQuestGoalEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class QuestArtifactHuntRepository implements IQuestArtifactHuntRepository {

    private static final Logger logger = CobaltKingdoms.getInstance().getLogger();
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(QuestArtifactHuntRepository.class);

    private Dao<ArtifactHuntQuestGoalEntity, Long> artifactHuntGoalDao;
    private Dao<ArtifactHuntEntity, Long> artifactHuntQuestDao;

    @Override
    public void init() {
        try {
            ConnectionSource connectionSource = SQLiteImplementation.getConnectionSource();

            artifactHuntGoalDao = DaoManager.createDao(connectionSource, ArtifactHuntQuestGoalEntity.class);
            artifactHuntQuestDao = DaoManager.createDao(connectionSource, ArtifactHuntEntity.class);

            TableUtils.createTableIfNotExists(connectionSource, ArtifactHuntQuestGoalEntity.class);
            TableUtils.createTableIfNotExists(connectionSource, ArtifactHuntEntity.class);

        } catch (SQLException e) {
            CobaltKingdoms.getInstance().getLogger().severe("Error initializing Artifact Hunt Quest DAO: " + e.getMessage());
        }
    }

    @Override
    public DataStorageType getDataStorageType() {
        return DataStorageType.SQLITE;
    }

    @Override
    public void createGoal(ArtifactHuntQuestGoalEntity goal) {
        try {
            artifactHuntGoalDao.create(goal);
        } catch (SQLException e) {
            logger.severe("Failed to create new goal: " + e.getMessage());
        }
    }

    @Override
    public List<ArtifactHuntQuestGoalEntity> getGoals() {
        try {
            return artifactHuntGoalDao.queryForAll();
        } catch (SQLException e) {
            logger.severe("Failed to get goals: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<ArtifactHuntQuestGoalEntity> getGoals(int difficulty) {
        try {
            return artifactHuntGoalDao.queryForEq("difficulty", difficulty);
        } catch (SQLException e) {
            logger.severe("Failed to get goals: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public ArtifactHuntQuestGoalEntity getGoal(Long id) {
        try {
            return artifactHuntGoalDao.queryForId(id);
        } catch (SQLException e) {
            logger.severe("Failed to get goal: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void insertQuest(ArtifactHuntEntity quest) {
        try {
            artifactHuntQuestDao.create(quest);
        } catch (SQLException e) {
            logger.severe("Failed to create gather quest: " + e.getMessage());
        }
    }

    @Override
    public ArtifactHuntEntity getQuest(Long questId) {
        try {
            return artifactHuntQuestDao.queryForEq("quest_id", questId).getFirst();
        } catch (SQLException e) {
            logger.severe("Failed to get quest: " + e.getMessage());
            return null;
        }
    }

    @Override
    public int getHighestDifficulty() {
        ArtifactHuntQuestGoalEntity highest = null;
        try {
            highest = artifactHuntGoalDao.queryBuilder()
                    .orderBy("difficulty", false) // false = descending
                    .limit(1L)
                    .queryForFirst();
        } catch (SQLException e) {
            logger.severe("Failed to get highest difficulty: " + e.getMessage());
        }

        if (highest != null) {
            return highest.getDifficulty();
        }
        return -1;
    }
}
