package se.fusion1013.cobaltKingdoms.quest;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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
import se.fusion1013.cobaltKingdoms.database.quest.artifact_hunt.IQuestArtifactHuntRepository;
import se.fusion1013.cobaltKingdoms.database.quest.item_delivery.IQuestItemDeliveryRepository;
import se.fusion1013.cobaltKingdoms.kingdom.town.Town;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownManager;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownMember;
import se.fusion1013.cobaltKingdoms.quest.artifact_hunt.ArtifactHuntQuest;
import se.fusion1013.cobaltKingdoms.quest.gui.QuestMenu;
import se.fusion1013.cobaltKingdoms.quest.item_delivery.ItemDeliveryQuest;
import se.fusion1013.cobaltKingdoms.util.ItemSerializationUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static se.fusion1013.cobaltKingdoms.quest.item_delivery.ItemDeliveryQuest.DROPS_KEY;
import static se.fusion1013.cobaltKingdoms.quest.item_delivery.ItemDeliveryQuest.QUEST_KEY;

public class QuestManager extends Manager<CobaltKingdoms> implements Listener {

    public static final NamespacedKey QUEST_ID_KEY = new NamespacedKey(CobaltKingdoms.getInstance(), "quest_id");
    public static final NamespacedKey QUEST_GIVER_ID_KEY = new NamespacedKey(CobaltKingdoms.getInstance(), "quest_giver_id");

    private static final IQuestRepository questRepository = DataManager.getInstance().getDao(IQuestRepository.class);
    private static final IQuestArtifactHuntRepository artifactHuntQuestRepository = DataManager.getInstance().getDao(IQuestArtifactHuntRepository.class);
    private static final ITownRepository townRepository = DataManager.getInstance().getDao(ITownRepository.class);
    private static final IQuestItemDeliveryRepository itemDeliveryRepository = DataManager.getInstance().getDao(IQuestItemDeliveryRepository.class);

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
        Town clickedTown = TownManager.getInstance().getTown(rightClicked);
        if (clickedTown == null) return;

        boolean completed = false;
        Optional<List<PlayerQuest>> activePlayerQuestsByPlayer = questRepository.getPlayerQuestsByPlayer(player);

        if (activePlayerQuestsByPlayer.isPresent()) {
            List<PlayerQuest> activePlayerQuestEntities = activePlayerQuestsByPlayer.get();
            List<PlayerQuest> activePlayerQuestEntitiesFiltered = activePlayerQuestEntities.stream()
                    .filter(Objects::nonNull)
                    .filter(q -> Objects.nonNull(q.getQuest()))
                    .filter(q -> q.getQuest().getQuestStatus() == QuestStatus.ACTIVE)
                    .toList();

            for (PlayerQuest playerQuest : activePlayerQuestEntitiesFiltered) {
                AbstractQuest quest = playerQuest.getQuest();

                completed = quest.tryComplete(player, player.getLocation(), clickedTown);
                if (completed) {
                    townRepository.increaseTownXp(playerQuest.getQuest().getStartTown().getId(), quest.getXpValue());
                    townRepository.increaseTownXp(clickedTown.getId(), quest.getXpValue() / 2);
                    questRepository.updateStatus(quest.getQuestId(), QuestStatus.COMPLETED);
                    break;
                }
            }
        }

        if (completed) {
            player.swingHand(EquipmentSlot.HAND);
            return;
        }

        // Can only do quests for towns that you are a member of
        if (!isInTown(player, clickedTown)) return;

        QuestMenu questMenu = new QuestMenu(clickedTown, event.getPlayer());
        questMenu.displayTo(event.getPlayer());

        player.swingHand(EquipmentSlot.HAND);
    }

    private boolean isInTown(Player player, Town town) {
        List<TownMember> townMembers = townRepository.getTownMember(player.getUniqueId());
        return townMembers.stream().anyMatch(townMember -> townMember.getTownId().equals(town.getId()));
    }

    private void resolveInteractAtQuestGiver(PlayerInteractAtEntityEvent event) {
        Entity rightClicked = event.getRightClicked();
        Long questId = rightClicked.getPersistentDataContainer().get(QUEST_GIVER_ID_KEY, PersistentDataType.LONG);
        if (questId == null) return;

        Optional<AbstractQuest> optionalQuest = questRepository.getQuest(questId);
        if (optionalQuest.isEmpty()) return;

        AbstractQuest quest = optionalQuest.get();

        quest.start(event.getPlayer(), rightClicked.getLocation());
        questRepository.updateStatus(questId, QuestStatus.ACTIVE);

        World world = rightClicked.getWorld();

        world.spawnParticle(Particle.CLOUD, rightClicked.getLocation(), 5, .1, .1, .1, 0);
        world.playSound(rightClicked.getLocation(), Sound.BLOCK_DECORATED_POT_INSERT, 1, 1);

        rightClicked.remove();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        Optional<List<PlayerQuest>> activeQuestsOptional = questRepository.getPlayerQuestsByPlayer(player);
        if (activeQuestsOptional.isEmpty()) return;

        List<PlayerQuest> activeQuests = activeQuestsOptional.get();
        if (activeQuests.isEmpty()) return;

        // Fail all active quests
        for (PlayerQuest activeQuest : activeQuests) {
            AbstractQuest quest = activeQuest.getQuest();
            if (quest.getQuestStatus() != QuestStatus.ACTIVE) continue;

            quest.fail(player, QuestFailReason.DEATH);
            questRepository.updateStatus(quest.getQuestId(), QuestStatus.FAILED);
        }

    }

    public void createRandomQuest(Town startTown) {
        List<AbstractQuest> quests = questRepository.getQuests(startTown).stream()
                .filter(q -> (q.getQuestStatus() == QuestStatus.NEW || q.getQuestStatus() == QuestStatus.ACTIVE) && q.canDespawn())
                .toList();
        TownLevelConfig levelConfig = startTown.getLevelConfig();
        if (quests.size() >= levelConfig.getMaxSimultaneousQuests()) return;

        List<Town> list = townRepository.getTowns().stream().filter(t -> !t.getId().equals(startTown.getId())).toList();
        if (list.isEmpty()) return;

        String randomQuest = levelConfig.getRandomQuest();

        if (randomQuest.equalsIgnoreCase("delivery")) {
            ItemDeliveryQuest quest = ItemDeliveryQuest.createRandom(startTown, list.get(random.nextInt(list.size())));
            if (quest == null) return;
            itemDeliveryRepository.createQuest(quest);

        } else if (randomQuest.equalsIgnoreCase("artifact_hunt")) {
            ArtifactHuntQuest quest = ArtifactHuntQuest.createRandom(startTown, startTown);
            if (quest == null) return;
            artifactHuntQuestRepository.createQuest(quest);
        }
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
        List<AbstractQuest> quests = questRepository.getQuests();
        for (AbstractQuest quest : quests) {
            if (quest.isValid()) continue;
            questRepository.updateStatus(quest.getQuestId(), QuestStatus.DESPAWNED);
        }

        List<AbstractQuest> newQuests = quests.stream().filter(q -> q.getQuestStatus() == QuestStatus.NEW).toList();

        // Iterate over quests that have not been claimed
        for (AbstractQuest quest : newQuests) {
            Instant createdTimestamp = quest.getCreatedTimestamp().toInstant();
            Instant expiresAt = createdTimestamp.plus(Duration.ofMinutes(60));
            if (expiresAt.isAfter(Instant.now())) continue;
            if (!quest.canDespawn()) continue;

            // Mark the quest as despawned
            questRepository.updateStatus(quest.getQuestId(), QuestStatus.DESPAWNED);
        }

        // Iterate over quests that are active
        List<PlayerQuest> activeQuests = questRepository.getPlayerQuests();
        List<PlayerQuest> activeQuestsFiltered = activeQuests.stream()
                .filter(q -> q.getQuest().getQuestStatus() == QuestStatus.NEW || q.getQuest().getQuestStatus() == QuestStatus.ACTIVE)
                .toList();

        for (PlayerQuest quest : activeQuestsFiltered) {
            Instant expiryTime = quest.getExpiryTime().toInstant();
            if (expiryTime.isAfter(Instant.now())) continue;
            if (!quest.getQuest().canDespawn()) continue;

            // Mark the quest as failed
            questRepository.updateStatus(quest.getQuest().getQuestId(), QuestStatus.FAILED);
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

    public void summonQuestMarker(@NotNull Location spawnPosition, AbstractQuest quest) {
        if (!spawnPosition.isChunkLoaded()) return;
        if (true) return; // Run this off for now

        World world = spawnPosition.getWorld();
        Block highestBlock = world.getHighestBlockAt(spawnPosition.getBlockX(), spawnPosition.getBlockZ());

        Location spawnLocationOnGround = new Location(spawnPosition.getWorld(), spawnPosition.x(), highestBlock.getY() + random.nextInt(1, 5), spawnPosition.z());

        CobaltKingdoms.getInstance().getLogger().info("Spawning quest giver at " + spawnLocationOnGround.toVector());

        Parrot questGiver = world.spawn(spawnLocationOnGround, Parrot.class, parrot -> {
            parrot.setGlowing(false);
            parrot.getPersistentDataContainer().set(QUEST_GIVER_ID_KEY, PersistentDataType.LONG, quest.getQuestId());
            parrot.setCustomNameVisible(false);
            parrot.setVariant(quest.getQuestType().parrotVariant);
            parrot.setCustomName(quest.getTitle());
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
            Optional<PlayerQuest> activePlayerQuest = questRepository.getPlayerQuestByQuestId(questId);

            // Check if the mission is still active (should always be the case or the tag would have been removed)
            if (activePlayerQuest.isPresent()) {
                // Send the player a message and play a sound
                UUID playerUUID = activePlayerQuest.get().getPlayerId();
                Player player = Bukkit.getPlayer(playerUUID);
                if (player != null) {
                    player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.6f, 0.7f);
                }

                questRepository.updateStatus(questId, QuestStatus.FAILED);
            } else {
                CobaltKingdoms.getInstance().getLogger().warning(
                        "Deceased entity had mission metadata, but the "
                                + "mission was not in the active mission database");
            }
        }
    }

    public List<AbstractQuest> getAllQuests() {
        return questRepository.getQuests();
    }

    public void setQuestStatus(Long questId, QuestStatus status) {
        questRepository.updateStatus(questId, status);
    }
}
