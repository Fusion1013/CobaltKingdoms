package se.fusion1013.cobaltKingdoms.database;

import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.database.kingdom.IKingdomDao;
import se.fusion1013.cobaltKingdoms.database.kingdom.KingdomDaoSQLite;

public class KingdomDataManager extends Manager<CobaltKingdoms> {

    public KingdomDataManager(CobaltKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        DataManager dataManager = DataManager.getInstance();
        dataManager.registerDao(new KingdomDaoSQLite(), IKingdomDao.class);
    }

    @Override
    public void disable() {

    }
}
