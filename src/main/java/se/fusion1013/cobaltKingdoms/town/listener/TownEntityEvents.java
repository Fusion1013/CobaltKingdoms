package se.fusion1013.cobaltKingdoms.town.listener;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import se.fusion1013.cobaltKingdoms.quest.service.QuestManager;
import se.fusion1013.cobaltKingdoms.town.util.TownUtil;

public class TownEntityEvents implements Listener {

    private static final QuestManager questManager = QuestManager.getInstance();

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Entity rightClicked = event.getRightClicked();
        if (!TownUtil.isTown(rightClicked)) return;
        
        questManager.resolveInteractAtTownEntity(event.getPlayer(), event.getRightClicked());
    }

}
