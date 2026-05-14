package se.fusion1013.cobaltKingdoms.quest.item_delivery;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Camel;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.HexUtils;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.config.KingdomsConfig;
import se.fusion1013.cobaltKingdoms.config.quest.QuestConfig;
import se.fusion1013.cobaltKingdoms.config.quest.QuestItemDeliveryConfig;
import se.fusion1013.cobaltKingdoms.config.town.TownConfig;
import se.fusion1013.cobaltKingdoms.config.town.TownLevelConfig;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownEntity;
import se.fusion1013.cobaltKingdoms.quest.*;
import se.fusion1013.cobaltKingdoms.util.ItemSerializationUtils;
import se.fusion1013.cobaltKingdoms.util.LargeItemStack;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static se.fusion1013.cobaltKingdoms.quest.QuestUtil.*;

@DatabaseTable(tableName = "quests_item_delivery")
public class ItemDeliveryQuestEntity implements IQuestData {

    public static final NamespacedKey DROPS_KEY = new NamespacedKey(CobaltKingdoms.getInstance(), "quest_death_items");
    public static final NamespacedKey QUEST_KEY = new NamespacedKey(CobaltKingdoms.getInstance(), "quest");

    @DatabaseField(generatedId = true, columnName = "uuid")
    private Long id;

    @DatabaseField(columnName = "required_items_serialized")
    private String requiredItemsSerialized;

    @DatabaseField(columnName = "rewards_serialized")
    private String rewardsSerialized;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, columnName = "quest")
    private QuestEntity quest;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, columnName = "end_town")
    private TownEntity endTown;

    private transient List<ItemStack> requiredItems;
    private transient List<ItemStack> rewardItems;

    public ItemDeliveryQuestEntity() {
    }

    private ItemDeliveryQuestEntity(List<ItemStack> requiredItems,
                                    List<ItemStack> rewardItems,
                                    QuestEntity quest,
                                    TownEntity endTown) {
        this.requiredItemsSerialized = ItemSerializationUtils.serializeItemStacks(requiredItems);
        this.rewardsSerialized = ItemSerializationUtils.serializeItemStacks(rewardItems);
        this.quest = quest;
        this.endTown = endTown;

        this.requiredItems = new ArrayList<>(requiredItems);
        this.rewardItems = new ArrayList<>(rewardItems);
    }

    public static ItemDeliveryQuestEntity createRandom(TownEntity startTown, TownEntity endTown) {
        QuestConfig questConfig = KingdomsConfig.getQuestConfig();

        // Base values
        float baseReqValue = questConfig.getBaseRequirementsValue();
        float baseRewardValue = questConfig.getBaseRewardValue();

        float reqFluctuation = (float) questConfig.getRequirementsFluctuationFraction();
        float rewardFluctuation = (float) questConfig.getRewardFluctuationFraction();

        int minReqItems = questConfig.getMinRequirementsUniqueItems();
        int maxReqItems = questConfig.getMaxRequirementsUniqueItems();

        int minRewardItems = questConfig.getMinRewardUniqueItems();
        int maxRewardItems = questConfig.getMaxRewardUniqueItems();

        QuestItemDeliveryConfig itemDeliveryConfig = questConfig.getItemDeliveryConfig();

        // Distance scaling
        float minReqScalingDist = itemDeliveryConfig.getMinRequirementsScalingDistance();
        float baseReqScalingDist = itemDeliveryConfig.getBaseRequirementsScalingDistance();
        float maxReqScalingDist = itemDeliveryConfig.getMaxRequirementsScalingDistance();

        float minReqScalingMult = (float) questConfig.getMinRequirementsScalingMultiplier();
        float maxReqScalingMult = (float) questConfig.getMaxRequirementsScalingMultiplier();

        float minRewardScalingDist = itemDeliveryConfig.getMinRewardScalingDistance();
        float baseRewardScalingDist = itemDeliveryConfig.getBaseRewardScalingDistance();
        float maxRewardScalingDist = itemDeliveryConfig.getMaxRewardScalingDistance();

        float minRewardScalingMult = (float) questConfig.getMinRewardScalingMultiplier();
        float maxRewardScalingMult = (float) questConfig.getMaxRewardScalingMultiplier();

        double distance = startTown.getLocation().distance(endTown.getLocation());

        float reqScalingMult = calculateScalingMultiplier(
                distance,
                minReqScalingDist,
                baseReqScalingDist,
                maxReqScalingDist,
                minReqScalingMult,
                maxReqScalingMult);

        float rewardScalingMult = calculateScalingMultiplier(
                distance,
                minRewardScalingDist,
                baseRewardScalingDist,
                maxRewardScalingDist,
                minRewardScalingMult,
                maxRewardScalingMult);

        baseReqValue *= (float) (reqScalingMult * startTown.getLevelConfig().getQuestRequirementsMultiplier());
        baseRewardValue *= (float) (rewardScalingMult * (startTown.getLevelConfig().getQuestRewardMultiplier() + itemDeliveryConfig.getRewardMultiplier()));

        float minReqValue = baseReqValue * (1 - reqFluctuation);
        float maxReqValue = baseReqValue * (1 + reqFluctuation);
        float minRewardValue = baseRewardValue * (1 - rewardFluctuation);
        float maxRewardValue = baseRewardValue * (1 + rewardFluctuation);

        Map<QuestItem, Double> requirementPool = questConfig.getRequirementPool();
        Map<QuestItem, Double> rewardPool = questConfig.getRewardPool();

        List<ItemStack> rewards = QuestUtil.generateTradeItems(minRewardValue, maxRewardValue, minRewardItems, maxRewardItems, rewardPool, List.of());
        List<ItemStack> requiredItems = QuestUtil.generateTradeItems(minReqValue, maxReqValue, minReqItems, maxReqItems, requirementPool, rewards);

        if (rewards.isEmpty() || requiredItems.isEmpty()) return null;

        QuestEntity questEntity = new QuestEntity(QuestType.DELIVER, new Date(), minReqValue, maxReqValue, minRewardValue, maxRewardValue, QuestStatus.NEW, startTown, endTown);
        questEntity.setCanDespawn(true);

        return new ItemDeliveryQuestEntity(requiredItems, rewards, questEntity, endTown);
    }


    @Override
    public boolean tryComplete(Player player, @NotNull Location location, TownEntity clickedTown) {
        if (!clickedTown.getId().equals(quest.getEndTown().getId())) {
            CobaltKingdoms.getInstance().getLogger().info("Wrong end town:");
            CobaltKingdoms.getInstance().getLogger().info(" - " + clickedTown.getName());
            CobaltKingdoms.getInstance().getLogger().info(" - " + quest.getEndTown().getName());
            return false;
        }

        Collection<Camel> nearbyEntitiesByType = location.getNearbyEntitiesByType(Camel.class, 12, camel -> {
            Long questId = camel.getPersistentDataContainer().get(QUEST_KEY, PersistentDataType.LONG);
            return Objects.equals(questId, quest.getId());
        });

        if (nearbyEntitiesByType.isEmpty()) {
            CobaltKingdoms.getInstance().getLogger().info("No nearby camel");
            return false;
        }

        for (Camel camel : nearbyEntitiesByType) {
            camel.remove();
        }

        if (player != null) {
            QuestUtil.clearQuestItems(player, quest.getId());

            // Give rewards to the player
            for (ItemStack reward : getRewards()) {
                HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(reward);
                if (!leftover.isEmpty()) {
                    player.getWorld().dropItem(player.getLocation(), leftover.get(0));
                }
            }
            // Broadcast message
            LocaleManager.getInstance().broadcastMessage(CobaltKingdoms.getInstance(), "kingdoms.quests.finish", StringPlaceholders.builder()
                    .addPlaceholder("player", player.getName())
                    .addPlaceholder("start_town", quest.getStartTown().getName())
                    .addPlaceholder("end_town", quest.getEndTown().getName())
                    .build());
        }

        Location endLocation = endTown.getLocation();
        for (int i = 0; i < 5; i++) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    spawnRandomFirework(endLocation);
                }
            }.runTaskLater(CobaltKingdoms.getInstance(), i * 4L);
        }

        endLocation.getWorld().playSound(endLocation, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);

        return true;
    }

    @Override
    public void start(@NotNull Player player, @NotNull Location location) {
        for (ItemStack requiredItem : getRequiredItems()) {
            player.getInventory().removeItem(requiredItem);
        }

        // Spawn and setup camel
        Location spawnLocation = player.getLocation();
        Camel camel = (Camel) player.getWorld().spawnEntity(spawnLocation, EntityType.CAMEL);
        camel.customName(Component.text("Trade Caravan to " + quest.getEndTown().getName()));
        float caravan_health = 40;
        Objects.requireNonNull(camel.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(caravan_health);
        camel.setHealth(caravan_health);
        camel.setRemoveWhenFarAway(false);
        PotionEffect glowEffect = new PotionEffect(
                PotionEffectType.GLOWING, PotionEffect.INFINITE_DURATION,
                0, false, false, false);
        camel.addPotionEffect(glowEffect);
        camel.setLeashHolder(player);

        QuestUtil.giveQuestCompass(player, endTown.getLocation(), "Compass to " + endTown.getName(), quest.getId());

        // Store required items in persistent data container
        PersistentDataContainer container = camel.getPersistentDataContainer();
        container.set(DROPS_KEY, PersistentDataType.STRING, ItemSerializationUtils.serializeItemStacks(getRequiredItems()));
        container.set(QUEST_KEY, PersistentDataType.LONG, quest.getId());

        // Broadcast message
        LocaleManager.getInstance().broadcastMessage(CobaltKingdoms.getInstance(), "kingdoms.quests.start", StringPlaceholders.builder()
                .addPlaceholder("player", player.getName())
                .addPlaceholder("start_town", quest.getStartTown().getName())
                .addPlaceholder("end_town", quest.getEndTown().getName())
                .build());
    }

    @Override
    public ItemStack getInstructionsItem() {
        ItemStack itemStack = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) itemStack.getItemMeta();

        meta.setTitle(getTitle());
        meta.setDisplayName(getTitle());
        meta.setAuthor("Quest");

        meta.getPersistentDataContainer().set(QuestManager.QUEST_ID_KEY, PersistentDataType.LONG, quest.getId());

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public boolean validateQuest(Player player) {
        List<ItemStack> missingItems = new ArrayList<>();
        for (ItemStack requiredItem : getRequiredItems()) {
            if (!player.getInventory().containsAtLeast(requiredItem, requiredItem.getAmount())) {
                missingItems.add(requiredItem);
            }
        }

        if (missingItems.isEmpty()) {
            return true;
        }

        LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.quests.fail_missing_items.header");
        for (ItemStack item : missingItems) {
            LocaleManager.getInstance().sendMessage("", player, "kingdoms.quests.fail_missing_items.item", StringPlaceholders.builder()
                    .addPlaceholder("item", item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : formatMaterialName(item.getType().name()))
                    .addPlaceholder("amount", item.getAmount())
                    .build());
        }
        return false;
    }

    @Override
    public String getTitle() {
        if (quest.getEndTown() == null || quest.getStartTown() == null) return "Something went wrong";
        return HexUtils.colorify(QuestUtil.formatTitle("Delivery to " + endTown.getName(), quest.getQuestType().symbol));
    }

    @Override
    public String getSymbol() {
        return quest.getQuestType().symbol;
    }

    @Override
    public ItemStack getButtonItem() {
        if (endTown == null || quest.getStartTown() == null) return new ItemStack(Material.BARRIER);

        String coordinates = String.format("[%d, %d, %d]",
                endTown.getLocation().getBlockX(),
                endTown.getLocation().getBlockY(),
                endTown.getLocation().getBlockZ());

        int distance = (int) quest.getStartTown().getLocation().distance(quest.getEndTown().getLocation());

        final ItemStack item = new ItemStack(Material.FILLED_MAP);
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(getTitle());

        List<String> lore = new ArrayList<>();
        lore.add("&zCoords: &7" + coordinates);
        lore.add("&zDistance: &7" + distance);
        lore.add("&zTime Limit: &7" + QuestUtil.formatDuration(getDuration()));
        lore.add("");

        TownConfig townConfig = KingdomsConfig.getTownConfig();
        int startTownXp = quest.getStartTown().getExperience();
        TownLevelConfig townLevelConfig = townConfig.getTownLevelConfig(startTownXp);

        // Add requested items
        lore.add("&z[x" + townLevelConfig.getQuestRequirementsMultiplier() + "] Requested Items:");
        List<LargeItemStack> requiredItems = LargeItemStack.toLargeItemStacks(getRequiredItems());
        for (LargeItemStack requestedItem : requiredItems) {
            String name = requestedItem.item().getItemMeta().hasDisplayName() ? requestedItem.item().getItemMeta().getDisplayName() : formatMaterialName(requestedItem.item().getType().name());
            lore.add("&7- " + HexUtils.stripColorCodes(name) + " &7[&z" + requestedItem.amount() + "&7]");
        }
        // Add rewards
        lore.add("");
        lore.add("&z[x" + townLevelConfig.getQuestRewardMultiplier() + "] Rewards:");
        List<LargeItemStack> rewardItems = LargeItemStack.toLargeItemStacks(getRewards());
        for (LargeItemStack rewardItem : rewardItems) {
            String name = rewardItem.item().getItemMeta().hasDisplayName() ? rewardItem.item().getItemMeta().getDisplayName() : formatMaterialName(rewardItem.item().getType().name());
            lore.add("&7- " + HexUtils.stripColorCodes(name) + " &7[&z" + rewardItem.amount() + "&7]");
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
        double averageQuestRewardValue = (quest.getMinRewardValue() + quest.getMaxRewardValue()) / 2.0;
        return (int) averageQuestRewardValue / 100;
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
        return quest.getStartTown() != null && quest.getEndTown() != null;
    }

    @Override
    public void fail(Player player, QuestFailReason reason) {
        LocaleManager.getInstance().broadcastMessage(CobaltKingdoms.getInstance(), "kingdoms.quests.delivery.fail", StringPlaceholders.builder()
                .addPlaceholder("player", player.getDisplayName())
                .build());
    }

    @Override
    public Response canClaim(Player player) {
        if (quest.getStatus() == QuestStatus.NEW) return Response.ok("Claim quest");
        return Response.error("Quest is not new");
    }

    @Override
    public int getDuration() {
        return 1000 * 60 * 60 * 2;
    }

    private String getTimeUntil(Instant expirationTime) {
        Duration duration = Duration.between(Instant.now(), expirationTime);
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();

        if (days > 0) {
            return days + (days == 1 ? " day" : " days");
        } else if (hours > 0) {
            return hours + (hours == 1 ? " hour" : " hours");
        } else if (minutes > 0) {
            return minutes + (minutes == 1 ? " minute" : " minutes");
        } else {
            return "<1 minute";
        }
    }

    public List<ItemStack> getRequiredItems() {
        if (requiredItems == null) {
            requiredItems = ItemSerializationUtils.deserializeItemStacks(requiredItemsSerialized);
        }
        return requiredItems;
    }

    public void setRequiredItems(List<ItemStack> requiredItems) {
        this.requiredItemsSerialized = ItemSerializationUtils.serializeItemStacks(requiredItems);
        this.requiredItems = new ArrayList<>(requiredItems);
    }

    public List<ItemStack> getRewards() {
        if (rewardItems == null) {
            rewardItems = ItemSerializationUtils.deserializeItemStacks(rewardsSerialized);
        }
        return rewardItems;
    }

    public void setRewards(List<ItemStack> rewards) {
        this.rewardsSerialized = ItemSerializationUtils.serializeItemStacks(rewards);
        this.rewardItems = new ArrayList<>(rewards);
    }
}
