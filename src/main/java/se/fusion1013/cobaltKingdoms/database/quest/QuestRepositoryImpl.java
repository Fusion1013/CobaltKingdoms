package se.fusion1013.cobaltKingdoms.database.quest;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.database.system.DataStorageType;
import se.fusion1013.cobaltCore.database.system.implementations.SQLiteImplementation;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.database.quest.artifact_hunt.IQuestArtifactHuntRepository;
import se.fusion1013.cobaltKingdoms.database.quest.bounty.IBountyRepository;
import se.fusion1013.cobaltKingdoms.database.quest.item_delivery.IQuestItemDeliveryRepository;
import se.fusion1013.cobaltKingdoms.database.quest.mapper.PlayerQuestMapper;
import se.fusion1013.cobaltKingdoms.database.quest.mapper.QuestMapper;
import se.fusion1013.cobaltKingdoms.kingdom.town.Town;
import se.fusion1013.cobaltKingdoms.quest.AbstractQuest;
import se.fusion1013.cobaltKingdoms.quest.PlayerQuest;
import se.fusion1013.cobaltKingdoms.quest.QuestStatus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public class QuestRepositoryImpl implements IQuestRepository {

    private static final Logger logger = CobaltKingdoms.getInstance().getLogger();

    private static final DataManager dataManager = DataManager.getInstance();
    private static final IQuestArtifactHuntRepository artifactHuntRepository = dataManager.getDao(IQuestArtifactHuntRepository.class);
    private static final IQuestItemDeliveryRepository itemDeliveryRepository = dataManager.getDao(IQuestItemDeliveryRepository.class);
    private static final IBountyRepository bountyRepository = dataManager.getDao(IBountyRepository.class);

    private Dao<QuestEntity, Long> questDao;
    private Dao<PlayerQuestEntity, Long> activePlayerQuestDao;

    @Override
    public void init() {
        try {
            ConnectionSource connectionSource = SQLiteImplementation.getConnectionSource();

            questDao = DaoManager.createDao(connectionSource, QuestEntity.class);
            activePlayerQuestDao = DaoManager.createDao(connectionSource, PlayerQuestEntity.class);

            TableUtils.createTableIfNotExists(connectionSource, QuestEntity.class);
            TableUtils.createTableIfNotExists(connectionSource, PlayerQuestEntity.class);

        } catch (SQLException e) {
            logger.severe("Error initializing Quest DAO: " + e.getMessage());
        }
    }

    // ##%%##%%## GENERAL QUESTS ##%%##%%## //

    @Override
    public List<AbstractQuest> getQuests() {
        List<AbstractQuest> quests = new ArrayList<>();
        quests.addAll(artifactHuntRepository.getQuests());
        quests.addAll(itemDeliveryRepository.getQuests());
        quests.addAll(bountyRepository.getQuests());
        return quests;
    }

    @Override
    public List<AbstractQuest> getQuests(Town town) {
        List<AbstractQuest> quests = getQuests();
        if (quests.isEmpty()) return List.of();

        return quests.stream()
                .filter(Objects::nonNull)
                .filter(quest -> quest.getStartTown() != null)
                .filter(quest -> quest.getStartTown().getId().equals(town.getId()))
                .toList();
    }

    @Override
    public Optional<AbstractQuest> getQuest(Long questId) {
        return getQuests().stream()
                .filter(quest -> quest.getQuestId().equals(questId))
                .findFirst();
    }

    @Override
    public void updateStatus(Long id, QuestStatus questStatus) {
        try {
            QuestEntity questEntity = questDao.queryForId(id);
            if (questEntity == null) return;

            questEntity.setStatus(questStatus);
            questDao.update(questEntity);
        } catch (SQLException e) {
            logger.severe("Error getting quest: " + e.getMessage());
        }
    }

    // ##%%##%%## PLAYER QUESTS ##%%##%%## //

    @Override
    public void createPlayerQuest(PlayerQuest playerQuest) {
        try {
            PlayerQuestEntity playerQuestEntity = PlayerQuestMapper.toEntity(playerQuest);
            playerQuestEntity.setQuest(getQuestEntity(playerQuest.getQuest().getQuestId()));
            activePlayerQuestDao.create(playerQuestEntity);
        } catch (SQLException e) {
            logger.severe("Error inserting active quest: " + e.getMessage());
        }
    }

    @Override
    public Optional<PlayerQuest> getPlayerQuestByQuestId(Long questId) {
        try {
            PlayerQuestEntity playerQuestEntity = activePlayerQuestDao.queryForEq("quest_id", questId).getFirst();
            return Optional.of(PlayerQuestMapper.toModel(playerQuestEntity));
        } catch (SQLException e) {
            logger.severe("Error getting active player quest: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void removePlayerQuestById(Long id) {
        try {
            activePlayerQuestDao.deleteById(id);
        } catch (SQLException e) {
            logger.severe("Error deleting active player quest: " + e.getMessage());
        }
    }

    @Override
    public Optional<List<PlayerQuest>> getPlayerQuestsByPlayer(Player player) {
        try {
            List<PlayerQuestEntity> playerQuests = activePlayerQuestDao.queryForEq("playerUUID", player.getUniqueId());
            return Optional.of(PlayerQuestMapper.toModels(playerQuests));
        } catch (SQLException e) {
            logger.severe("Error getting active player quest: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<PlayerQuest> getPlayerQuests() {
        try {
            return PlayerQuestMapper.toModels(activePlayerQuestDao.queryForAll());
        } catch (SQLException e) {
            logger.severe("Error getting active player quests: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public QuestEntity getQuestEntity(Long id) {
        try {
            return questDao.queryForId(id);
        } catch (SQLException e) {
            logger.severe("Error getting quest entity: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void createQuest(AbstractQuest quest) {
        QuestEntity entity = QuestMapper.toEntity(quest);
        try {
            questDao.createOrUpdate(entity);
        } catch (SQLException e) {
            logger.severe("Failed to create quest: " + e.getMessage());
        }
    }

    // ##%%##%%## ITEM DELIVERY QUESTS ##%%##%%## //

    @Override
    public DataStorageType getDataStorageType() {
        return DataStorageType.SQLITE;
    }
}
