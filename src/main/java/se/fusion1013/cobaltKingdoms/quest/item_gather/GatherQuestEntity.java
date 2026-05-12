package se.fusion1013.cobaltKingdoms.quest.item_gather;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.item.ICustomItem;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.HexUtils;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.config.KingdomsConfig;
import se.fusion1013.cobaltKingdoms.config.quest.QuestConfig;
import se.fusion1013.cobaltKingdoms.config.town.TownConfig;
import se.fusion1013.cobaltKingdoms.config.town.TownLevelConfig;
import se.fusion1013.cobaltKingdoms.database.ItemStackListPersister;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownEntity;
import se.fusion1013.cobaltKingdoms.quest.*;
import se.fusion1013.cobaltKingdoms.util.ItemStackList;
import se.fusion1013.cobaltKingdoms.util.LargeItemStack;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static se.fusion1013.cobaltKingdoms.quest.QuestUtil.*;

@DatabaseTable(tableName = "quests_gather")
public class GatherQuestEntity implements IQuestData {

    @DatabaseField(generatedId = true, columnName = "id")
    private Long id;

    @DatabaseField(columnName = "rewards", persisterClass = ItemStackListPersister.class)
    private ItemStackList rewards;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private QuestEntity quest;

    @DatabaseField(foreign = true, foreignAutoCreate = false, foreignAutoRefresh = true)
    private GatherQuestGoalEntity goal;

    public GatherQuestEntity() {
    }

    public static GatherQuestEntity createRandom(TownEntity startTown, TownEntity endTown) {
        QuestConfig questConfig = KingdomsConfig.getQuestConfig();

        int difficulty = 0;

        // Base values
        float baseRewardValue = questConfig.getBaseRewardValue();
        float rewardFluctuation = (float) questConfig.getRewardFluctuationFraction();

        int minRewardItems = questConfig.getMinRewardUniqueItems();
        int maxRewardItems = questConfig.getMaxRewardUniqueItems();

        float minRewardScalingMult = (float) questConfig.getMinRewardScalingMultiplier();
        float maxRewardScalingMult = (float) questConfig.getMaxRewardScalingMultiplier();

        float rewardScalingMult = calculateScalingMultiplier(
                difficulty,
                0,
                1,
                6,
                minRewardScalingMult,
                maxRewardScalingMult);

        baseRewardValue *= (float) (rewardScalingMult * startTown.getLevelConfig().getQuestRewardMultiplier()) * 2;

        float minRewardValue = baseRewardValue * (1 - rewardFluctuation);
        float maxRewardValue = baseRewardValue * (1 + rewardFluctuation);

        Map<QuestItem, Double> rewardPool = questConfig.getRewardPool();

        List<ItemStack> rewards = QuestUtil.generateTradeItems(minRewardValue, maxRewardValue, minRewardItems, maxRewardItems, rewardPool);

        QuestEntity questEntity = new QuestEntity(QuestType.Gather, new Date(), -1, -1, minRewardValue, maxRewardValue, QuestStatus.NEW, startTown, endTown);
        questEntity.setCanDespawn(true);

        GatherQuestGoalEntity randomGoal = GatherQuestManager.getInstance().getRandomGoal(difficulty);

        GatherQuestEntity gatherQuest = new GatherQuestEntity();
        gatherQuest.setQuest(questEntity);
        gatherQuest.setGoal(randomGoal);
        gatherQuest.setRewards(rewards);
        return gatherQuest;
    }

    @Override
    public boolean tryComplete(Player player, @NotNull Location location, TownEntity clickedTown) {
        if (player == null) return false;

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.isEmpty()) return false;

        ICustomItem goalItem = CustomItemManager.getCustomItem(goal.getItemName());
        ItemStack goalItemStack = goalItem.getItemStack();

        boolean isSame = CustomItemManager.compare(itemInMainHand, goalItemStack);
        if (!isSame) return false;

        boolean hasQuestKey = itemInMainHand.getItemMeta().getPersistentDataContainer().has(QuestManager.QUEST_ID_KEY) &&
                itemInMainHand.getItemMeta().getPersistentDataContainer().get(QuestManager.QUEST_ID_KEY, PersistentDataType.LONG).equals(quest.getId());
        if (!hasQuestKey) return false;

        // Give rewards to the player
        for (ItemStack reward : getRewards()) {
            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(reward);
            if (!leftover.isEmpty()) {
                player.getWorld().dropItem(player.getLocation(), leftover.get(0));
            }
        }
        // Broadcast message
        LocaleManager.getInstance().broadcastMessage(CobaltKingdoms.getInstance(), "kingdoms.quests.gather.finish", StringPlaceholders.builder()
                .addPlaceholder("player", player.getName())
                .addPlaceholder("start_town", quest.getStartTown().getName())
                .addPlaceholder("end_town", quest.getEndTown().getName())
                .build());

        Location endLocation = quest.getEndTown().getLocation();
        for (int i = 0; i < 5; i++) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    spawnRandomFirework(endLocation);
                }
            }.runTaskLater(CobaltKingdoms.getInstance(), i * 4L);
        }

        endLocation.getWorld().playSound(endLocation, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);

        player.getInventory().setItemInMainHand(ItemStack.empty());

        return true;
    }

    @Override
    public void start(@NotNull Player player, @NotNull Location location) {
        // Drop item at the location
        String itemName = goal.getItemName();
        ICustomItem customItem = CustomItemManager.getCustomItem(itemName);
        if (customItem == null) return;

        ItemStack itemStack = customItem.getItemStack();
        if (itemStack == null) return;

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(QuestManager.QUEST_ID_KEY, PersistentDataType.LONG, quest.getId());
        itemStack.setItemMeta(itemMeta);

        Location goalLocation = goal.getLocation();
        World goalWorld = goalLocation.getWorld();
        goalWorld.dropItem(goalLocation.toCenterLocation(), itemStack, item -> {
            item.setGlowing(true);
            item.setPersistent(true);
            item.setGravity(false);
            item.setVelocity(new Vector());
        });

        giveQuestCompass(player, goalLocation, "Artifact Compass");

        // Broadcast message
        LocaleManager.getInstance().broadcastMessage(CobaltKingdoms.getInstance(), "kingdoms.quests.gather.start", StringPlaceholders.builder()
                .addPlaceholder("player", player.getName())
                .addPlaceholder("item", itemName)
                .build());
    }

    @Override
    public ItemStack getInstructionsItem() {
        return null;
    }

    @Override
    public boolean validateQuest(Player player) {
        return true;
    }

    @Override
    public Component getTitle() {
        if (quest.getEndTown() == null || quest.getStartTown() == null) return Component.text("Something went wrong");
        Component typeSymbol = Component.text(" [" + quest.getQuestType().symbol + "] ").color(quest.getQuestType().textColor);
        Component titleText = Component.text("Artifact Hunt").color(NamedTextColor.GRAY);
        return typeSymbol.append(titleText).append(typeSymbol).decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public String getSymbol() {
        return quest.getQuestType().symbol;
    }

    @Override
    public ItemStack getButtonItem() {
        if (quest.getEndTown() == null || quest.getStartTown() == null) return new ItemStack(Material.BARRIER);

        String coordinates = String.format("[%d, %d, %d]",
                goal.getLocation().getBlockX(),
                goal.getLocation().getBlockY(),
                goal.getLocation().getBlockZ());

        final ItemStack item = new ItemStack(Material.CLOCK);
        final ItemMeta meta = item.getItemMeta();

        meta.setItemModel(new NamespacedKey("thegreatwork", "quest/scroll_orange"));

        meta.displayName(getTitle());

        List<String> lore = new ArrayList<>();
        lore.add("&zCoords: &7" + coordinates);

        ICustomItem targetItem = CustomItemManager.getCustomItem(goal.getItemName());
        if (targetItem != null) lore.add("&zItem: &7" + targetItem.getItemStack().getItemMeta().getDisplayName());
        else {
            lore.add("Could not find target item, report as a bug");
            lore.add("with the following information:");
            lore.add("QuestID: " + quest.getId());
        }

        TownConfig townConfig = KingdomsConfig.getTownConfig();
        int startTownXp = quest.getStartTown().getExperience();
        TownLevelConfig townLevelConfig = townConfig.getTownLevelConfig(startTownXp);

        // Add rewards
        lore.add("");
        lore.add("&z[x" + townLevelConfig.getQuestRewardMultiplier() + "] Rewards:");
        List<LargeItemStack> rewardItems = LargeItemStack.toLargeItemStacks(getRewards());
        for (LargeItemStack rewardItem : rewardItems) {
            String name = rewardItem.item().getItemMeta().hasDisplayName() ? rewardItem.item().getItemMeta().getDisplayName() : formatMaterialName(rewardItem.item().getType().name());
            lore.add("&7- " + HexUtils.colorify(name) + " &7[&z" + rewardItem.amount() + "&7]");
        }

        lore.add("");
        Instant expiresAt = quest.getCreatedTimestamp().toInstant().plus(Duration.ofMinutes(60));
        Duration durationToExpires = Duration.between(Instant.now(), expiresAt);
        lore.add("&7Expires in " + durationToExpires.toMinutes() + " minutes");

        lore.replaceAll(HexUtils::colorify);

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public int getXpValue() {
        return 0;
    }

    @Override
    public boolean shouldShowInMenu(TownEntity town, Player player) {
        if (town == null) return false;
        if (quest.getStartTown() == null) return false;
        if (quest.getEndTown() == null) return false;

        return town.getId().equals(quest.getStartTown().getId()) &&
                quest.getStatus() == QuestStatus.NEW;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public Long getId() {
        return id;
    }

    public List<ItemStack> getRewards() {
        return rewards.list();
    }

    public void setRewards(List<ItemStack> rewards) {
        this.rewards = new ItemStackList(rewards);
    }

    public QuestEntity getQuest() {
        return quest;
    }

    public void setQuest(QuestEntity quest) {
        this.quest = quest;
    }

    public GatherQuestGoalEntity getGoal() {
        return goal;
    }

    public void setGoal(GatherQuestGoalEntity goal) {
        this.goal = goal;
    }
}
