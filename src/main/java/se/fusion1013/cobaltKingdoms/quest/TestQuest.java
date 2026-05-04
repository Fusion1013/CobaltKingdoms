package se.fusion1013.cobaltKingdoms.quest;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownData;

import java.util.Random;
import java.util.UUID;

public class TestQuest implements IQuest {

    private static final Random random = new Random();

    private final UUID id;
    private final Long createdTimestamp;
    private final QuestType questType;

    public TestQuest() {
        this.id = UUID.randomUUID();
        this.createdTimestamp = System.currentTimeMillis();
        questType = QuestType.values()[random.nextInt(QuestType.values().length)];
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public Component getEntityName() {
        Component typeSymbol = Component.text(" [" + questType.symbol + "] ").color(questType.textColor);
        Component costReward = Component.text("64x Oak Log -> 1x Spawner").color(NamedTextColor.GRAY);

        return typeSymbol.append(costReward).append(typeSymbol);
    }

    @Override
    public QuestType getQuestType() {
        return questType;
    }

    @Override
    public Long getCreatedTimestamp() {
        return createdTimestamp;
    }

    @Override
    public ItemStack getQuestToken() {
        ItemStack itemStack = new ItemStack(Material.CLOCK);

        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(Component.text("Quest Token").color(NamedTextColor.GOLD));
        meta.setItemModel(new NamespacedKey("thegreatwork", "quest/gold_quest_token"));
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    @Override
    public ItemStack getQuestDescriptionItem() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);

        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.title(getEntityName());
        meta.customName(getEntityName());
        meta.setAuthor("Quest");
        meta.addPages(Component.text("Some quest description here"));
        book.setItemMeta(meta);

        return book;
    }

    @Override
    public TownData getHandInTown() {
        return null;
    }

    @Override
    public void start(Player player, Location location) {
        
    }

    @Override
    public boolean tryFinish(Player player, Location location) {
        return false;
    }
}
