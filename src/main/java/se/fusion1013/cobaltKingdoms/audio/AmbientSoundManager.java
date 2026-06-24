package se.fusion1013.cobaltKingdoms.audio;

import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.manager.registry.FileLoadedRegistry;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

import java.util.Arrays;
import java.util.List;

public class AmbientSoundManager extends Manager<CobaltKingdoms> {

    private static final FileLoadedRegistry<IAmbientSound> AMBIENT_SOUNDS = new FileLoadedRegistry<>(
            CobaltKingdoms.getInstance(),
            "ambient_sounds",
            AmbientSound::new,
            AmbientSound::new,
            (p, e) -> {
            }
    );

    private static final FileLoadedRegistry<IAmbientSoundCollection> AMBIENT_SOUND_COLLECTIONS = new FileLoadedRegistry<>(
            CobaltKingdoms.getInstance(),
            "ambient_sound_collections",
            AmbientSoundCollection::new,
            AmbientSoundCollection::new,
            (p, e) -> {
            }
    );

    public AmbientSoundManager(CobaltKingdoms plugin) {
        super(plugin);
    }

    public static List<IAmbientSound> fromString(String value) {
        String[] split = value.split(",");
        return Arrays.stream(split).map(AMBIENT_SOUNDS::get).toList();
    }

    public static String[] getAmbientSoundCollectionNames() {
        return AMBIENT_SOUND_COLLECTIONS.getNames();
    }

    public static IAmbientSoundCollection getAmbientSoundCollection(String collection) {
        return AMBIENT_SOUND_COLLECTIONS.get(collection);
    }

    @Override
    public void reload() {
        AMBIENT_SOUNDS.reload();
        AMBIENT_SOUND_COLLECTIONS.reload();
    }

    @Override
    public void disable() {

    }
}
