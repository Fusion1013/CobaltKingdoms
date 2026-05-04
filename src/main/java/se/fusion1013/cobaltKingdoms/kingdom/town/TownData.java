package se.fusion1013.cobaltKingdoms.kingdom.town;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Objects;
import java.util.UUID;

public final class TownData {
    private final UUID uuid;
    private final UUID ownerUuid;
    private final String townName;
    private final UUID kingdomId;
    private double xCenter;
    private double yCenter;
    private double zCenter;
    private UUID worldUuid;

    public TownData(UUID uuid, UUID ownerUuid, String townName, UUID kingdomId, double xCenter, double yCenter,
                    double zCenter, UUID worldUuid) {
        this.uuid = uuid;
        this.ownerUuid = ownerUuid;
        this.townName = townName;
        this.kingdomId = kingdomId;
        this.xCenter = xCenter;
        this.yCenter = yCenter;
        this.zCenter = zCenter;
        this.worldUuid = worldUuid;
    }

    public TownData(String townName, UUID kingdomId, UUID ownerUuid, Location center) {
        this(UUID.randomUUID(), ownerUuid, townName, kingdomId, center.getX(), center.getY(), center.getZ(), center.getWorld().getUID());
    }

    public void moveTo(Location location) {
        this.xCenter = location.getX();
        this.yCenter = location.getY();
        this.zCenter = location.getZ();
        this.worldUuid = location.getWorld().getUID();
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(worldUuid()), xCenter(), yCenter(), zCenter());
    }

    public UUID uuid() {
        return uuid;
    }

    public UUID ownerUuid() {
        return ownerUuid;
    }

    public String townName() {
        return townName;
    }

    public UUID kingdomId() {
        return kingdomId;
    }

    public double xCenter() {
        return xCenter;
    }

    public double yCenter() {
        return yCenter;
    }

    public double zCenter() {
        return zCenter;
    }

    public UUID worldUuid() {
        return worldUuid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (TownData) obj;
        return Objects.equals(this.uuid, that.uuid) &&
                Objects.equals(this.ownerUuid, that.ownerUuid) &&
                Objects.equals(this.townName, that.townName) &&
                Objects.equals(this.kingdomId, that.kingdomId) &&
                Double.doubleToLongBits(this.xCenter) == Double.doubleToLongBits(that.xCenter) &&
                Double.doubleToLongBits(this.yCenter) == Double.doubleToLongBits(that.yCenter) &&
                Double.doubleToLongBits(this.zCenter) == Double.doubleToLongBits(that.zCenter) &&
                Objects.equals(this.worldUuid, that.worldUuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, ownerUuid, townName, kingdomId, xCenter, yCenter, zCenter, worldUuid);
    }

    @Override
    public String toString() {
        return "TownData[" +
                "uuid=" + uuid + ", " +
                "ownerUuid=" + ownerUuid + ", " +
                "townName=" + townName + ", " +
                "kingdomId=" + kingdomId + ", " +
                "xCenter=" + xCenter + ", " +
                "yCenter=" + yCenter + ", " +
                "zCenter=" + zCenter + ", " +
                "worldUuid=" + worldUuid + ']';
    }


}
