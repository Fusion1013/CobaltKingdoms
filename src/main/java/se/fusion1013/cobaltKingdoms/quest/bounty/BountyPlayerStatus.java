package se.fusion1013.cobaltKingdoms.quest.bounty;

import java.util.Date;
import java.util.UUID;

public class BountyPlayerStatus {

    private static final double CompletedWeight = 4.0;
    private static final double FailedWeight = 2.25;
    private static final double EvadedWeight = 0.85;
    private static final double KilledWeight = 4.0;

    private Long id;
    private UUID playerId;
    private String playerName;

    private int completed;
    private int failed;
    private int evaded;
    private int killed;

    private boolean bountiesEnabled;

    private double rating;

    private Date updateTimestamp;

    public BountyPlayerStatus() {

    }

    public void incrementFailed() {
        failed++;
        calculateRating();
    }

    public void incrementEvaded() {
        evaded++;
        calculateRating();
    }

    public void incrementCompleted() {
        completed++;
        calculateRating();
    }

    public void incrementKilled() {
        killed++;
        calculateRating();
    }

    public BountyPlayerStatus(Long id) {
        this.id = id;
    }

    private void calculateRating() {
        this.rating = CompletedWeight * completed
                - FailedWeight * failed
                + EvadedWeight * evaded
                - KilledWeight * killed;
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
