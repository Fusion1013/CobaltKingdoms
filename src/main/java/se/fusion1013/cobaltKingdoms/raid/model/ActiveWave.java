package se.fusion1013.cobaltKingdoms.raid.model;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.cobaltCore.entity.CustomEntityManager;
import se.fusion1013.cobaltCore.entity.ICustomEntity;
import se.fusion1013.cobaltCore.entity.ICustomEntityInstance;
import se.fusion1013.cobaltKingdoms.raid.util.RaidConstants;
import se.fusion1013.cobaltKingdoms.raid.util.RaidUtil;

import java.util.*;

public class ActiveWave {

    public static final Random random = new Random();

    private final RaidWaveDefinition waveDefinition;
    private final int waveNumber;
    private final Set<UUID> aliveMobs = new HashSet<>();
    private boolean completed;

    public ActiveWave(RaidWaveDefinition waveDefinition, int waveNumber) {
        this.waveDefinition = waveDefinition;
        this.waveNumber = waveNumber;
    }

    public Set<UUID> spawn(Location center) {
        int spawnRadius = waveDefinition.getSpawnRadius();
        List<RaidGroupDefinition> raidGroupDefinitions = waveDefinition.getRaidGroupDefinitions();
        Set<UUID> spawnedEntityIds = new HashSet<>();

        for (int i = 0; i < waveDefinition.getSpawnAttempts(); i++) {
            Location spawnLocation = RaidUtil.getRandomLocation(center, spawnRadius);
            RaidGroupDefinition raidGroupDefinition = raidGroupDefinitions.get(random.nextInt(raidGroupDefinitions.size()));
            spawnEntityGroup(spawnLocation, raidGroupDefinition, spawnedEntityIds);
        }

        aliveMobs.addAll(spawnedEntityIds);

        return spawnedEntityIds;
    }

    private static void spawnEntityGroup(Location location, RaidGroupDefinition group, Set<UUID> spawnedEntityIds) {
        String entityName = group.getEntityName();
        ICustomEntity entityType = CustomEntityManager.getEntityType(entityName);
        if (entityType == null) return;

        Location spawnLocation = RaidUtil.getRandomLocation(location, 1);

        int spawnAmount = group.getRandomAmount();

        for (int i = 0; i < spawnAmount; i++) {
            ICustomEntityInstance entity = entityType.spawn(spawnLocation.getWorld(), spawnLocation);
            Entity spawnedEntity = entity.entity();
            spawnedEntity.getPersistentDataContainer().set(RaidConstants.RAID_ENTITY_KEY, PersistentDataType.BOOLEAN, true);
            spawnedEntity.setGlowing(true);

            UUID uniqueId = spawnedEntity.getUniqueId();
            spawnedEntityIds.add(uniqueId);
        }
    }

    public void removeRaidEntity(UUID entityId) {
        aliveMobs.remove(entityId);
        if (aliveMobs.isEmpty()) completed = true;
    }

    public boolean isCompleted() {
        return completed;
    }
}
