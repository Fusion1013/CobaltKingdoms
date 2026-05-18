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
import se.fusion1013.cobaltKingdoms.database.quest.artifact_hunt.IQuestArtifactHuntRepository;
import se.fusion1013.cobaltKingdoms.database.quest.artifact_hunt.QuestArtifactHuntRepository;
import se.fusion1013.cobaltKingdoms.database.quest.bounty.BountyRepositoryImpl;
import se.fusion1013.cobaltKingdoms.database.quest.bounty.IBountyRepository;
import se.fusion1013.cobaltKingdoms.database.quest.item_delivery.IQuestItemDeliveryRepository;
import se.fusion1013.cobaltKingdoms.database.quest.item_delivery.QuestItemDeliveryRepositoryImpl;

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
