package se.fusion1013.cobaltKingdoms.quest.service;

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
import se.fusion1013.cobaltKingdoms.quest.model.*;
import se.fusion1013.cobaltKingdoms.quest.repository.IQuestArtifactHuntRepository;
import se.fusion1013.cobaltKingdoms.quest.repository.IQuestItemDeliveryRepository;
import se.fusion1013.cobaltKingdoms.quest.repository.IQuestRepository;
import se.fusion1013.cobaltKingdoms.quest.task.QuestGiverTask;
import se.fusion1013.cobaltKingdoms.quest.task.QuestValidationTask;
import se.fusion1013.cobaltKingdoms.town.config.TownLevelConfig;
import se.fusion1013.cobaltKingdoms.town.model.Town;
import se.fusion1013.cobaltKingdoms.town.model.TownMember;
import se.fusion1013.cobaltKingdoms.town.repository.ITownRepository;
import se.fusion1013.cobaltKingdoms.town.service.TownManager;
import se.fusion1013.cobaltKingdoms.town.util.TownUtil;
import se.fusion1013.cobaltKingdoms.util.ItemSerializationUtils;

import java.util.*;

import static se.fusion1013.cobaltKingdoms.quest.model.ItemDeliveryQuest.DROPS_KEY;
import static se.fusion1013.cobaltKingdoms.quest.model.ItemDeliveryQuest.QUEST_KEY;
import static se.fusion1013.cobaltKingdoms.quest.task.QuestGiverTask.QUEST_GIVER_ENTITIES;

public class QuestManager extends Manager<CobaltKingdoms> implements Listener {

    public static final NamespacedKey QUEST_ID_KEY = new NamespacedKey(CobaltKingdoms.getInstance(), "quest_id");
    public static final NamespacedKey QUEST_GIVER_ID_KEY = new NamespacedKey(CobaltKingdoms.getInstance(), "quest_giver_id");

    private static final IQuestRepository questRepository = DataManager.getInstance().getDao(IQuestRepository.class);
    private static final IQuestArtifactHuntRepository artifactHuntQuestRepository = DataManager.getInstance().getDao(IQuestArtifactHuntRepository.class);
    private static final ITownRepository townRepository = DataManager.getInstance().getDao(ITownRepository.class);
    private static final IQuestItemDeliveryRepository itemDeliveryRepository = DataManager.getInstance().getDao(IQuestItemDeliveryRepository.class);

    private static final Random random = new Random();

    public void resolveInteractAtTownEntity(Player player, Entity townEntity) {
        Long townId = TownUtil.getTownId(townEntity);
        if (townId == null) return;

        boolean completed = tryCompleteQuest(player, townId);

        if (completed) {
            player.swingHand(EquipmentSlot.HAND);
            return;
        }

        // Can only open quest menu for towns that you are a member of
        if (!isInTown(player, townId)) return;

        Town startTown = TownManager.getInstance().getTown(townId);
        QuestMenu questMenu = new QuestMenu(startTown, player);
        questMenu.displayTo(player);

        player.swingHand(EquipmentSlot.HAND);
    }

    private static boolean tryCompleteQuest(Player player, Long townId) {
        boolean completed = false;

        List<PlayerQuest> playerQuestsByPlayer = questRepository.getPlayerQuestsByPlayer(player);
        if (playerQuestsByPlayer.isEmpty()) return false;

        List<PlayerQuest> playerQuestsByPlayerFiltered = playerQuestsByPlayer.stream()
                .filter(Objects::nonNull)
                .filter(q -> Objects.nonNull(q.getQuest()))
                .filter(q -> q.getQuest().getQuestStatus() == QuestStatus.ACTIVE)
                .toList();

        for (PlayerQuest playerQuest : playerQuestsByPlayerFiltered) {
            AbstractQuest headlessQuest = playerQuest.getQuest(); // TODO: This is dumb, prevent this

            AbstractQuest realQuest = questRepository.getQuest(headlessQuest.getQuestId()).orElse(null);
            if (realQuest == null) continue;

            completed = realQuest.tryComplete(player, player.getLocation(), townId);
            if (!completed) continue;

            townRepository.increaseTownXp(playerQuest.getQuest().getStartTown().getId(), realQuest.getXpValue());
            townRepository.increaseTownXp(townId, realQuest.getXpValue() / 2);
            questRepository.updateStatus(realQuest.getQuestId(), QuestStatus.COMPLETED);
            break;
        }
        return completed;
    }

    private boolean isInTown(Player player, Town town) {
        return isInTown(player, town.getId());
    }

    private boolean isInTown(Player player, Long townId) {
        List<TownMember> townMembers = townRepository.getTownMember(player.getUniqueId());
        return townMembers.stream().anyMatch(townMember -> townMember.getTownId().equals(townId));
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
        List<PlayerQuest> activeQuests = questRepository.getPlayerQuestsByPlayer(player);
        if (activeQuests.isEmpty()) return;

        // Fail all active quests
        for (PlayerQuest activeQuest : activeQuests) {
            AbstractQuest quest = activeQuest.getQuest();
            if (quest.getQuestStatus() != QuestStatus.ACTIVE) continue;

            AbstractQuest questImplemented = questRepository.getQuest(quest.getQuestId()).orElse(null);
            if (questImplemented == null) continue;

            questRepository.updateStatus(questImplemented.getQuestId(), QuestStatus.FAILED);
            questImplemented.fail(player, QuestFailReason.DEATH);
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
        Bukkit.getScheduler().runTaskTimer(CobaltKingdoms.getInstance(), new QuestGiverTask(), 1, 20 * 7);
        Bukkit.getScheduler().runTaskTimer(CobaltKingdoms.getInstance(), new QuestValidationTask(), 1, 20 * 60);
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

    public List<PlayerQuest> getPlayerQuests(Player player) {
        return questRepository.getPlayerQuestsByPlayer(player);
    }

    public void setQuestStatus(Long questId, QuestStatus status) {
        questRepository.updateStatus(questId, status);
    }
}
