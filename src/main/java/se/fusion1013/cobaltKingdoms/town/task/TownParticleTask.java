package se.fusion1013.cobaltKingdoms.town.task;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltKingdoms.town.repository.ITownRepository;

public class TownParticleTask implements Runnable {

    private static final DataManager dataManager = DataManager.getInstance();
    private static final ITownRepository townRepository = dataManager.getDao(ITownRepository.class);

    @Override
    public void run() {
        displayTownParticles();
    }

    private void displayTownParticles() {
        townRepository.getTowns().forEach(town -> {
            Location location = town.getLocation();
            World world = location.getWorld();
            world.spawnParticle(Particle.END_ROD, location, 3, .2, .2, .2, 0);

            townRepository.getJails(town.getId()).forEach(jail -> {
                Location jailLocation = jail.getLocation();
                world.spawnParticle(Particle.CRIT, jailLocation, 3, .2, .2, .2, 0);
            });
        });
    }

}
