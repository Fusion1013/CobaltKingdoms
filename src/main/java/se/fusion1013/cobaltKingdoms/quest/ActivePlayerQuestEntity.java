package se.fusion1013.cobaltKingdoms.quest;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

import java.util.Date;
import java.util.UUID;

@DatabaseTable(tableName = "quest_player_active")
public class ActivePlayerQuestEntity {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private QuestEntity quest;

    @DatabaseField
    private UUID playerUUID;

    @DatabaseField
    private String playerName;

    @DatabaseField
    private Date startTime;

    @DatabaseField
    private Date expiryTime;

    // Add these as class variables
    private static final NamespacedKey TRACKING_KEY = new NamespacedKey(
            CobaltKingdoms.getInstance(), "tracking_quest");

    // No-args constructor required by ORMLite
    public ActivePlayerQuestEntity() {
    }

    public ActivePlayerQuestEntity(
            QuestEntity quest,
            UUID playerUUID,
            Date startTime,
            Date expiryTime) {
        this.quest = quest;
        this.playerUUID = playerUUID;
        this.startTime = startTime;
        this.expiryTime = expiryTime;
        Player player = Bukkit.getPlayer(playerUUID);
        this.playerName = player == null ? null : player.getName();
    }

    public static ActivePlayerQuestEntity initiateMission(Player player, QuestEntity quest) {
        Date startTime = new Date();
        int duration = quest.getQuestData().getDuration();
        Date expiryTime = new Date(startTime.getTime() + duration);
        return new ActivePlayerQuestEntity(quest, player.getUniqueId(), startTime, expiryTime);
    }

    public Long getId() {
        return id;
    }

    public QuestEntity getQuest() {
        return quest;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getExpiryTime() {
        return expiryTime;
    }
}
