package se.fusion1013.cobaltKingdoms.quest.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.UUID;

@DatabaseTable(tableName = "bounty_player_status")
public class BountyPlayerStatusEntity {

    private static final double CompletedWeight = 4.0;
    private static final double FailedWeight = 2.25;
    private static final double EvadedWeight = 0.85;
    private static final double KilledWeight = 4.0;

    @DatabaseField(generatedId = true, columnName = "id")
    private Long id;

    @DatabaseField(columnName = "player_id", unique = true)
    private UUID playerId;

    @DatabaseField(columnName = "player_name")
    private String playerName;

    @DatabaseField(columnName = "completed")
    private int completed;

    @DatabaseField(columnName = "failed")
    private int failed;

    @DatabaseField(columnName = "evaded")
    private int evaded;

    @DatabaseField(columnName = "killed")
    private int killed;

    @DatabaseField(columnName = "bounties_enabled")
    private boolean bountiesEnabled;

    @DatabaseField(columnName = "rating")
    private double rating;

    @DatabaseField(columnName = "update_timestamp")
    private Date updateTimestamp;

    public BountyPlayerStatusEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public int getEvaded() {
        return evaded;
    }

    public void setEvaded(int evaded) {
        this.evaded = evaded;
    }

    public int getKilled() {
        return killed;
    }

    public void setKilled(int killed) {
        this.killed = killed;
    }

    public boolean isBountiesEnabled() {
        return bountiesEnabled;
    }

    public void setBountiesEnabled(boolean bountiesEnabled) {
        this.bountiesEnabled = bountiesEnabled;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Date getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Date updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }
}
