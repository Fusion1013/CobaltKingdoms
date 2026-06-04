package se.fusion1013.cobaltKingdoms.town.model;

import java.util.UUID;

public class TownMember {

    private Long id;
    private UUID playerId;
    private String playerName;
    private TownMemberRole role;
    private Long townId;

    public TownMember() {

    }

    public TownMember(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
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

    public TownMemberRole getRole() {
        return role;
    }

    public void setRole(TownMemberRole role) {
        this.role = role;
    }

    public Long getTownId() {
        return townId;
    }

    public void setTownId(Long townId) {
        this.townId = townId;
    }
}
