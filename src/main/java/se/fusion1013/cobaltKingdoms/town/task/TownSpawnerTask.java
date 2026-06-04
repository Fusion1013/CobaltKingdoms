package se.fusion1013.cobaltKingdoms.town.task;

import com.destroystokyo.paper.profile.ProfileProperty;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import io.papermc.paper.entity.LookAnchor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltKingdoms.config.KingdomsConfig;
import se.fusion1013.cobaltKingdoms.town.config.TownConfig;
import se.fusion1013.cobaltKingdoms.town.model.Town;
import se.fusion1013.cobaltKingdoms.town.repository.ITownRepository;
import se.fusion1013.cobaltKingdoms.town.service.TownManager;
import se.fusion1013.cobaltKingdoms.town.util.TownUtil;

import java.util.Collection;
import java.util.Optional;

import static se.fusion1013.cobaltKingdoms.town.util.TownUtil.TOWN_ENTITY_KEY;

public class TownSpawnerTask implements Runnable {

    private static final DataManager dataManager = DataManager.getInstance();
    private static final ITownRepository townRepository = dataManager.getDao(ITownRepository.class);
    private static final TownConfig townConfig = KingdomsConfig.getTownConfig();

    @Override
    public void run() {
        tickTownDisplay();
    }

    private void tickTownDisplay() {
        townRepository.getTowns().forEach(town -> {
            if (isTownLoaded(town)) {
                spawnTownEntity(town);
            } else {
                TownUtil.removeTownEntities(town);
            }
        });
    }

    private void spawnTownEntity(Town town) {
        Location location = town.getLocation();
        World world = location.getWorld();

        Collection<Mannequin> nearbyEntitiesByType = world.getNearbyEntitiesByType(Mannequin.class, location, 5, v -> v.getPersistentDataContainer().has(TOWN_ENTITY_KEY));
        if (!nearbyEntitiesByType.isEmpty()) {
            for (Mannequin mannequin : nearbyEntitiesByType) {
                updateTownEntity(mannequin);
            }
            return;
        }

        String skin = town.getAppearance() == null ? "" : town.getAppearance().getSkin() == null ? "" : town.getAppearance().getSkin();

        world.spawn(location, Mannequin.class, mannequin -> {
            mannequin.setInvulnerable(true);
            mannequin.setAI(false);
            mannequin.getPersistentDataContainer().set(TOWN_ENTITY_KEY, PersistentDataType.LONG, town.getId());

            String texture = town.getAppearance().getTexture();
            if (texture == null || texture.isEmpty()) {
                mannequin.setProfile(ResolvableProfile.resolvableProfile().name(skin).build());
            } else {
                mannequin.setProfile(ResolvableProfile.resolvableProfile().addProperty(new ProfileProperty("textures", texture)).build());
            }

            mannequin.customName(Component.text(town.getDisplayName()));
            mannequin.setImmovable(true);
            mannequin.setDescription(Component.text("Town"));
        });

        TownManager.getInstance().onTownSpawn(town);

    }

    private void updateTownEntity(Mannequin mannequin) {
        Location location = mannequin.getLocation();
        World world = location.getWorld();
        Collection<Player> nearbyPlayers = world.getNearbyPlayers(location, 12);
        if (nearbyPlayers.isEmpty()) {
            mannequin.setGlowing(false);
            return;
        } else {
            mannequin.setGlowing(true);
        }

        Optional<Player> first = nearbyPlayers.stream().findFirst();
        Player player = first.get();

        mannequin.lookAt(player.getEyeLocation(), LookAnchor.EYES);
    }

    private boolean isTownLoaded(Town town) {
        double closestPlayerDistance = Double.MAX_VALUE;
        for (Player p : Bukkit.getOnlinePlayers()) {
            double distance = p.getLocation().distanceSquared(town.getLocation());
            if (distance < closestPlayerDistance) closestPlayerDistance = distance;
        }
        return closestPlayerDistance < townConfig.getTownSpawnDistance() * townConfig.getTownSpawnDistance();
    }

}
