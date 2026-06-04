package se.fusion1013.cobaltKingdoms.kingdom.model;

import java.util.List;
import java.util.UUID;

public record KingdomInfo(String name, UUID owner, List<UUID> members) {
}
