package se.fusion1013.cobaltKingdoms.quest.artifact_hunt;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.Location;
import se.fusion1013.cobaltKingdoms.database.LocationPersister;

@DatabaseTable(tableName = "quest_artifact_hunt_goal")
public class ArtifactHuntQuestGoalEntity {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(columnName = "name")
    private String name;

    @DatabaseField(columnName = "difficulty")
    private int difficulty;

    @DatabaseField(columnName = "location", persisterClass = LocationPersister.class)
    private Location location;

    @DatabaseField(columnName = "item_name")
    private String itemName;

    @DatabaseField(columnName = "description")
    private String description;

    public ArtifactHuntQuestGoalEntity() {
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
