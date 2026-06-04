package se.fusion1013.cobaltKingdoms.raid.service;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.manager.registry.FileLoadedRegistry;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.raid.model.ActiveRaid;
import se.fusion1013.cobaltKingdoms.raid.model.RaidDefinition;
import se.fusion1013.cobaltKingdoms.raid.model.RaidState;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RaidService extends Manager<CobaltKingdoms> {

    private static final FileLoadedRegistry<RaidDefinition> RAID_DEFINITIONS = new FileLoadedRegistry<RaidDefinition>(
            CobaltKingdoms.getInstance(),
            "raid",
            RaidDefinition::new,
            RaidDefinition::new,
            (p, r) -> {
            }
    );

    private static final Map<UUID, ActiveRaid> ACTIVE_RAIDS = new HashMap<>();
    private static final Map<UUID, ActiveRaid> ACTIVE_RAID_BY_ENTITY_ID = new HashMap<>();


    public Response startRaid(String raidName, Location location) {
        RaidDefinition raidDefinition = RAID_DEFINITIONS.get(raidName);
        if (raidDefinition == null) return Response.error("Could not find raid definition");

        ActiveRaid activeRaid = new ActiveRaid(raidDefinition, location);
        ACTIVE_RAIDS.put(activeRaid.getId(), activeRaid);

        scheduleNextWave(activeRaid, activeRaid.getWaveDelay());

        return Response.ok("Spawned new raid");
    }


    public void raidMobDeath(LivingEntity entity) {
        UUID entityId = entity.getUniqueId();
        ActiveRaid raid = ACTIVE_RAID_BY_ENTITY_ID.get(entityId);
        if (raid == null) return;

        CobaltKingdoms.getInstance().getLogger().info("Raid entity death");

        raid.removeRaidEntity(entityId);
        ACTIVE_RAID_BY_ENTITY_ID.remove(entityId);

        if (raid.getState() == RaidState.BETWEEN_WAVES) scheduleNextWave(raid, raid.getWaveDelay());
        if (raid.getState() == RaidState.COMPLETED) scheduleReward(raid, raid.getWaveDelay());
    }

    private void scheduleNextWave(ActiveRaid raid, int delaySeconds) {
        Bukkit.getScheduler().runTaskLater(CobaltKingdoms.getInstance(), () -> {
            Set<UUID> spawnedMobs = raid.nextWave();
            for (UUID uuid : spawnedMobs) {
                ACTIVE_RAID_BY_ENTITY_ID.put(uuid, raid);
            }
        }, delaySeconds * 20L);
    }

    private void scheduleReward(ActiveRaid raid, int delaySeconds) {
        Bukkit.getScheduler().runTaskLater(CobaltKingdoms.getInstance(), raid::spawnRewards, delaySeconds * 20L);
    }

    public RaidService(CobaltKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        RAID_DEFINITIONS.reload();
    }

    @Override
    public void disable() {

    }


    private static RaidService INSTANCE;

    public static RaidService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RaidService(CobaltKingdoms.getInstance());
        }
        return INSTANCE;
    }

    public String[] getRaidDefinitionNames() {
        return RAID_DEFINITIONS.getNames();
    }
}
