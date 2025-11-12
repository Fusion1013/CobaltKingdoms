package se.fusion1013.cobaltKingdoms.database.kingdom;

import se.fusion1013.cobaltCore.database.system.IDao;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomData;

import java.util.List;
import java.util.UUID;

public interface IKingdomDao extends IDao {

    void insertKingdom(KingdomData kingdomData);
    List<KingdomData> getKingdomData();
    void insertPlayer(UUID playerId, UUID kingdomId);
    void removePlayer(UUID playerId, UUID kingdomId);
    void deleteKingdom(UUID id);

    @Override
    default String getId() { return "kingdom"; }
}
