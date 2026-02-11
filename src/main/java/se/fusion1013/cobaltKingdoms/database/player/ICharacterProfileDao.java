package se.fusion1013.cobaltKingdoms.database.player;

import se.fusion1013.cobaltCore.database.system.IDao;
import se.fusion1013.cobaltKingdoms.player.character.ICharacterProfile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ICharacterProfileDao extends IDao {

    void insertActiveCharacter(UUID playerId, UUID profileId);

    Map<UUID, UUID> getActiveCharacters();

    void insertCharacterProfile(ICharacterProfile characterProfile);

    Map<UUID, List<ICharacterProfile>> getCharacterProfiles();

    @Override
    default String getId() {
        return "character_profile";
    }
}
