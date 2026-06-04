package se.fusion1013.cobaltKingdoms.raid.util;

import org.bukkit.Location;

import java.util.Random;

public class RaidUtil {

    private static final Random random = new Random();

    public static Location getRandomLocation(Location center, int radius) {
        int xOffset = random.nextInt(-radius, radius + 1);
        int zOffset = random.nextInt(-radius, radius + 1);

        Location randomLocation = center.clone().add(xOffset, 0, zOffset);
        return randomLocation.toHighestLocation();
    }

}
