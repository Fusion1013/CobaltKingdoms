package se.fusion1013.cobaltKingdoms.kingdom.town;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "town_members")
public class TownMemberEntity {

    @DatabaseField(generatedId = true, columnName = "id")
    private Long id;

    @DatabaseField(columnName = "player_uuid")
    private UUID playerUuid;

    @DatabaseField(columnName = "player_name")
    private String playerName;

    @DatabaseField(columnName = "town", foreign = true, foreignAutoRefresh = true)
    private TownEntity town;

    @DatabaseField(columnName = "role")
    private TownMemberRole role;

    public TownMemberEntity() {
    }

    public Long getId() {
        return id;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public void setPlayerUuid(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public TownEntity getTown() {
        return town;
    }

    public void setTown(TownEntity town) {
        this.town = town;
    }

    public TownMemberRole getRole() {
        return role;
    }

    public void setRole(TownMemberRole role) {
        this.role = role;
    }
}
