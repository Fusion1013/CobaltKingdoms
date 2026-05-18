package se.fusion1013.cobaltKingdoms.kingdom.town;

import org.bukkit.Location;

public class TownJail {

    private Long id;
    private String name;
    private Location location;

    public TownJail() {

    }

    public TownJail(Long id) {
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
