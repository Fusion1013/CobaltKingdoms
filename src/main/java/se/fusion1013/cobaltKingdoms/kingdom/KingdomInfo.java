package se.fusion1013.cobaltKingdoms.kingdom;

import java.util.List;
import java.util.UUID;

public record KingdomInfo(String name, UUID owner, List<UUID> members) {
}
