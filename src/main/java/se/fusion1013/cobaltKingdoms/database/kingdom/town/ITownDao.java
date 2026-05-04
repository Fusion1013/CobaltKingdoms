package se.fusion1013.cobaltKingdoms.database.kingdom.town;

import se.fusion1013.cobaltCore.database.system.IDao;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownData;

import java.util.List;
import java.util.UUID;

public interface ITownDao extends IDao {

    void insertTown(TownData townData);

    List<TownData> getTownData();

    void deleteTown(UUID id);

    @Override
    default String getId() {
        return "town";
    }

}
