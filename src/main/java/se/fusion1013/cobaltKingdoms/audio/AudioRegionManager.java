package se.fusion1013.cobaltKingdoms.audio;

import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.*;

public class AudioRegionManager {

    private final Map<UUID, PlayerSoundState> states = new HashMap<>();
    private final Random random = new Random();

    public void tickRegion(
            Location corner1,
            Location corner2,
            Player player,
            List<IAmbientSound> sounds
    ) {
        UUID uuid = player.getUniqueId();

        PlayerSoundState state = states.computeIfAbsent(
                uuid,
                id -> new PlayerSoundState()
        );

        boolean inside = isInside(player.getLocation(), corner1, corner2);
        long now = System.currentTimeMillis();

        /*
         * Player just entered region.
         */
        if (inside && !state.wasInside) {

            // Only start a sound if nothing is currently playing.
            if (now >= state.soundEndTimeMillis) {
                playRandomSound(player, state, sounds);
            }
        }

        /*
         * Sound finished.
         */
        if (inside && now >= state.soundEndTimeMillis) {
            playRandomSound(player, state, sounds);
        }

        state.wasInside = inside;
    }

    private void playRandomSound(
            Player player,
            PlayerSoundState state,
            List<IAmbientSound> sounds
    ) {
        if (sounds.isEmpty()) {
            return;
        }

        IAmbientSound sound =
                sounds.get(random.nextInt(sounds.size()));

        player.playSound(
                player,
                sound.getKey(),
                SoundCategory.AMBIENT,
                1.0f,
                1.0f
        );

        long overlapMillis = sound.getOverlapTicks() * 50L;

        state.soundEndTimeMillis =
                System.currentTimeMillis()
                        + sound.getLengthTicks() * 50L
                        - overlapMillis;

        state.currentSound = sound;
    }

    private boolean isInside(
            Location loc,
            Location c1,
            Location c2
    ) {
        double minX = Math.min(c1.getX(), c2.getX());
        double maxX = Math.max(c1.getX(), c2.getX());

        double minY = Math.min(c1.getY(), c2.getY());
        double maxY = Math.max(c1.getY(), c2.getY());

        double minZ = Math.min(c1.getZ(), c2.getZ());
        double maxZ = Math.max(c1.getZ(), c2.getZ());

        return loc.getX() >= minX
                && loc.getX() <= maxX
                && loc.getY() >= minY
                && loc.getY() <= maxY
                && loc.getZ() >= minZ
                && loc.getZ() <= maxZ;
    }

    private static final class PlayerSoundState {

        private boolean wasInside;

        private long soundEndTimeMillis;

        private IAmbientSound currentSound;
    }

}
