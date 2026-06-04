package se.fusion1013.cobaltKingdoms.town.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.Location;
import se.fusion1013.cobaltKingdoms.database.LocationPersister;

@DatabaseTable(tableName = "town_jails")
public class TownJailEntity {

    @DatabaseField(generatedId = true, columnName = "id")
    private Long id;

    @DatabaseField(columnName = "name")
    private String name;

    @DatabaseField(columnName = "town", foreign = true, foreignAutoRefresh = true)
    private TownEntity town;

    @DatabaseField(columnName = "location", persisterClass = LocationPersister.class)
    private Location location;

    public TownJailEntity() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TownEntity getTown() {
        return town;
    }

    public void setTown(TownEntity town) {
        this.town = town;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
