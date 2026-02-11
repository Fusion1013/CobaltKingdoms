package se.fusion1013.cobaltKingdoms.database.letter;

import com.google.gson.Gson;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.database.system.DataStorageType;
import se.fusion1013.cobaltCore.database.system.implementations.SQLiteImplementation;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.pigeon.Letter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class LetterDaoSQLite implements ILetterDao {

    private static final Gson gson = new Gson();
    private static final String SQLiteCreateLetterTable = """
            CREATE TABLE IF NOT EXISTS letters(
            `letter_id` varchar(36) NOT NULL,
            `sender_name` varchar(36) NOT NULL,
            `receiver_name` varchar(36) NOT NULL,
            `data` text NOT NULL,
            PRIMARY KEY (`letter_id`)
            );
            """;

    @Override
    public void storeLetter(String senderName, String receiverName, ItemStack book) {
        Map<String, Object> map = new HashMap<>(book.serialize());
        map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, "ItemStack");
        String jsonString = gson.toJson(map);
        SQLiteImplementation.performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement ps = conn.prepareStatement("INSERT INTO letters(letter_id, sender_name, receiver_name, data) VALUES(?, ?, ?, ?)")
            ) {
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, senderName);
                ps.setString(3, receiverName);
                ps.setString(4, jsonString);
                ps.executeUpdate();
            } catch (SQLException ex) {
                CobaltKingdoms.getInstance().getLogger().severe("Error inserting letter into database: " + ex.getMessage());
            }
        });
    }

    @Override
    public void deleteLetter(UUID letterId) {
        SQLiteImplementation.performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM letters WHERE letter_id = ?")
            ) {
                ps.setString(1, letterId.toString());
                ps.executeUpdate();
            } catch (SQLException ex) {
                CobaltKingdoms.getInstance().getLogger().severe("Error deleting letter from database: " + ex.getMessage());
            }
        });
    }

    @Override
    public void deleteLetters(String receiverName) {
        SQLiteImplementation.performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM letters WHERE receiver_name = ?")
            ) {
                ps.setString(1, receiverName);
                ps.executeUpdate();
            } catch (SQLException ex) {
                CobaltKingdoms.getInstance().getLogger().severe("Error deleting letters from database: " + ex.getMessage());
            }
        });
    }

    @Override
    public List<Letter> getReceiverLetters(String receiverName) {
        List<Letter> letters = new ArrayList<>();

        SQLiteImplementation.performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement ps = conn.prepareStatement("SELECT * FROM letters WHERE receiver_name = ? COLLATE NOCASE")
            ) {
                ps.setString(1, receiverName);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String letterId = rs.getString("letter_id");
                        String senderName = rs.getString("sender_name");
                        String data = rs.getString("data");
                        ItemStack itemStack = ItemStack.deserialize(gson.fromJson(data, Map.class));
                        Letter letter = new Letter(UUID.fromString(letterId), senderName, receiverName, itemStack);
                        letters.add(letter);
                    }
                }
            } catch (SQLException ex) {
                CobaltKingdoms.getInstance().getLogger().severe("Error getting letters from database: " + ex.getMessage());
            }
        });

        return letters;
    }


    @Override
    public DataStorageType getDataStorageType() {
        return DataStorageType.SQLITE;
    }

    @Override
    public void init() {
        SQLiteImplementation.getSqliteDb().executeString(SQLiteCreateLetterTable);
    }
}
