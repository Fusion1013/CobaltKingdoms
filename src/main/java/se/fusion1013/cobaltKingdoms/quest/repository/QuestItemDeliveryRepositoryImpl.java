package se.fusion1013.cobaltKingdoms.quest.repository;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import se.fusion1013.cobaltCore.database.system.DataStorageType;
import se.fusion1013.cobaltCore.database.system.implementations.SQLiteImplementation;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.quest.entity.ItemDeliveryQuestEntity;
import se.fusion1013.cobaltKingdoms.quest.mapper.ItemDeliveryQuestMapper;
import se.fusion1013.cobaltKingdoms.quest.mapper.QuestMapper;
import se.fusion1013.cobaltKingdoms.quest.model.ItemDeliveryQuest;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class QuestItemDeliveryRepositoryImpl implements IQuestItemDeliveryRepository {

    private static final Logger logger = CobaltKingdoms.getInstance().getLogger();

    private Dao<ItemDeliveryQuestEntity, Long> itemDeliveryQuestDao;

    @Override
    public void init() {
        try {
            ConnectionSource connectionSource = SQLiteImplementation.getConnectionSource();

            itemDeliveryQuestDao = DaoManager.createDao(connectionSource, ItemDeliveryQuestEntity.class);

            TableUtils.createTableIfNotExists(connectionSource, ItemDeliveryQuestEntity.class);

        } catch (SQLException e) {
            logger.severe("Error initializing Quest DAO: " + e.getMessage());
        }
    }

    @Override
    public void createQuest(ItemDeliveryQuest quest) {
        ItemDeliveryQuestEntity entity = ItemDeliveryQuestMapper.toEntity(quest);
        try {
            entity.setQuest(QuestMapper.toEntity(quest));
            itemDeliveryQuestDao.createOrUpdate(entity);
        } catch (SQLException e) {
            logger.severe("Error creating item delivery quest: " + e.getMessage());
        }
    }

    @Override
    public List<ItemDeliveryQuest> getQuests() {
        try {
            return ItemDeliveryQuestMapper.toModels(itemDeliveryQuestDao.queryForAll());
        } catch (SQLException e) {
            logger.severe("Error getting item delivery quests: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public DataStorageType getDataStorageType() {
        return DataStorageType.SQLITE;
    }
}
