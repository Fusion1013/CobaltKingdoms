package se.fusion1013.cobaltKingdoms.kingdom.repository;

import se.fusion1013.cobaltCore.database.system.IDao;
import se.fusion1013.cobaltKingdoms.kingdom.model.KingdomData;

import java.util.List;
import java.util.UUID;

public interface IKingdomRepository extends IDao {

    void insertKingdom(KingdomData kingdomData);

    List<KingdomData> getKingdomData();

    void insertPlayer(UUID playerId, UUID kingdomId);

    void removePlayer(UUID playerId, UUID kingdomId);

    void deleteKingdom(UUID id);

    @Override
    default String getId() {
        return "kingdom";
    }
}
