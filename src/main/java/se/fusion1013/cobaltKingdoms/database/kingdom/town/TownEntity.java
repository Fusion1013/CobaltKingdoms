package se.fusion1013.cobaltKingdoms.database.kingdom.town;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.Location;
import se.fusion1013.cobaltKingdoms.database.LocationPersister;

import java.util.UUID;

@DatabaseTable(tableName = "towns")
public class TownEntity {

    @DatabaseField(generatedId = true, columnName = "id")
    private Long id;

    @DatabaseField(columnName = "owner_id", canBeNull = false)
    private UUID ownerId;

    @DatabaseField(columnName = "name", canBeNull = false)
    private String name;

    @DatabaseField(columnName = "display_name")
    private String displayName;

    @DatabaseField(columnName = "kingdom_id", canBeNull = false)
    private UUID kingdomId;

    @DatabaseField(columnName = "location", persisterClass = LocationPersister.class)
    private Location location;

    @DatabaseField(columnName = "experience")
    private int experience;

    @DatabaseField(columnName = "appearance", foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
    private TownAppearanceEntity appearance;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<TownJailEntity> townJails;

    @ForeignCollectionField
    private ForeignCollection<TownMemberEntity> townMembers;

    public TownEntity() {
        appearance = new TownAppearanceEntity();
    }

    public Long getId() {
        return id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public UUID getKingdomId() {
        return kingdomId;
    }

    public Location getLocation() {
        return location;
    }

    public int getExperience() {
        return experience;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKingdomId(UUID kingdomId) {
        this.kingdomId = kingdomId;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public TownAppearanceEntity getAppearance() {
        if (appearance == null) {
            appearance = new TownAppearanceEntity();
        }
        return appearance;
    }

    public void setAppearance(TownAppearanceEntity appearance) {
        this.appearance = appearance;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        if (displayName == null || displayName.isEmpty()) return name;
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ForeignCollection<TownJailEntity> getJails() {
        return townJails;
    }

    public ForeignCollection<TownMemberEntity> getTownMembers() {
        return townMembers;
    }
}