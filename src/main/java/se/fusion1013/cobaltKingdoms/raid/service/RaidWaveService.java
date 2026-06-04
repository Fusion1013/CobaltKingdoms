package se.fusion1013.cobaltKingdoms.raid.service;

import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.manager.registry.FileLoadedRegistry;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.raid.model.RaidWaveDefinition;

public class RaidWaveService extends Manager<CobaltKingdoms> {

    private static final FileLoadedRegistry<RaidWaveDefinition> RAID_WAVES = new FileLoadedRegistry<>(
            CobaltKingdoms.getInstance(),
            "raid_waves",
            RaidWaveDefinition::new,
            RaidWaveDefinition::new,
            (p, r) -> {
            }
    );

    public RaidWaveService(CobaltKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        RAID_WAVES.reload();
    }

    @Override
    public void disable() {

    }

    private static RaidWaveService INSTANCE;

    public static RaidWaveService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RaidWaveService(CobaltKingdoms.getInstance());
        }
        return INSTANCE;
    }

    public RaidWaveDefinition getWave(String waveName) {
        return RAID_WAVES.get(waveName);
    }
}
