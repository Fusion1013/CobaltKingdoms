package se.fusion1013.cobaltKingdoms.raid.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import se.fusion1013.cobaltKingdoms.raid.service.RaidService;
import se.fusion1013.cobaltKingdoms.raid.util.RaidConstants;

public class RaidDeathListener implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        PersistentDataContainer persistent = entity.getPersistentDataContainer();
        if (!persistent.has(RaidConstants.RAID_ENTITY_KEY)) return;
        RaidService.getInstance().raidMobDeath(entity);
    }

}
