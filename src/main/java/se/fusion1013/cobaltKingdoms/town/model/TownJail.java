package se.fusion1013.cobaltKingdoms.town.model;

import org.bukkit.Location;

public class TownJail {

    private Long id;
    private String name;
    private Location location;
    private Long townId;

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

    public Long getTownId() {
        return townId;
    }

    public void setTownId(Long townId) {
        this.townId = townId;
    }
}
