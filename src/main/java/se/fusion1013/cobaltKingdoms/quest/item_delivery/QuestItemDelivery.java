package se.fusion1013.cobaltKingdoms.quest.item_delivery;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownData;
import se.fusion1013.cobaltKingdoms.quest.AbstractQuest;
import se.fusion1013.cobaltKingdoms.quest.IQuest;
import se.fusion1013.cobaltKingdoms.quest.QuestType;

import java.util.List;

public class QuestItemDelivery extends AbstractQuest {

    private final int difficulty;
    private final List<ItemStack> cost;
    private final List<ItemStack> reward;

    private final TownData startTown;
    private final TownData handInTown;

    public static IQuest create(TownData startLocation, int difficulty) {
        TownData endTown = QuestItemDeliveryUtil.getRandomTown(startLocation, difficulty);

        return new QuestItemDelivery(
                difficulty,
                List.of(new ItemStack(Material.OAK_LOG, 64)),
                List.of(new ItemStack(Material.SPAWNER, 1)),
                startLocation,
                endTown);
    }

    private QuestItemDelivery(int difficulty, List<ItemStack> cost, List<ItemStack> reward, TownData startTown, TownData handInTown) {
        super(QuestType.Deliver);
        this.difficulty = difficulty;
        this.cost = cost;
        this.reward = reward;
        this.startTown = startTown;
        this.handInTown = handInTown;
    }

    @Override
    public Component getEntityName() {
        Component difficulty = Component.text("★".repeat(this.difficulty + 1)).color(NamedTextColor.GOLD);
        Component typeSymbol = Component.text(" [" + questType.symbol + "] ").color(questType.textColor);
        Component costReward = QuestItemDeliveryUtil.createCostRewardComponent(cost, reward);
        return difficulty.append(typeSymbol).append(costReward).append(typeSymbol).append(difficulty).decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public ItemStack getQuestDescriptionItem() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);

        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.title(getEntityName());
        meta.customName(getEntityName());
        meta.setAuthor("Quest");
        meta.addPages(Component.text(""));
        book.setItemMeta(meta);

        return book;
    }

    @Override
    public TownData getHandInTown() {
        return handInTown;
    }


    @Override
    public void start(Player player, Location location) {
        LivingEntity deliveryEntity = QuestItemDeliveryUtil.spawnDeliveryEntity(location, difficulty, getId());

        LocaleManager.getInstance().broadcastMessage(CobaltKingdoms.getInstance(), "kingdoms.quests.start.item_delivery", StringPlaceholders.builder()
                .addPlaceholder("player", player.getName())
                .addPlaceholder("start", startTown.townName())
                .addPlaceholder("end", handInTown.townName())
                .build());
    }

    @Override
    public boolean tryFinish(Player player, Location location) {
        // Validate entity
        Entity deliveryEntity = QuestItemDeliveryUtil.getNearbyDeliveryEntity(location, getId());
        if (deliveryEntity == null) {
            CobaltKingdoms.getInstance().getLogger().info("Quest Delivery Entity not nearby");
            return false;
        }

        // Validate items
        boolean hasRequiredItems = QuestItemDeliveryUtil.hasRequiredItems(player, cost);
        if (!hasRequiredItems) {
            CobaltKingdoms.getInstance().getLogger().info("Does not have the required items");
            return false;
        }

        CobaltKingdoms.getInstance().getLogger().info("Quest delivered!");

        deliveryEntity.remove();
        QuestItemDeliveryUtil.removeRequiredItems(player, cost);

        reward.forEach(player::give);
        return true;
    }
}
