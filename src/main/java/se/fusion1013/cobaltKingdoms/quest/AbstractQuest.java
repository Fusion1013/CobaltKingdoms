package se.fusion1013.cobaltKingdoms.quest;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public abstract class AbstractQuest implements IQuest {

    protected static final Random random = new Random();

    private final UUID id;
    private final Long createdTimestamp;
    protected final QuestType questType;

    public AbstractQuest(QuestType questType) {
        id = UUID.randomUUID();
        createdTimestamp = System.currentTimeMillis();
        this.questType = questType;
    }

    @Override
    public ItemStack getQuestToken() {
        ItemStack itemStack = new ItemStack(Material.CLOCK);
        ItemMeta meta = itemStack.getItemMeta();

        meta.displayName(Component.text("Quest Token").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false));
        meta.setItemModel(new NamespacedKey("thegreatwork", "quest/gold_quest_token"));
        meta.lore(
                List.of(
                        Component.text("Deliver to " + getHandInTown().townName()).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                )
        );
        meta.getPersistentDataContainer().set(QuestManager.QUEST_ID_KEY, PersistentDataType.STRING, id.toString());

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public Long getCreatedTimestamp() {
        return createdTimestamp;
    }

    @Override
    public QuestType getQuestType() {
        return questType;
    }
}
