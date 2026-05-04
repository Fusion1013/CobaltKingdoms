package se.fusion1013.cobaltKingdoms.database.kingdom.town;

import se.fusion1013.cobaltCore.database.system.Dao;
import se.fusion1013.cobaltCore.database.system.DataStorageType;
import se.fusion1013.cobaltCore.database.system.implementations.SQLiteImplementation;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TownDaoSQLite extends Dao implements ITownDao {

    private static final String SQLiteCreateTownTable = """
            CREATE TABLE IF NOT EXISTS towns(
            `uuid` varchar(36) NOT NULL,
            `owner_id` varchar(36) NOT NULL,
            `name` varchar(36) NOT NULL,
            `kingdom_id` varchar(36) NOT NULL,
            `center_x` REAL NOT NULL,
            `center_y` REAL NOT NULL,
            `center_z` REAL NOT NULL,
            `world_id` varchar(36) NOT NULL,
            PRIMARY KEY (`uuid`)
            );
            """;

    @Override
    public void insertTown(TownData townData) {
        SQLiteImplementation.performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement insertTownPs = conn.prepareStatement("INSERT OR REPLACE INTO towns(uuid, owner_id, name, kingdom_id, center_x, center_y, center_z, world_id) VALUES(?, ?, ?, ?, ?, ?, ?, ?)")
            ) {
                insertTownPs.setString(1, townData.uuid().toString());
                insertTownPs.setString(2, townData.ownerUuid().toString());
                insertTownPs.setString(3, townData.townName());
                insertTownPs.setString(4, townData.kingdomId().toString());
                insertTownPs.setDouble(5, townData.xCenter());
                insertTownPs.setDouble(6, townData.yCenter());
                insertTownPs.setDouble(7, townData.zCenter());
                insertTownPs.setString(8, townData.worldUuid().toString());
                insertTownPs.executeUpdate();
            } catch (SQLException ex) {
                CobaltKingdoms.getInstance().getLogger().severe("Error inserting town into database: " + ex.getMessage());
            }
        });
    }

    @Override
    public List<TownData> getTownData() {
        List<TownData> townDataList = new ArrayList<>();

        SQLiteImplementation.performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement getTownsPs = conn.prepareStatement("SELECT * FROM towns")
            ) {
                ResultSet townResult = getTownsPs.executeQuery();

                while (townResult.next()) {
                    String townUuid = townResult.getString("uuid");
                    String ownerId = townResult.getString("owner_id");
                    String name = townResult.getString("name");
                    String kingdomId = townResult.getString("kingdom_id");
                    double xCenter = townResult.getDouble("center_x");
                    double yCenter = townResult.getDouble("center_y");
                    double zCenter = townResult.getDouble("center_z");
                    String worldId = townResult.getString("world_id");

                    townDataList.add(new TownData(UUID.fromString(townUuid), UUID.fromString(ownerId), name, UUID.fromString(kingdomId), xCenter, yCenter, zCenter, UUID.fromString(worldId)));
                }
            } catch (SQLException ex) {
                CobaltKingdoms.getInstance().getLogger().severe("Error getting towns from database: " + ex.getMessage());
            }
        });

        return townDataList;
    }

    @Override
    public void deleteTown(UUID id) {

    }

    @Override
    public DataStorageType getDataStorageType() {
        return DataStorageType.SQLITE;
    }

    @Override
    public void init() {
        SQLiteImplementation.getSqliteDb().executeString(SQLiteCreateTownTable);
    }
}
