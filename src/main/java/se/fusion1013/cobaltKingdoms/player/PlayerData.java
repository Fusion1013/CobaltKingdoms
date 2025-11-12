package se.fusion1013.cobaltKingdoms.player;

public class PlayerData {

    private String colorPrefix;
    private PlayerStatus playerStatus;

    public PlayerData(String colorPrefix, PlayerStatus playerStatus) {
        this.colorPrefix = colorPrefix;
        this.playerStatus = playerStatus;
    }

    public String getColorPrefix() {
        return colorPrefix;
    }

    public void setColorPrefix(String colorPrefix) {
        this.colorPrefix = colorPrefix;
    }

    public PlayerStatus getPlayerStatus() {
        return playerStatus;
    }

    public void setPlayerStatus(PlayerStatus playerStatus) {
        this.playerStatus = playerStatus;
    }
}
