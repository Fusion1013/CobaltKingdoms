package se.fusion1013.cobaltKingdoms.quest.repository;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import se.fusion1013.cobaltCore.database.system.DataStorageType;
import se.fusion1013.cobaltCore.database.system.implementations.SQLiteImplementation;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.quest.entity.ArtifactHuntEntity;
import se.fusion1013.cobaltKingdoms.quest.entity.ArtifactHuntQuestGoalEntity;
import se.fusion1013.cobaltKingdoms.quest.mapper.ArtifactHuntGoalMapper;
import se.fusion1013.cobaltKingdoms.quest.mapper.ArtifactHuntQuestMapper;
import se.fusion1013.cobaltKingdoms.quest.mapper.QuestMapper;
import se.fusion1013.cobaltKingdoms.quest.model.ArtifactHuntGoal;
import se.fusion1013.cobaltKingdoms.quest.model.ArtifactHuntQuest;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class QuestArtifactHuntRepository implements IQuestArtifactHuntRepository {

    private static final Logger logger = CobaltKingdoms.getInstance().getLogger();

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
    public void createGoal(ArtifactHuntGoal goal) {
        try {
            artifactHuntGoalDao.create(ArtifactHuntGoalMapper.toEntity(goal));
        } catch (SQLException e) {
            logger.severe("Failed to create new goal: " + e.getMessage());
        }
    }

    @Override
    public List<ArtifactHuntGoal> getGoals() {
        try {
            return ArtifactHuntGoalMapper.toModels(artifactHuntGoalDao.queryForAll());
        } catch (SQLException e) {
            logger.severe("Failed to get goals: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<ArtifactHuntGoal> getGoals(int difficulty) {
        try {
            return ArtifactHuntGoalMapper.toModels(artifactHuntGoalDao.queryForEq("difficulty", difficulty));
        } catch (SQLException e) {
            logger.severe("Failed to get goals: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public ArtifactHuntGoal getGoal(Long id) {
        try {
            return ArtifactHuntGoalMapper.toModel(artifactHuntGoalDao.queryForId(id));
        } catch (SQLException e) {
            logger.severe("Failed to get goal: " + e.getMessage());
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

    @Override
    public void deleteQuestsWithIds(List<Long> ids) {
        try {
            DeleteBuilder<ArtifactHuntEntity, Long> deleteStatement =
                    artifactHuntQuestDao.deleteBuilder();
            deleteStatement.where().in("quest_id", ids);
            deleteStatement.delete();
        } catch (SQLException e) {
            logger.severe("Error deleting item delivery quests with quest ids: " + e.getMessage());
        }
    }

    @Override
    public void createQuest(ArtifactHuntQuest quest) {
        ArtifactHuntEntity entity = ArtifactHuntQuestMapper.toEntity(quest);
        try {
            entity.setQuest(QuestMapper.toEntity(quest));
            entity.setGoal(artifactHuntGoalDao.queryForId(quest.getGoal().getId()));
            artifactHuntQuestDao.createOrUpdate(entity);
        } catch (SQLException e) {
            logger.severe("Failed to create gather quest: " + e.getMessage());
        }
    }

    @Override
    public ArtifactHuntQuest getQuest(Long questId) {
        try {
            return ArtifactHuntQuestMapper.toModel(artifactHuntQuestDao.queryForEq("quest_id", questId).getFirst());
        } catch (SQLException e) {
            logger.severe("Failed to get artifact hunt quests: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<ArtifactHuntQuest> getQuests() {
        try {
            return ArtifactHuntQuestMapper.toModels(artifactHuntQuestDao.queryForAll());
        } catch (SQLException e) {
            logger.severe("Failed to get artifact hunt quests: " + e.getMessage());
            return List.of();
        }
    }
}
