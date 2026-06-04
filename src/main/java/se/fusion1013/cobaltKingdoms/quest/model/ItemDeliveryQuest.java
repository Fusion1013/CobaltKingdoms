package se.fusion1013.cobaltKingdoms.quest.model;

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
import se.fusion1013.cobaltKingdoms.quest.service.QuestManager;
import se.fusion1013.cobaltKingdoms.quest.util.QuestUtil;
import se.fusion1013.cobaltKingdoms.town.config.TownConfig;
import se.fusion1013.cobaltKingdoms.town.config.TownLevelConfig;
import se.fusion1013.cobaltKingdoms.town.model.Town;
import se.fusion1013.cobaltKingdoms.util.ItemSerializationUtils;
import se.fusion1013.cobaltKingdoms.util.LargeItemStack;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static se.fusion1013.cobaltKingdoms.quest.util.QuestUtil.*;

public class ItemDeliveryQuest extends AbstractQuest {

    public static final NamespacedKey DROPS_KEY = new NamespacedKey(CobaltKingdoms.getInstance(), "quest_death_items");
    public static final NamespacedKey QUEST_KEY = new NamespacedKey(CobaltKingdoms.getInstance(), "quest");

    private Long id;
    private List<ItemStack> requiredItems;
    private List<ItemStack> rewards;

    public ItemDeliveryQuest() {
        super(QuestType.DELIVER);
    }

    public ItemDeliveryQuest(Long questId) {
        super(questId, QuestType.DELIVER);
    }

    public static ItemDeliveryQuest createRandom(Town startTown, Town endTown) {
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

        Map<QuestItem, Double> requirementPool = itemDeliveryConfig.getRequirementPool();
        Map<QuestItem, Double> rewardPool = itemDeliveryConfig.getRewardPool();

        List<ItemStack> rewards = QuestUtil.generateTradeItems(minRewardValue, maxRewardValue, minRewardItems, maxRewardItems, rewardPool, List.of());
        List<ItemStack> requiredItems = QuestUtil.generateTradeItems(minReqValue, maxReqValue, minReqItems, maxReqItems, requirementPool, rewards);

        if (rewards.isEmpty() || requiredItems.isEmpty()) return null;

        ItemDeliveryQuest quest = new ItemDeliveryQuest();

        quest.setCreatedTimestamp(new Date());
        quest.setMinRequirementValue(minReqValue);
        quest.setMaxRequirementValue(maxReqValue);
        quest.setMinRewardValue(minRewardValue);
        quest.setMaxRewardValue(maxRewardValue);
        quest.setQuestStatus(QuestStatus.NEW);
        quest.setStartTown(startTown);
        quest.setEndTown(endTown);
        quest.setCanDespawn(true);

        quest.setRequiredItems(requiredItems);
        quest.setRewards(rewards);

        return quest;
    }

    @Override
    public boolean tryComplete(@NotNull Player player, @NotNull Location location, Long locationId) {
        if (!locationId.equals(getEndTown().getId())) {
            CobaltKingdoms.getInstance().getLogger().info("Wrong end town:");
            CobaltKingdoms.getInstance().getLogger().info(" - " + locationId);
            CobaltKingdoms.getInstance().getLogger().info(" - " + getEndTown().getDisplayName());
            return false;
        }

        Collection<Camel> nearbyEntitiesByType = location.getNearbyEntitiesByType(Camel.class, 12, camel -> {
            Long questId = camel.getPersistentDataContainer().get(QUEST_KEY, PersistentDataType.LONG);
            return Objects.equals(questId, getQuestId());
        });

        if (nearbyEntitiesByType.isEmpty()) {
            CobaltKingdoms.getInstance().getLogger().info("No nearby camel");
            return false;
        }

        for (Camel camel : nearbyEntitiesByType) {
            camel.remove();
        }

        QuestUtil.clearQuestItems(player, getQuestId());

        // Give rewards to the player
        for (ItemStack reward : getRewards()) {
            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(reward);
            if (!leftover.isEmpty()) {
                player.getWorld().dropItem(player.getLocation(), leftover.get(0));
            }
        }
        // Broadcast message
        LocaleManager.getInstance().broadcastMessage(CobaltKingdoms.getInstance(), "kingdoms.quests.delivery.finish", StringPlaceholders.builder()
                .addPlaceholder("player", player.getName())
                .addPlaceholder("start_town", getStartTown().getDisplayName())
                .addPlaceholder("end_town", getEndTown().getDisplayName())
                .build());

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
        camel.customName(Component.text("Trade Caravan to " + getEndTown().getDisplayName()));
        float caravan_health = 40;
        Objects.requireNonNull(camel.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(caravan_health);
        camel.setHealth(caravan_health);
        camel.setRemoveWhenFarAway(false);
        PotionEffect glowEffect = new PotionEffect(
                PotionEffectType.GLOWING, PotionEffect.INFINITE_DURATION,
                0, false, false, false);
        camel.addPotionEffect(glowEffect);
        camel.setLeashHolder(player);

        QuestUtil.giveQuestCompass(player, endTown.getLocation(), endTown.getDisplayName(), getQuestId());

        // Store required items in persistent data container
        PersistentDataContainer container = camel.getPersistentDataContainer();
        container.set(DROPS_KEY, PersistentDataType.STRING, ItemSerializationUtils.serializeItemStacks(getRequiredItems()));
        container.set(QUEST_KEY, PersistentDataType.LONG, getQuestId());

        // Broadcast message
        LocaleManager.getInstance().broadcastMessage(CobaltKingdoms.getInstance(), "kingdoms.quests.start.item_delivery", StringPlaceholders.builder()
                .addPlaceholder("player", player.getName())
                .addPlaceholder("start_town", getStartTown().getDisplayName())
                .addPlaceholder("end_town", getEndTown().getDisplayName())
                .build());
    }

    @Override
    public void fail(@NotNull Player player, QuestFailReason reason) {
        LocaleManager.getInstance().broadcastMessage(CobaltKingdoms.getInstance(), "kingdoms.quests.delivery.fail", StringPlaceholders.builder()
                .addPlaceholder("player", player.getDisplayName())
                .build());
    }

    @Override
    public ItemStack getButtonItem() {
        if (endTown == null || getStartTown() == null) return new ItemStack(Material.BARRIER);

        String coordinates = String.format("[%d, %d, %d]",
                endTown.getLocation().getBlockX(),
                endTown.getLocation().getBlockY(),
                endTown.getLocation().getBlockZ());

        int distance = (int) getStartTown().getLocation().distance(getEndTown().getLocation());

        final ItemStack item = new ItemStack(Material.FILLED_MAP);
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(getTitle());

        List<String> lore = new ArrayList<>();
        lore.add("&zCoords: &7" + coordinates);
        lore.add("&zDistance: &7" + distance);
        lore.add("&zTime Limit: &7" + QuestUtil.formatDuration(getDuration()));
        lore.add("");

        TownConfig townConfig = KingdomsConfig.getTownConfig();
        int startTownXp = getStartTown().getExperience();
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
        Instant expiresAt = getCreatedTimestamp().toInstant().plus(Duration.ofMinutes(60));
        Duration durationToExpires = Duration.between(Instant.now(), expiresAt);
        lore.add("&7Expires in " + durationToExpires.toMinutes() + " minutes");

        lore.replaceAll(HexUtils::colorify);

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public String getTitle() {
        if (getEndTown() == null || getStartTown() == null) return "Something went wrong";
        return HexUtils.colorify(QuestUtil.formatTitle("Delivery to " + endTown.getDisplayName(), getQuestType().symbol));
    }

    @Override
    public Response canClaim(@NotNull Player player) {
        if (getQuestStatus() == QuestStatus.NEW) return Response.ok("Claim quest");
        return Response.error("Quest is not new");
    }

    @Override
    public ItemStack getInstructionsItem() {
        ItemStack itemStack = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) itemStack.getItemMeta();

        meta.setTitle(getTitle());
        meta.setDisplayName(getTitle());
        meta.setAuthor("Quest");

        meta.getPersistentDataContainer().set(QuestManager.QUEST_ID_KEY, PersistentDataType.LONG, getQuestId());

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public int getDuration() {
        return 1000 * 60 * 60 * 2;
    }

    @Override
    public int getXpValue() {
        double averageQuestRewardValue = (getMinRewardValue() + getMaxRewardValue()) / 2.0;
        return (int) averageQuestRewardValue / 100;
    }

    @Override
    public boolean isValid() {
        return getStartTown() != null && getEndTown() != null;
    }

    @Override
    public boolean validateQuest(@NotNull Player player) {
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
                    .addPlaceholder("item", item.getItemMeta().hasDisplayName() ? HexUtils.stripColorCodes(item.getItemMeta().getDisplayName()) : formatMaterialName(item.getType().name()))
                    .addPlaceholder("amount", item.getAmount())
                    .build());
        }
        return false;
    }

    @Override
    public boolean shouldShowInMenu(Town startTown, Player player) {
        if (startTown == null) return false;
        if (getStartTown() == null) return false;
        if (getEndTown() == null) return false;

        return startTown.getId().equals(getStartTown().getId()) &&
                getQuestStatus() == QuestStatus.NEW;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ItemStack> getRequiredItems() {
        return requiredItems;
    }

    public void setRequiredItems(List<ItemStack> requiredItems) {
        this.requiredItems = requiredItems;
    }

    public List<ItemStack> getRewards() {
        return rewards;
    }

    public void setRewards(List<ItemStack> rewards) {
        this.rewards = rewards;
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
}
