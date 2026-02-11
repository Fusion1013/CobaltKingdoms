package se.fusion1013.cobaltKingdoms.database.letter;

import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.database.system.IDao;
import se.fusion1013.cobaltKingdoms.pigeon.Letter;

import java.util.List;
import java.util.UUID;

public interface ILetterDao extends IDao {

    void storeLetter(String senderName, String receiverName, ItemStack book);

    void deleteLetter(UUID letterId);

    void deleteLetters(String receiverName);

    List<Letter> getReceiverLetters(String receiverName);

    @Override
    default String getId() {
        return "letter";
    }
}
