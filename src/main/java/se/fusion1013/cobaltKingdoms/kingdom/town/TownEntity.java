package se.fusion1013.cobaltKingdoms.kingdom.town;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltKingdoms.config.KingdomsConfig;
import se.fusion1013.cobaltKingdoms.config.town.TownConfig;
import se.fusion1013.cobaltKingdoms.config.town.TownLevelConfig;
import se.fusion1013.cobaltKingdoms.database.quest.IQuestRepository;
import se.fusion1013.cobaltKingdoms.quest.QuestEntity;

import java.util.List;
import java.util.UUID;

@DatabaseTable(tableName = "towns")
public class TownEntity {

    @DatabaseField(id = true, columnName = "uuid")
    private String uuid;

    @DatabaseField(columnName = "owner_id", canBeNull = false)
    private String ownerId;

    @DatabaseField(columnName = "name", canBeNull = false)
    private String name;

    @DatabaseField(columnName = "kingdom_id", canBeNull = false)
    private String kingdomId;

    @DatabaseField(columnName = "center_x")
    private double centerX;

    @DatabaseField(columnName = "center_y")
    private double centerY;

    @DatabaseField(columnName = "center_z")
    private double centerZ;

    @DatabaseField(columnName = "world_id", canBeNull = false)
    private String worldId;

    @DatabaseField(columnName = "experience")
    private int experience;

    public TownEntity() {
    }

    public TownEntity(String ownerId, String name, String kingdomId, double centerX, double centerY, double centerZ, String worldId) {
        this.uuid = UUID.randomUUID().toString();
        this.ownerId = ownerId;
        this.name = name;
        this.kingdomId = kingdomId;
        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;
        this.worldId = worldId;
    }

    public TownEntity(String uuid, String ownerId, String name, String kingdomId, double centerX, double centerY, double centerZ, String worldId) {
        this.uuid = uuid;
        this.ownerId = ownerId;
        this.name = name;
        this.kingdomId = kingdomId;
        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;
        this.worldId = worldId;
    }

    public TownEntity(String townName, UUID kingdomId, @NotNull UUID playerId, Location location) {
        this.uuid = UUID.randomUUID().toString();
        this.name = townName;
        this.kingdomId = kingdomId.toString();
        this.ownerId = playerId.toString();
        this.centerX = location.x();
        this.centerY = location.y();
        this.centerZ = location.z();
        this.worldId = location.getWorld().getUID().toString();
    }

    public void moveTo(Location location) {
        this.centerX = location.getX();
        this.centerY = location.getY();
        this.centerZ = location.getZ();
        this.worldId = location.getWorld().getUID().toString();
    }

    public UUID getUuid() {
        return UUID.fromString(uuid);
    }

    public UUID getOwnerId() {
        return UUID.fromString(ownerId);
    }

    public String getName() {
        return name;
    }

    public UUID getKingdomId() {
        return UUID.fromString(kingdomId);
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public double getCenterZ() {
        return centerZ;
    }

    public UUID getWorldId() {
        return UUID.fromString(worldId);
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(UUID.fromString(worldId)), centerX, centerY, centerZ);
    }

    public int getExperience() {
        return experience;
    }

    public List<QuestEntity> getQuests() {
        return DataManager.getInstance().getDao(IQuestRepository.class).getQuests(this);
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKingdomId(String kingdomId) {
        this.kingdomId = kingdomId;
    }

    public void setCenterX(double centerX) {
        this.centerX = centerX;
    }

    public void setCenterY(double centerY) {
        this.centerY = centerY;
    }

    public void setCenterZ(double centerZ) {
        this.centerZ = centerZ;
    }

    public void setWorldId(String worldId) {
        this.worldId = worldId;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public TownLevelConfig getLevelConfig() {
        TownConfig townConfig = KingdomsConfig.getTownConfig();
        return townConfig.getTownLevelConfig(experience);
    }
}