package se.fusion1013.cobaltKingdoms.quest;

import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

public class PlayerQuest {

    private Long id;
    private AbstractQuest quest;
    private UUID playerId;
    private String playerName;
    private Date startTime;
    private Date expiryTime;

    public PlayerQuest() {

    }

    public PlayerQuest(Long id) {
        this.id = id;
    }

    public static PlayerQuest initiateQuest(Player player, AbstractQuest quest) {
        Date startTime = new Date();
        int duration = quest.getDuration();
        Date expiryTime = new Date(startTime.getTime() + duration);

        PlayerQuest playerQuest = new PlayerQuest();
        playerQuest.setQuest(quest);
        playerQuest.setPlayerId(player.getUniqueId());
        playerQuest.setPlayerName(player.getName());
        playerQuest.setStartTime(startTime);
        playerQuest.setExpiryTime(expiryTime);

        return playerQuest;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AbstractQuest getQuest() {
        return quest;
    }

    public void setQuest(AbstractQuest quest) {
        this.quest = quest;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Date expiryTime) {
        this.expiryTime = expiryTime;
    }
}
