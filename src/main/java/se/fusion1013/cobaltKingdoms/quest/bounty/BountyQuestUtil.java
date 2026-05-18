package se.fusion1013.cobaltKingdoms.quest.bounty;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.item.ICustomItem;
import se.fusion1013.cobaltKingdoms.database.quest.bounty.IBountyRepository;

import java.util.UUID;

public class BountyQuestUtil {

    private static final IBountyRepository bountyRepository = DataManager.getInstance().getDao(IBountyRepository.class);

    public static ItemStack getBountyItem(Player target) {
        return getBountyItem(target.getUniqueId(), target.getDisplayName());
    }

    public static ItemStack getBountyItem(UUID uuid, String playerName) {
        ICustomItem customItem = CustomItemManager.getCustomItem("bounty_coin");
        if (customItem == null) return null;

        ItemStack itemStack = customItem.getItemStack();
        if (itemStack == null) return null;

        int amount = getBountyRewardMultiplier(uuid, playerName);
        itemStack.setAmount(amount);

        return itemStack;
    }

    public static int getBountyRewardMultiplier(UUID uuid, String playerName) {
        BountyPlayerStatus playerBountyStatus = bountyRepository.getPlayerBountyStatus(uuid, playerName);
        double rating = playerBountyStatus.getRating();
        return rating < 0 ? 1 : (int) (rating / 25.0) + 1;
    }

}
