package se.fusion1013.cobaltKingdoms.database.kingdom;

import se.fusion1013.cobaltCore.database.system.Dao;
import se.fusion1013.cobaltCore.database.system.DataStorageType;
import se.fusion1013.cobaltCore.database.system.implementations.SQLiteImplementation;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KingdomDaoSQLite extends Dao implements IKingdomDao {

    private static final String SQLiteCreateKingdomTable = """
            CREATE TABLE IF NOT EXISTS kingdoms(
            `uuid` varchar(36) NOT NULL,
            `owner_id` varchar(36) NOT NULL,
            `name` varchar(36) NOT NULL,
            `color_prefix` varchar(36) NOT NULL,
            PRIMARY KEY (`uuid`)
            );
            """;

    private static final String SQLiteCreateKingdomMembersTable = """
            CREATE TABLE IF NOT EXISTS kingdom_members(
            `player_uuid` varchar(36) NOT NULL,
            `kingdom_uuid` varchar(36) NOT NULL,
            PRIMARY KEY (`player_uuid`, `kingdom_uuid`)
            );
            """;

    @Override
    public void insertKingdom(KingdomData kingdomData) {
        SQLiteImplementation.performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement insertKingdomPs = conn.prepareStatement("INSERT OR REPLACE INTO kingdoms(uuid, owner_id, name, color_prefix) VALUES(?, ?, ?, ?)");
                    PreparedStatement insertUserPs = conn.prepareStatement("INSERT OR IGNORE INTO kingdom_members(player_uuid, kingdom_uuid) VALUES(?, ?)")
            ) {
                conn.setAutoCommit(false);

                insertKingdomPs.setString(1, kingdomData.getId().toString());
                insertKingdomPs.setString(2, kingdomData.getOwner().toString());
                insertKingdomPs.setString(3, kingdomData.getName());
                insertKingdomPs.setString(4, kingdomData.getColorPrefix());
                insertKingdomPs.executeUpdate();

                for (UUID playerId : kingdomData.getMembers()) {
                    insertUserPs.setString(1, playerId.toString());
                    insertUserPs.setString(2, kingdomData.getId().toString());
                    insertUserPs.executeUpdate();
                }

                conn.commit();
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                CobaltKingdoms.getInstance().getLogger().severe("Error inserting kingdom into database: " + ex.getMessage());
            }
        });
    }

    @Override
    public List<KingdomData> getKingdomData() {
        List<KingdomData> kingdomDataList = new ArrayList<>();

        SQLiteImplementation.performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement getKingdomsPs = conn.prepareStatement("SELECT * FROM kingdoms");
                    PreparedStatement getUsersPs = conn.prepareStatement("SELECT * FROM kingdom_members WHERE kingdom_uuid = ?")
            ) {
                ResultSet kingdomResults = getKingdomsPs.executeQuery();

                while (kingdomResults.next()) {
                    String uuid = kingdomResults.getString("uuid");
                    String ownerId = kingdomResults.getString("owner_id");
                    String name = kingdomResults.getString("name");
                    String colorPrefix = kingdomResults.getString("color_prefix");

                    KingdomData kingdomData = new KingdomData(colorPrefix, new ArrayList<UUID>(), UUID.fromString(ownerId), UUID.fromString(uuid), name);

                    getUsersPs.setString(1, uuid);
                    ResultSet usersResult = getUsersPs.executeQuery();
                    List<UUID> members = new ArrayList<>();
                    while (usersResult.next()) {
                        String memberUuid = usersResult.getString("player_uuid");
                        members.add(UUID.fromString(memberUuid));
                    }

                    usersResult.close();

                    kingdomData.setMembers(members);
                    kingdomDataList.add(kingdomData);
                }

                kingdomResults.close();
            } catch (SQLException ex) {
                CobaltKingdoms.getInstance().getLogger().severe("Error getting kingdoms from database: " + ex.getMessage());
            }
        });

        return kingdomDataList;
    }

    @Override
    public void insertPlayer(UUID playerId, UUID kingdomId) {
        SQLiteImplementation.performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement insertUserPs = conn.prepareStatement("INSERT OR IGNORE INTO kingdom_members(player_uuid, kingdom_uuid) VALUES(?, ?)")
            ) {
                insertUserPs.setString(1, playerId.toString());
                insertUserPs.setString(2, kingdomId.toString());
                insertUserPs.executeUpdate();
            } catch (SQLException ex) {
                CobaltKingdoms.getInstance().getLogger().severe("Error inserting kingdom user into database: " + ex.getMessage());
            }
        });
    }

    @Override
    public void deleteKingdom(UUID id) {
        SQLiteImplementation.performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement deleteUsers = conn.prepareStatement("DELETE FROM kingdom_members WHERE kingdom_uuid = ?");
                    PreparedStatement deleteKingdom = conn.prepareStatement("DELETE FROM kingdoms WHERE uuid = ?")
            ) {
                conn.setAutoCommit(false);

                deleteUsers.setString(1, id.toString());
                deleteUsers.executeUpdate();

                deleteKingdom.setString(1, id.toString());
                deleteKingdom.executeUpdate();

                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void removePlayer(UUID playerId, UUID kingdomId) {
        SQLiteImplementation.performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement deleteUsers = conn.prepareStatement("DELETE FROM kingdom_members WHERE kingdom_uuid = ? AND player_uuid = ?")
            ) {
                deleteUsers.setString(1, kingdomId.toString());
                deleteUsers.setString(2, playerId.toString());
                deleteUsers.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public DataStorageType getDataStorageType() {
        return DataStorageType.SQLITE;
    }

    @Override
    public void init() {
        SQLiteImplementation.getSqliteDb().executeString(SQLiteCreateKingdomTable);
        SQLiteImplementation.getSqliteDb().executeString(SQLiteCreateKingdomMembersTable);
    }
}
