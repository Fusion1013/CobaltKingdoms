package se.fusion1013.cobaltKingdoms.raid.model;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

import java.util.Set;
import java.util.UUID;

import static se.fusion1013.cobaltKingdoms.quest.util.QuestUtil.spawnRandomFirework;

public class ActiveRaid {

    private final UUID id;
    private final RaidDefinition definition;
    private final Location center;
    private int currentWaveIndex;
    private ActiveWave activeWave;
    private RaidState state;

    public ActiveRaid(RaidDefinition definition, Location center) {
        this.id = UUID.randomUUID();
        this.definition = definition;
        this.center = center;
        this.currentWaveIndex = -1;
    }

    public Set<UUID> nextWave() {
        currentWaveIndex++;
        CobaltKingdoms.getInstance().getLogger().info("Spawning wave " + currentWaveIndex);

        RaidWaveDefinition waveDefinition = definition.getWave(currentWaveIndex);

        // Spawn wave
        activeWave = new ActiveWave(waveDefinition, currentWaveIndex);
        Set<UUID> spawnedEntities = activeWave.spawn(center);

        // Play sounds
        World world = center.getWorld();
        world.playSound(center, Sound.EVENT_RAID_HORN, 10, 1);

        state = RaidState.IN_PROGRESS;

        return spawnedEntities;
    }

    public void spawnRewards() {
        CobaltKingdoms.getInstance().getLogger().info("You win the raid!");
        for (int i = 0; i < 5; i++) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    spawnRandomFirework(center);
                }
            }.runTaskLater(CobaltKingdoms.getInstance(), i * 4L);
        }
    }

    public void removeRaidEntity(UUID entityId) {
        activeWave.removeRaidEntity(entityId);
        if (!activeWave.isCompleted()) return;

        if (definition.getWave(currentWaveIndex + 1) == null) {
            state = RaidState.COMPLETED;
        } else {
            state = RaidState.BETWEEN_WAVES;
        }
    }

    public UUID getId() {
        return id;
    }

    public RaidState getState() {
        return state;
    }

    public int getWaveDelay() {
        return 15;
    }
}
