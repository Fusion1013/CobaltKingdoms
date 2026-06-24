package se.fusion1013.cobaltKingdoms.quest.model;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
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
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.config.KingdomsConfig;
import se.fusion1013.cobaltKingdoms.config.quest.QuestArtifactHuntConfig;
import se.fusion1013.cobaltKingdoms.config.quest.QuestConfig;
import se.fusion1013.cobaltKingdoms.quest.service.ArtifactHuntQuestManager;
import se.fusion1013.cobaltKingdoms.quest.service.QuestManager;
import se.fusion1013.cobaltKingdoms.quest.util.QuestUtil;
import se.fusion1013.cobaltKingdoms.town.config.TownConfig;
import se.fusion1013.cobaltKingdoms.town.config.TownLevelConfig;
import se.fusion1013.cobaltKingdoms.town.model.Town;
import se.fusion1013.cobaltKingdoms.util.LargeItemStack;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static se.fusion1013.cobaltKingdoms.quest.util.QuestUtil.*;

public class ArtifactHuntQuest extends AbstractQuest {

    private Long id;
    private List<ItemStack> rewards;
    private ArtifactHuntGoal goal;

    public ArtifactHuntQuest() {
        super(QuestType.ARTIFACT_HUNT);
    }

    public ArtifactHuntQuest(Long id) {
        this();
        this.id = id;
    }

    public static ArtifactHuntQuest createRandom(Town startTown, Town endTown) {
        QuestConfig questConfig = KingdomsConfig.getQuestConfig();
        QuestArtifactHuntConfig artifactHuntConfig = questConfig.getArtifactHuntConfig();

        ArtifactHuntGoal randomGoal = ArtifactHuntQuestManager.getInstance().getRandomGoal();
        if (randomGoal == null) return null;

        // Base values
        float baseRewardValue = questConfig.getBaseRewardValue();
        float rewardFluctuation = (float) questConfig.getRewardFluctuationFraction();

        int minRewardItems = artifactHuntConfig.getMinRewardUniqueItems() > 0 ? artifactHuntConfig.getMinRewardUniqueItems() : questConfig.getMinRewardUniqueItems();
        int maxRewardItems = artifactHuntConfig.getMaxRewardUniqueItems() > 0 ? artifactHuntConfig.getMaxRewardUniqueItems() : questConfig.getMaxRewardUniqueItems();

        float minRewardScalingMult = (float) questConfig.getMinRewardScalingMultiplier();
        float maxRewardScalingMult = (float) questConfig.getMaxRewardScalingMultiplier();

        float rewardScalingMult = calculateScalingMultiplier(
                randomGoal.getDifficulty() + 3,
                0,
                1,
                16,
                minRewardScalingMult,
                maxRewardScalingMult);

        baseRewardValue *= (float) (rewardScalingMult * (startTown.getLevelConfig().getQuestRewardMultiplier() + artifactHuntConfig.getRewardMultiplier())) * 2;

        float minRewardValue = baseRewardValue * (1 - rewardFluctuation);
        float maxRewardValue = baseRewardValue * (1 + rewardFluctuation);

        Map<QuestItem, Double> rewardPool = artifactHuntConfig.getRewardPool();

        List<ItemStack> rewards = QuestUtil.generateTradeItems(minRewardValue, maxRewardValue, minRewardItems, maxRewardItems, rewardPool, List.of());
        if (rewards.isEmpty()) return null;

        ArtifactHuntQuest quest = new ArtifactHuntQuest();

        quest.setCreatedTimestamp(new Date());
        quest.setMinRequirementValue(-1);
        quest.setMaxRequirementValue(-1);
        quest.setMinRewardValue(minRewardValue);
        quest.setMaxRewardValue(maxRewardValue);
        quest.setQuestStatus(QuestStatus.NEW);
        quest.setStartTown(startTown);
        quest.setEndTown(endTown);
        quest.setCanDespawn(true);
        quest.setGoal(randomGoal);
        quest.setRewards(rewards);

        return quest;
    }

    @Override
    public boolean tryComplete(@NotNull Player player, @NotNull Location location, Long locationId) {
        if (!Objects.equals(locationId, getEndTown().getId())) return false;

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.isEmpty()) return false;

        ICustomItem goalItem = CustomItemManager.getCustomItem(goal.getItemName());
        ItemStack goalItemStack = goalItem.getItemStack();

        boolean isSame = CustomItemManager.compare(itemInMainHand, goalItemStack);
        if (!isSame) return false;

        boolean hasQuestKey = itemInMainHand.getItemMeta().getPersistentDataContainer().has(QuestManager.QUEST_ID_KEY) &&
                itemInMainHand.getItemMeta().getPersistentDataContainer().get(QuestManager.QUEST_ID_KEY, PersistentDataType.LONG).equals(getQuestId());
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
                .addPlaceholder("start_town", getStartTown().getDisplayName())
                .addPlaceholder("end_town", getEndTown().getDisplayName())
                .build());

        Location endLocation = getEndTown().getLocation();
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
        QuestUtil.clearQuestItems(player, getQuestId());

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
        itemMeta.getPersistentDataContainer().set(QuestManager.QUEST_ID_KEY, PersistentDataType.LONG, getQuestId());
        itemStack.setItemMeta(itemMeta);

        Location goalLocation = goal.getLocation();
        World goalWorld = goalLocation.getWorld();
        goalWorld.dropItem(goalLocation.toCenterLocation(), itemStack, item -> {
            item.setGlowing(true);
            item.setPersistent(true);
            item.setGravity(false);
            item.setVelocity(new Vector());
        });

//        giveQuestCompass(player, goalLocation, "Artifact Compass", getQuestId());

        // Broadcast message
        LocaleManager.getInstance().broadcastMessage(CobaltKingdoms.getInstance(), "kingdoms.quests.gather.start", StringPlaceholders.builder()
                .addPlaceholder("player", player.getName())
                .addPlaceholder("item", itemName)
                .build());
    }

    @Override
    public void fail(@NotNull Player player, QuestFailReason reason) {
        LocaleManager.getInstance().broadcastMessage(CobaltKingdoms.getInstance(), "kingdoms.quests.artifact_hunt.fail", StringPlaceholders.builder()
                .addPlaceholder("player", player.getDisplayName())
                .build());
    }

    @Override
    public String getTitle() {
        if (getEndTown() == null || getStartTown() == null) return "Something went wrong";
        return HexUtils.colorify(QuestUtil.formatTitle("Artifact Hunt", getQuestType().symbol));
    }

    @Override
    public Response canClaim(@NotNull Player player) {
        if (getQuestStatus() == QuestStatus.NEW) return Response.ok("Can claim");
        return Response.error("Quest is not new");
    }

    @Override
    public int getDuration() {
        return 1000 * 60 * 60 * 4;
    }

    @Override
    public int getXpValue() {
        return 0;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean validateQuest(@NotNull Player player) {
        return true;
    }

    @Override
    public boolean shouldShowInMenu(Town town, Player player) {
        if (town == null) return false;
        if (getStartTown() == null) return false;
        if (getEndTown() == null) return false;

        return town.getId().equals(getStartTown().getId()) &&
                getQuestStatus() == QuestStatus.NEW;
    }

    @Override
    public ItemStack getButtonItem() {
        if (getEndTown() == null || getStartTown() == null) return new ItemStack(Material.BARRIER);
        if (goal == null) return new ItemStack(Material.BARRIER);

        String coordinates = String.format("[%d, %d, %d]",
                goal.getLocation().getBlockX(),
                goal.getLocation().getBlockY(),
                goal.getLocation().getBlockZ());

        final ItemStack item = new ItemStack(Material.CLOCK);
        final ItemMeta meta = item.getItemMeta();

        meta.setItemModel(new NamespacedKey("thegreatwork", "quest/scroll_orange"));

        meta.setDisplayName(getTitle());

        List<String> lore = new ArrayList<>();
//        lore.add("&zCoords: &7" + coordinates);

        ICustomItem targetItem = CustomItemManager.getCustomItem(goal.getItemName());
        if (targetItem != null) {
            String customItemDisplayName = targetItem.getItemStack().getItemMeta().getDisplayName();
            customItemDisplayName = HexUtils.stripColorCodes(customItemDisplayName);
            lore.add("&zArtifact: &7" + customItemDisplayName);
        } else {
            lore.add("Could not find target item, report as a bug");
            lore.add("with the following information:");
            lore.add("QuestID: " + getQuestId());
        }

        lore.add("&zTime Limit: &7" + QuestUtil.formatDuration(getDuration()));

        if (goal.getDescription() != null && !goal.getDescription().isEmpty()) {
            lore.add("");
            lore.addAll(QuestUtil.wrapText(goal.getDescription(), 35).stream().map(s -> "&7" + s).toList());
        }

        TownConfig townConfig = KingdomsConfig.getTownConfig();
        int startTownXp = getStartTown().getExperience();
        TownLevelConfig townLevelConfig = townConfig.getTownLevelConfig(startTownXp);

        // Add rewards
        lore.add("");
        lore.add("&z[x" + townLevelConfig.getQuestRewardMultiplier() + "] Rewards:");
        List<LargeItemStack> rewardItems = LargeItemStack.toLargeItemStacks(getRewards());
        for (LargeItemStack rewardItem : rewardItems) {
            String name = rewardItem.item().getItemMeta().hasDisplayName() ? rewardItem.item().getItemMeta().getDisplayName() : formatMaterialName(rewardItem.item().getType().name());
            lore.add("&7- " + HexUtils.stripColorCodes(name) + " &7[&z" + rewardItem.amount() + "&7]");
        }

        lore.add("");
        Instant expiresAt = getCreatedTimestamp().toInstant().plus(Duration.ofMinutes(60));
        Duration durationToExpires = Duration.between(Instant.now(), expiresAt);
        lore.add("&7Expires in " + durationToExpires.toMinutes() + " minutes");

        lore.replaceAll(HexUtils::colorify);

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public ItemStack getInstructionsItem() {
        ItemStack itemStack = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) itemStack.getItemMeta();

        ICustomItem customItem = CustomItemManager.getCustomItem(goal.getItemName());
        ItemStack targetItem = customItem.getItemStack();

        meta.setTitle(getTitle());
        meta.setDisplayName(getTitle());
        meta.setAuthor("Quest");

        String itemDisplayName = targetItem.getItemMeta().getDisplayName();
        itemDisplayName = HexUtils.stripColorCodes(itemDisplayName);

        String content = "&z&lArtifact Hunt\n\n" +
                "&z&lArtifact: &8" + itemDisplayName + "\n\n" +
                "&z&lReward: &8";

        List<LargeItemStack> rewardItems = LargeItemStack.toLargeItemStacks(getRewards());
        for (LargeItemStack rewardItem : rewardItems) {
            String name = rewardItem.item().getItemMeta().hasDisplayName() ? rewardItem.item().getItemMeta().getDisplayName() : formatMaterialName(rewardItem.item().getType().name());
            content += "\n&7- " + HexUtils.stripColorCodes(name) + " &7[&z" + rewardItem.amount() + "&7]";
        }


        meta.addPage(HexUtils.colorify(content));

        meta.getPersistentDataContainer().set(QuestManager.QUEST_ID_KEY, PersistentDataType.LONG, getQuestId());

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    // ##%%##%%## GETTERS / SETTERS ##%%##%%## //

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ItemStack> getRewards() {
        return rewards;
    }

    public void setRewards(List<ItemStack> rewards) {
        this.rewards = rewards;
    }

    public ArtifactHuntGoal getGoal() {
        return goal;
    }

    public void setGoal(ArtifactHuntGoal goal) {
        this.goal = goal;
    }
}
