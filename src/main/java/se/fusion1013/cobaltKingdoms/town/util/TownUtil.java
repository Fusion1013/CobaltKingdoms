package se.fusion1013.cobaltKingdoms.town.util;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.quest.service.QuestManager;
import se.fusion1013.cobaltKingdoms.town.model.Town;

import java.util.Collection;

public class TownUtil {

    public static final NamespacedKey TOWN_ENTITY_KEY = new NamespacedKey(CobaltKingdoms.getInstance(), "town_entity");

    public static boolean isTown(Entity entity) {
        return entity.getPersistentDataContainer().has(TownUtil.TOWN_ENTITY_KEY);
    }

    public static boolean isTown(Entity entity, Long townId) {
        if (!entity.getPersistentDataContainer().has(TOWN_ENTITY_KEY)) return false;
        Long keyValue = entity.getPersistentDataContainer().get(TOWN_ENTITY_KEY, PersistentDataType.LONG);
        if (keyValue == null) return false;
        return keyValue.equals(townId);
    }

    public static void removeTownEntities(Town town) {
        Location location = town.getLocation();
        World world = location.getWorld();

        Collection<Entity> entities = world.getNearbyEntities(location, 16, 16, 16, entity -> entity.getPersistentDataContainer().has(TOWN_ENTITY_KEY) || entity.getPersistentDataContainer().has(QuestManager.QUEST_GIVER_ID_KEY));
        for (Entity entity : entities) {
            entity.remove();
        }
    }

    public static Long getTownId(Entity entity) {
        if (!entity.getPersistentDataContainer().has(TOWN_ENTITY_KEY)) return null;
        return entity.getPersistentDataContainer().get(TOWN_ENTITY_KEY, PersistentDataType.LONG);
    }
}
