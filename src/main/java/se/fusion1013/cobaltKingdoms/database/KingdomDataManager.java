package se.fusion1013.cobaltKingdoms.database;

import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.database.letter.ILetterDao;
import se.fusion1013.cobaltKingdoms.database.letter.LetterDaoSQLite;
import se.fusion1013.cobaltKingdoms.database.player.CharacterProfileDaoSQLite;
import se.fusion1013.cobaltKingdoms.database.player.ICharacterProfileDao;
import se.fusion1013.cobaltKingdoms.kingdom.repository.IKingdomRepository;
import se.fusion1013.cobaltKingdoms.kingdom.repository.KingdomRepositoryImpl;
import se.fusion1013.cobaltKingdoms.quest.repository.*;
import se.fusion1013.cobaltKingdoms.town.repository.ITownRepository;
import se.fusion1013.cobaltKingdoms.town.repository.TownRepositoryImpl;

public class KingdomDataManager extends Manager<CobaltKingdoms> {

    public KingdomDataManager(CobaltKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        DataManager dataManager = DataManager.getInstance();
        dataManager.registerDao(new KingdomRepositoryImpl(), IKingdomRepository.class);
        dataManager.registerDao(new CharacterProfileDaoSQLite(), ICharacterProfileDao.class);
        dataManager.registerDao(new LetterDaoSQLite(), ILetterDao.class);
        dataManager.registerDao(new TownRepositoryImpl(), ITownRepository.class);
        dataManager.registerDao(new QuestArtifactHuntRepository(), IQuestArtifactHuntRepository.class);
        dataManager.registerDao(new BountyRepositoryImpl(), IBountyRepository.class);
        dataManager.registerDao(new QuestItemDeliveryRepositoryImpl(), IQuestItemDeliveryRepository.class);

        // This is kinda stupid but this has to be last
        dataManager.registerDao(new QuestRepositoryImpl(), IQuestRepository.class);
    }

    @Override
    public void disable() {

    }
}
