package se.fusion1013.cobaltKingdoms.pigeon;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.database.letter.ILetterDao;

import java.util.*;

public class LetterManager extends Manager<CobaltKingdoms> implements Listener {

    private static final Map<UUID, List<Letter>> PLAYER_LETTERS = new HashMap<>();
    private static final ILetterDao LETTER_DAO = DataManager.getInstance().getDao(ILetterDao.class);

    public void sendLetter(Player senderPlayer, String receiverName, ItemStack itemStack, boolean forceOfflineSend) {
        PigeonEvents.sendSenderPigeon(senderPlayer);
        sendLetter(senderPlayer.getName(), receiverName, itemStack, forceOfflineSend);
    }

    public void sendLetter(String senderName, String receiverName, ItemStack itemStack, boolean forceOfflineSend) {
        Optional<? extends Player> receiverPlayerOptional = Bukkit.getOnlinePlayers().stream().filter(p -> p.getName().equalsIgnoreCase(receiverName)).findFirst();

        if (receiverPlayerOptional.isEmpty() || forceOfflineSend)
            sendLetterOfflinePlayer(senderName, receiverName, itemStack);
        else sendLetterOnlinePlayer(receiverName, itemStack);
    }

    private void sendLetterOfflinePlayer(String senderName, String receiverName, ItemStack itemStack) {
        LETTER_DAO.storeLetter(senderName, receiverName, itemStack);
    }

    private void sendLetterOnlinePlayer(String receiverName, ItemStack itemStack) {
        Optional<? extends Player> receiverPlayerOptional = Bukkit.getOnlinePlayers().stream().filter(p -> p.getName().equalsIgnoreCase(receiverName)).findFirst();

        if (receiverPlayerOptional.isEmpty()) return;

        Player receiver = receiverPlayerOptional.get();
        PigeonEvents.receiveLetter(receiver, itemStack);
    }

    // ##%%##%%## LOADING / DISABLING ##%%##%%## //

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltKingdoms.getInstance());
    }

    @Override
    public void disable() {

    }

    // ##%%##%%## EVENTS ##%%##%%## //

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        List<Letter> letters = LETTER_DAO.getReceiverLetters(player.getName());
        PLAYER_LETTERS.put(player.getUniqueId(), letters);
        if (letters.isEmpty()) return;

        LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.letters.notification_on_join", StringPlaceholders.builder()
                .addPlaceholder("letters", letters.size())
                .build());
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
    }

    // ##%%##%%## GETTERS / SETTERS ##%%##%%## //

    public List<Letter> getPlayerLetters(UUID playerId) {
        return PLAYER_LETTERS.get(playerId);
    }

    public void trySendSavedLetters(UUID receiverUuid, UUID letterId) {
        List<Letter> letters = PLAYER_LETTERS.get(receiverUuid);
        if (letters == null) return;

        List<Letter> sendLetters = letters.stream().filter(letter -> letter.letterId().equals(letterId)).toList();
        for (Letter letter : sendLetters) {
            sendLetterOnlinePlayer(letter.receiver(), letter.data());
        }
    }

    public void trySendAllSavedLetters(@NotNull UUID receiverUuid) {
        Optional<? extends Player> receiverPlayerOptional = Bukkit.getOnlinePlayers().stream().filter(p -> p.getUniqueId().equals(receiverUuid)).findFirst();
        if (receiverPlayerOptional.isEmpty()) return;

        List<Letter> letters = PLAYER_LETTERS.get(receiverUuid);
        for (Letter letter : letters) {
            sendLetterOnlinePlayer(letter.receiver(), letter.data());
        }

        PLAYER_LETTERS.remove(receiverUuid);
        LETTER_DAO.deleteLetters(receiverPlayerOptional.get().getName());
    }

    // ##%%##%%## INSTANCE ##%%##%%## //

    public LetterManager(CobaltKingdoms plugin) {
        super(plugin);
    }

    private static LetterManager INSTANCE;

    public static LetterManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LetterManager(CobaltKingdoms.getInstance());
        }
        return INSTANCE;
    }
}
