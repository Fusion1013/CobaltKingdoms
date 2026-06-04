package se.fusion1013.cobaltKingdoms.quest.listener;

import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.HexUtils;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.quest.service.QuestManager;

public class ArtifactPickupListener implements Listener {

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        Item item = event.getItem();
        ItemStack itemStack = item.getItemStack();

        if (!itemStack.getPersistentDataContainer().has(QuestManager.QUEST_ID_KEY)) return;

        LivingEntity entity = event.getEntity();
        LocaleManager.getInstance().broadcastMessage(CobaltKingdoms.getInstance(), "kingdoms.quests.artifact_hunt.pickup", StringPlaceholders.builder()
                .addPlaceholder("entity", entity.getName())
                .addPlaceholder("artifact", HexUtils.stripColorCodes(itemStack.getItemMeta().getDisplayName()))
                .build());
    }

}
