package se.fusion1013.cobaltKingdoms.kingdom;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KingdomData {

    private final String name;
    private final UUID id;
    private final UUID owner;

    private List<UUID> members = new ArrayList<>();

    private String colorPrefix = "&f";

    public KingdomData(String name, UUID id, UUID owner) {
        this.name = name;
        this.id = id;
        this.owner = owner;
        members.add(owner);
    }

    public KingdomData(String colorPrefix, List<UUID> members, UUID owner, UUID id, String name) {
        this.colorPrefix = colorPrefix;
        this.members = members;
        this.owner = owner;
        this.id = id;
        this.name = name;
        members.add(owner);
    }

    public void removeMember(UUID playerId) {
        members.remove(playerId);
    }

    public void addMember(UUID playerId) {
        members.add(playerId);
    }

    public boolean isPlayerMember(UUID playerId) {
        return members.contains(playerId);
    }

    public List<UUID> getMembers() {
        return members;
    }

    public void setMembers(List<UUID> members) {
        this.members = members;
    }

    public String getColorPrefix() {
        return colorPrefix;
    }

    public void setColorPrefix(String colorPrefix) {
        this.colorPrefix = colorPrefix;
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOwner() {
        return owner;
    }
}
