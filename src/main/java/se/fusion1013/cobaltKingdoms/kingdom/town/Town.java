package se.fusion1013.cobaltKingdoms.kingdom.town;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltKingdoms.config.KingdomsConfig;
import se.fusion1013.cobaltKingdoms.config.town.TownConfig;
import se.fusion1013.cobaltKingdoms.config.town.TownLevelConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Town {

    private Long id;
    private UUID ownerId;
    private String name;
    private String displayName;
    private UUID kingdomId;
    private Location location;
    private int experience;
    private TownAppearance appearance;
    private List<TownJail> jails = new ArrayList<>();
    private List<TownMember> townMembers = new ArrayList<>();

    public Town() {
    }

    public Town(Player owner) {
        TownMember ownerMember = new TownMember();
        ownerMember.setPlayerId(owner.getUniqueId());
        ownerMember.setPlayerName(owner.getName());
        ownerMember.setRole(TownMemberRole.OWNER);
        townMembers.add(ownerMember);
        appearance = new TownAppearance();
    }

    public Town(Long id) {
        this.id = id;
        appearance = new TownAppearance();
    }

    public void setJails(List<TownJail> jails) {
        this.jails = jails;
    }

    public List<TownJail> getJails() {
        return jails;
    }

    public Long getId() {
        return id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public UUID getKingdomId() {
        return kingdomId;
    }

    public void setKingdomId(UUID kingdomId) {
        this.kingdomId = kingdomId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public TownAppearance getAppearance() {
        return appearance;
    }

    public void setAppearance(TownAppearance appearance) {
        this.appearance = appearance;
    }

    public List<TownMember> getTownMembers() {
        return townMembers;
    }

    public void setTownMembers(List<TownMember> townMembers) {
        this.townMembers = townMembers;
    }

    public TownLevelConfig getLevelConfig() {
        TownConfig townConfig = KingdomsConfig.getTownConfig();
        return townConfig.getTownLevelConfig(experience);
    }

    public void moveTo(Location location) {
        this.location = location;
    }
}
