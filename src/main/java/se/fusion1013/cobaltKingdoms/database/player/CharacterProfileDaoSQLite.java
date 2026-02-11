package se.fusion1013.cobaltKingdoms.database.player;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import se.fusion1013.cobaltCore.database.system.Dao;
import se.fusion1013.cobaltCore.database.system.DataStorageType;
import se.fusion1013.cobaltCore.database.system.implementations.SQLiteImplementation;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.player.character.CharacterProfile;
import se.fusion1013.cobaltKingdoms.player.character.ICharacterProfile;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CharacterProfileDaoSQLite extends Dao implements ICharacterProfileDao {

    private static final String SQLiteCreateActiveCharacterTable = """
            CREATE TABLE IF NOT EXISTS active_character(
            `player_id` varchar(36) NOT NULL,
            `profile_id` varchar(36) NOT NULL,
            PRIMARY KEY (`player_id`)
            );
            """;

    private static final String SQLiteCreateCharacterProfileTable = """
            CREATE TABLE IF NOT EXISTS character_profiles(
            `profile_id` varchar(36) NOT NULL,
            `player_id` varchar(36) NOT NULL,
            `character_id` varchar(36) NOT NULL,
            `data` text NOT NULL,
            PRIMARY KEY (`character_id`)
            );
            """;

    @Override
    public void insertActiveCharacter(UUID playerId, UUID profileId) {
        SQLiteImplementation.performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO active_character(player_id, profile_id) VALUES(?, ?)")
            ) {
                ps.setString(1, playerId.toString());
                ps.setString(2, profileId.toString());
                ps.executeUpdate();
            } catch (SQLException ex) {
                CobaltKingdoms.getInstance().getLogger().severe("Error inserting active character into database: " + ex.getMessage());
            }
        });
    }

    @Override
    public Map<UUID, UUID> getActiveCharacters() {
        Map<UUID, UUID> activeCharacters = new HashMap<>();
        SQLiteImplementation.performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement ps = conn.prepareStatement("SELECT * FROM active_character")
            ) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String playerIdString = rs.getString("player_id");
                        UUID playerId = UUID.fromString(playerIdString);
                        String profileIdString = rs.getString("profile_id");
                        UUID profileId = UUID.fromString(profileIdString);
                        activeCharacters.put(playerId, profileId);
                    }
                }
            } catch (SQLException ex) {
                CobaltKingdoms.getInstance().getLogger().severe("Error getting active characters from database: " + ex.getMessage());
            }
        });
        return activeCharacters;
    }

    @Override
    public void insertCharacterProfile(ICharacterProfile characterProfile) {
        SQLiteImplementation.performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement insertCharacterProfilePs = conn.prepareStatement("INSERT OR REPLACE INTO character_profiles(profile_id, player_id, character_id, data) VALUES(?, ?, ?, ?)")
            ) {
                insertCharacterProfilePs.setString(1, characterProfile.getProfileId().toString());
                insertCharacterProfilePs.setString(2, characterProfile.getPlayerId().toString());
                insertCharacterProfilePs.setString(3, characterProfile.getCharacterId());
                insertCharacterProfilePs.setString(4, characterProfile.getJsonData().toString());
                insertCharacterProfilePs.executeUpdate();
            } catch (SQLException ex) {
                CobaltKingdoms.getInstance().getLogger().severe("Error inserting character profile into database: " + ex.getMessage());
            }
        });
    }

    @Override
    public Map<UUID, List<ICharacterProfile>> getCharacterProfiles() {
        Map<UUID, List<ICharacterProfile>> characterProfiles = new HashMap<>();

        SQLiteImplementation.performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement getCharacterProfilesPs = conn.prepareStatement("SELECT * FROM character_profiles")
            ) {
                try (ResultSet characterProfilesResult = getCharacterProfilesPs.executeQuery()) {
                    JSONParser parser = new JSONParser();

                    while (characterProfilesResult.next()) {
                        String profileId = characterProfilesResult.getString("profile_id");
                        String playerId = characterProfilesResult.getString("player_id");
                        String characterId = characterProfilesResult.getString("character_id");
                        String data = characterProfilesResult.getString("data");

                        UUID profileUuid = UUID.fromString(profileId);
                        UUID playerUuid = UUID.fromString(playerId);
                        JSONObject json = (JSONObject) parser.parse(data);

                        CharacterProfile profile = new CharacterProfile(profileUuid, playerUuid, characterId, json);
                        List<ICharacterProfile> list = characterProfiles.computeIfAbsent(playerUuid, k -> new ArrayList<>());
                        list.add(profile);
                    }
                }

            } catch (SQLException | ParseException ex) {
                CobaltKingdoms.getInstance().getLogger().severe("Error getting character profiles from database: " + ex.getMessage());
            }
        });

        return characterProfiles;
    }

    @Override
    public DataStorageType getDataStorageType() {
        return DataStorageType.SQLITE;
    }

    @Override
    public void init() {
        SQLiteImplementation.getSqliteDb().executeString(SQLiteCreateCharacterProfileTable);
        SQLiteImplementation.getSqliteDb().executeString(SQLiteCreateActiveCharacterTable);
    }
}
