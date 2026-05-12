package se.fusion1013.cobaltKingdoms.quest;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.config.town.TownLevelConfig;
import se.fusion1013.cobaltKingdoms.database.kingdom.town.ITownRepository;
import se.fusion1013.cobaltKingdoms.database.quest.IQuestRepository;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownEntity;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownManager;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownMemberEntity;
import se.fusion1013.cobaltKingdoms.quest.gui.QuestMenu;
import se.fusion1013.cobaltKingdoms.quest.item_delivery.ItemDeliveryQuestEntity;
import se.fusion1013.cobaltKingdoms.util.ItemSerializationUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static se.fusion1013.cobaltKingdoms.quest.item_delivery.ItemDeliveryQuestEntity.DROPS_KEY;
import static se.fusion1013.cobaltKingdoms.quest.item_delivery.ItemDeliveryQuestEntity.QUEST_KEY;

public class QuestManager extends Manager<CobaltKingdoms> implements Listener {

    public static final NamespacedKey QUEST_ID_KEY = new NamespacedKey(CobaltKingdoms.getInstance(), "quest_id");
    public static final NamespacedKey QUEST_GIVER_ID_KEY = new NamespacedKey(CobaltKingdoms.getInstance(), "quest_giver_id");

    private static final IQuestRepository questRepository = DataManager.getInstance().getDao(IQuestRepository.class);
    private static final ITownRepository townRepository = DataManager.getInstance().getDao(ITownRepository.class);

    private static final Random random = new Random();

    // TODO: Replace with a list of some custom class
    // TODO: Store the spawn time and remove the entity after some time
    private static final Map<Location, Parrot> QUEST_GIVER_ENTITIES = new HashMap<>();

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Entity rightClicked = event.getRightClicked();
        // TODO: Might add this back later for more dynamic quests that is not given by towns
//        if (rightClicked.getPersistentDataContainer().has(QUEST_GIVER_ID_KEY)) resolveInteractAtQuestGiver(event);
        if (rightClicked.getPersistentDataContainer().has(TownManager.TOWN_ENTITY_KEY))
            resolveInteractAtTownEntity(event);
    }

    private void resolveInteractAtTownEntity(PlayerInteractAtEntityEvent event) {
        Entity rightClicked = event.getRightClicked();
        Player player = event.getPlayer();
        if (event.getHand() != EquipmentSlot.HAND) return;
        TownEntity clickedTown = TownManager.getInstance().getTown(rightClicked);
        if (clickedTown == null) return;

        boolean completed = false;
        Optional<List<ActivePlayerQuestEntity>> activePlayerQuestsByPlayer = questRepository.getActivePlayerQuestsByPlayer(player);

        if (activePlayerQuestsByPlayer.isPresent()) {
            List<ActivePlayerQuestEntity> activePlayerQuestEntities = activePlayerQuestsByPlayer.get();
            for (ActivePlayerQuestEntity q : activePlayerQuestEntities) {
                IQuestData questData = q.getQuest().getQuestData();
                if (questData == null) continue;

                completed = questData.tryComplete(player, player.getLocation(), clickedTown) || completed;
                if (completed) {
                    townRepository.increaseTownXp(q.getQuest().getStartTown().getId(), questData.getXpValue());
                    townRepository.increaseTownXp(clickedTown.getId(), questData.getXpValue() / 2);
                    questRepository.removeActivePlayerQuestById(q.getId());
                    questRepository.updateStatus(q.getQuest().getId(), QuestStatus.COMPLETED);
                }
            }
        }

        if (completed) {
            player.swingHand(EquipmentSlot.HAND);
            return;
        }

        // Can only do quests for towns that you are a member of
        List<TownMemberEntity> townMember = townRepository.getTownMember(player.getUniqueId()).stream().filter(tm -> tm.getTown().getId().equals(clickedTown.getId())).toList();
        if (townMember.isEmpty()) return;

        QuestMenu questMenu = new QuestMenu(clickedTown, event.getPlayer());
        questMenu.displayTo(event.getPlayer());

        player.swingHand(EquipmentSlot.HAND);
    }

    private void resolveInteractAtQuestGiver(PlayerInteractAtEntityEvent event) {
        Entity rightClicked = event.getRightClicked();
        Long questId = rightClicked.getPersistentDataContainer().get(QUEST_GIVER_ID_KEY, PersistentDataType.LONG);
        if (questId == null) return;

        QuestEntity quest = questRepository.getQuest(questId);
        if (quest == null) return;

        IQuestData questData = questRepository.getQuestData(questId, quest.getQuestType());
        if (questData == null) return;

        questData.start(event.getPlayer(), rightClicked.getLocation());
        questRepository.updateStatus(questId, QuestStatus.ACTIVE);

        World world = rightClicked.getWorld();

        world.spawnParticle(Particle.CLOUD, rightClicked.getLocation(), 5, .1, .1, .1, 0);
        world.playSound(rightClicked.getLocation(), Sound.BLOCK_DECORATED_POT_INSERT, 1, 1);

        rightClicked.remove();
    }

    public void createRandomQuest(TownEntity startTown) {
        List<QuestEntity> quests = questRepository.getQuests(startTown).stream().filter(q -> (q.getStatus() == QuestStatus.NEW || q.getStatus() == QuestStatus.ACTIVE) && !q.canDespawn()).toList();
        TownLevelConfig levelConfig = startTown.getLevelConfig();
        if (quests.size() >= levelConfig.getMaxSimultaneousQuests()) return;

        List<TownEntity> list = townRepository.getTowns().stream().filter(t -> !t.getId().equals(startTown.getId())).toList();
        if (list.isEmpty()) return;

        // TODO: Add other quest types
        ItemDeliveryQuestEntity quest = ItemDeliveryQuestEntity.createRandom(startTown, list.get(random.nextInt(list.size())));
        questRepository.insertQuest(quest);

        CobaltKingdoms.getInstance().getLogger().info("Created new quest");
    }

    public QuestManager(CobaltKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        Bukkit.getScheduler().runTaskTimer(CobaltKingdoms.getInstance(), this::tickQuestGiverEntities, 1, 20 * 7);
        Bukkit.getScheduler().runTaskTimer(CobaltKingdoms.getInstance(), this::validateQuests, 1, 20 * 60);
        Bukkit.getPluginManager().registerEvents(this, CobaltKingdoms.getInstance());
    }

    @Override
    public void disable() {

    }

    private static QuestManager INSTANCE;

    public static QuestManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new QuestManager(CobaltKingdoms.getInstance());
        }
        return INSTANCE;
    }

    private void validateQuests() {
        List<QuestEntity> quests = questRepository.getQuests();
        for (QuestEntity quest : quests) {
            IQuestData questData = quest.getQuestData();
            if (questData == null) {
                questRepository.updateStatus(quest.getId(), QuestStatus.DESPAWNED);
                continue;
            }

            if (quest.isValid()) continue;
            questRepository.updateStatus(quest.getId(), QuestStatus.DESPAWNED);
        }

        List<QuestEntity> newQuests = quests.stream().filter(q -> q.getStatus() == QuestStatus.NEW).toList();

        // Iterate over quests that have not been claimed
        for (QuestEntity quest : newQuests) {
            Instant createdTimestamp = quest.getCreatedTimestamp().toInstant();
            Instant expiresAt = createdTimestamp.plus(Duration.ofMinutes(60));
            if (expiresAt.isAfter(Instant.now())) continue;
            if (!quest.canDespawn()) continue;

            // Mark the quest as despawned
            questRepository.updateStatus(quest.getId(), QuestStatus.DESPAWNED);
        }

        // Iterate over quests that are active
        List<ActivePlayerQuestEntity> activeQuests = questRepository.getActiveQuests();
        for (ActivePlayerQuestEntity quest : activeQuests) {
            Instant expiryTime = quest.getExpiryTime().toInstant();
            if (expiryTime.isAfter(Instant.now())) continue;
            if (!quest.getQuest().canDespawn()) continue;

            // Mark the quest as failed
            questRepository.updateStatus(quest.getQuest().getId(), QuestStatus.FAILED);
            // TODO: Send a message to the player if they are online
        }

    }

    private void tickQuestGiverEntities() {
        for (Map.Entry<Location, Parrot> locationParrotEntry : QUEST_GIVER_ENTITIES.entrySet()) {
            Location location = locationParrotEntry.getKey();
            Parrot parrot = locationParrotEntry.getValue();

            if (location.isChunkLoaded()) {
                parrot.getPathfinder().moveTo(location);
            } else {
                parrot.remove();
            }
        }

    }

    public void summonQuestMarker(@NotNull Location spawnPosition, QuestEntity quest) {
        if (!spawnPosition.isChunkLoaded()) return;

        IQuestData questData = questRepository.getQuestData(quest.getId(), quest.getQuestType());
        if (questData == null) return;

        World world = spawnPosition.getWorld();
        Block highestBlock = world.getHighestBlockAt(spawnPosition.getBlockX(), spawnPosition.getBlockZ());

        Location spawnLocationOnGround = new Location(spawnPosition.getWorld(), spawnPosition.x(), highestBlock.getY() + random.nextInt(1, 5), spawnPosition.z());

        CobaltKingdoms.getInstance().getLogger().info("Spawning quest giver at " + spawnLocationOnGround.toVector());

        Parrot questGiver = world.spawn(spawnLocationOnGround, Parrot.class, parrot -> {
            parrot.setGlowing(true);
            parrot.getPersistentDataContainer().set(QUEST_GIVER_ID_KEY, PersistentDataType.LONG, quest.getId());
            parrot.setCustomNameVisible(false);
            parrot.setVariant(quest.getQuestType().parrotVariant);
            parrot.customName(questData.getTitle());
        });
        world.spawnParticle(Particle.CLOUD, spawnLocationOnGround, 5, .1, .1, .1, 0);
        world.playSound(spawnLocationOnGround, Sound.BLOCK_DECORATED_POT_INSERT, 1, 1);

        QUEST_GIVER_ENTITIES.put(spawnPosition, questGiver);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();

        // Check if we need to change drops
        String serializedDrops = entity.getPersistentDataContainer().get(DROPS_KEY, PersistentDataType.STRING);
        if (serializedDrops != null) {
            List<ItemStack> drops = event.getDrops();
            drops.clear();
            List<ItemStack> newDrops = ItemSerializationUtils.deserializeItemStacks(serializedDrops);
            drops.addAll(newDrops);
        }

        // Check if the entity is part of the caravan for a mission
        Long questId = entity.getPersistentDataContainer().get(QUEST_KEY, PersistentDataType.LONG);
        if (questId != null) {
            // Get the mission from the database
            Optional<ActivePlayerQuestEntity> activePlayerQuest = questRepository.getActivePlayerQuestByQuestId(questId);

            // Check if the mission is still active (should always be the case or the tag would have been removed)
            if (activePlayerQuest.isPresent()) {
                // Send the player a message and play a sound
                UUID playerUUID = activePlayerQuest.get().getPlayerUUID();
                Player player = Bukkit.getPlayer(playerUUID);
                if (player != null) {
                    player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.6f, 0.7f);
                }

                questRepository.updateStatus(questId, QuestStatus.FAILED);
                questRepository.removeActivePlayerQuestById(activePlayerQuest.get().getId());
            } else {
                CobaltKingdoms.getInstance().getLogger().warning(
                        "Deceased entity had mission metadata, but the "
                                + "mission was not in the active mission database");
            }
        }
    }
}
