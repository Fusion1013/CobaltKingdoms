package se.fusion1013.cobaltKingdoms.player;

import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.util.HexUtils;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomInfo;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomManager;
import se.fusion1013.cobaltKingdoms.player.status.PlayerStatus;

public class PlayerData {

    // ##%%##%%## VARIABLES ##%%##%%## //

    private String playerNameColorPrefix;
    private PlayerStatus playerStatus;

    // ##%%##%%## CONSTRUCTOR ##%%##%%## //

    public PlayerData(String playerNameColorPrefix, PlayerStatus playerStatus) {
        this.playerNameColorPrefix = playerNameColorPrefix;
        this.playerStatus = playerStatus;
    }

    // ##%%##%%## GETTERS / SETTERS ##%%##%%## //

    /**
     * Sets the player list name following this template:
     * <p>
     * [ROLE] [STATUS] NAME
     * <p>
     * Example: [A] [IC] Fusion1013
     *
     * @param player player.
     * @return player name string.
     */
    public String createPlayerListName(Player player) {

        KingdomInfo kingdomInfo = KingdomManager.getInstance()
                .getPlayerKingdomInfo(player.getUniqueId());
        boolean isKingdomLeader = kingdomInfo != null && kingdomInfo.owner().equals(player.getUniqueId());

        String rolePrefix = "";
        if (player.isOp()) {
            rolePrefix = "&7[#2991F2A&7] ";
        } else if (isKingdomLeader) {
            rolePrefix = "&7[#DE3A3AL&7] ";
        } else {
            rolePrefix = "&7[#EDDD53V&7] ";
        }

        String playerName = getColorPrefix() + player.getName();
        String statusString = "&7[&3" + playerStatus.colorPrefix + playerStatus.prefix + "&7] ";
        return HexUtils.colorify(rolePrefix + statusString + playerName);
    }

    public String getColorPrefix() {
        return playerNameColorPrefix;
    }

    public void setColorPrefix(String colorPrefix) {
        this.playerNameColorPrefix = colorPrefix;
    }

    public PlayerStatus getPlayerStatus() {
        return playerStatus;
    }

    public void setPlayerStatus(PlayerStatus playerStatus) {
        this.playerStatus = playerStatus;
    }
}
