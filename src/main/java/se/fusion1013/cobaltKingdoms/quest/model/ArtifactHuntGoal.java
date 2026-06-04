package se.fusion1013.cobaltKingdoms.quest.model;

import org.bukkit.Location;

public class ArtifactHuntGoal {

    private Long id;
    private String name;
    private int difficulty;
    private Location location;
    private String itemName;
    private String description;

    public ArtifactHuntGoal() {
    }

    public ArtifactHuntGoal(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
