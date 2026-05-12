package se.fusion1013.cobaltKingdoms.database;

import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.database.kingdom.IKingdomDao;
import se.fusion1013.cobaltKingdoms.database.kingdom.KingdomDaoSQLite;
import se.fusion1013.cobaltKingdoms.database.kingdom.town.ITownRepository;
import se.fusion1013.cobaltKingdoms.database.kingdom.town.TownRepositoryImpl;
import se.fusion1013.cobaltKingdoms.database.letter.ILetterDao;
import se.fusion1013.cobaltKingdoms.database.letter.LetterDaoSQLite;
import se.fusion1013.cobaltKingdoms.database.player.CharacterProfileDaoSQLite;
import se.fusion1013.cobaltKingdoms.database.player.ICharacterProfileDao;
import se.fusion1013.cobaltKingdoms.database.quest.IQuestRepository;
import se.fusion1013.cobaltKingdoms.database.quest.QuestRepositoryImpl;
import se.fusion1013.cobaltKingdoms.database.quest.gather.IQuestGatherRepository;
import se.fusion1013.cobaltKingdoms.database.quest.gather.QuestGatherRepository;

public class KingdomDataManager extends Manager<CobaltKingdoms> {

    public KingdomDataManager(CobaltKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        DataManager dataManager = DataManager.getInstance();
        dataManager.registerDao(new KingdomDaoSQLite(), IKingdomDao.class);
        dataManager.registerDao(new CharacterProfileDaoSQLite(), ICharacterProfileDao.class);
        dataManager.registerDao(new LetterDaoSQLite(), ILetterDao.class);
        dataManager.registerDao(new TownRepositoryImpl(), ITownRepository.class);
        dataManager.registerDao(new QuestRepositoryImpl(), IQuestRepository.class);
        dataManager.registerDao(new QuestGatherRepository(), IQuestGatherRepository.class);
    }

    @Override
    public void disable() {

    }
}
