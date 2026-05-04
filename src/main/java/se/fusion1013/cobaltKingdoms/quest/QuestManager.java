package se.fusion1013.cobaltKingdoms.quest;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownData;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownManager;
import se.fusion1013.cobaltKingdoms.quest.item_delivery.QuestItemDelivery;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static se.fusion1013.cobaltKingdoms.commands.kingdom.KingdomCreateCommand.spawnRandomFirework;

public class QuestManager extends Manager<CobaltKingdoms> implements Listener {

    public static final NamespacedKey QUEST_ID_KEY = new NamespacedKey(CobaltKingdoms.getInstance(), "quest_id");
    public static final NamespacedKey QUEST_GIVER_ID_KEY = new NamespacedKey(CobaltKingdoms.getInstance(), "quest_giver_id");

    private static final Random random = new Random();
    private static final Map<UUID, IQuest> ACTIVE_QUESTS = new HashMap<>();

    // TODO: Replace with a list of some custom class
    // TODO: Store the spawn time and remove the entity after some time
    private static final Map<Location, Parrot> QUEST_GIVER_ENTITIES = new HashMap<>();

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Entity rightClicked = event.getRightClicked();
        if (rightClicked.getPersistentDataContainer().has(QUEST_GIVER_ID_KEY)) resolveInteractAtQuestGiver(event);
        if (rightClicked.getPersistentDataContainer().has(TownManager.TOWN_ENTITY_KEY))
            resolveInteractAtTownEntity(event);
    }

    private void resolveInteractAtTownEntity(PlayerInteractAtEntityEvent event) {
        Entity rightClicked = event.getRightClicked();
        Player player = event.getPlayer();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (!itemInMainHand.getPersistentDataContainer().has(QUEST_ID_KEY)) {
            CobaltKingdoms.getInstance().getLogger().info("Item does not have quest id");
            return;
        }

        String questIdString = itemInMainHand.getPersistentDataContainer().get(QUEST_ID_KEY, PersistentDataType.STRING);
        if (questIdString == null) {
            CobaltKingdoms.getInstance().getLogger().info("Could not find quest id on item");
            return;
        }

        UUID questId = UUID.fromString(questIdString);
        IQuest quest = ACTIVE_QUESTS.get(questId);

        TownData handInTown = quest.getHandInTown();
        if (handInTown == null) {
            CobaltKingdoms.getInstance().getLogger().info("Could not find hand in town");
            return;
        }

        if (!TownManager.getInstance().isTown(rightClicked, handInTown.uuid())) {
            CobaltKingdoms.getInstance().getLogger().info("Incorrect hand in town: " + handInTown.uuid().toString());
            return;
        }

        boolean finishedQuest = quest.tryFinish(player, rightClicked.getLocation());
        if (finishedQuest) {
            ACTIVE_QUESTS.remove(questId);
            player.getInventory().setItemInMainHand(ItemStack.empty());
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            for (int i = 0; i < 5; i++) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        spawnRandomFirework(player);
                    }
                }.runTaskLater(CobaltKingdoms.getInstance(), i * 4);
            }
        }

    }

    private void resolveInteractAtQuestGiver(PlayerInteractAtEntityEvent event) {
        Entity rightClicked = event.getRightClicked();
        String questIdString = rightClicked.getPersistentDataContainer().get(QUEST_GIVER_ID_KEY, PersistentDataType.STRING);
        if (questIdString == null) return;

        UUID questId = UUID.fromString(questIdString);

        IQuest quest = ACTIVE_QUESTS.get(questId);
        if (quest == null) return;

        givePlayerQuest(event.getPlayer(), quest);
        quest.start(event.getPlayer(), rightClicked.getLocation());

        World world = rightClicked.getWorld();

        world.spawnParticle(Particle.CLOUD, rightClicked.getLocation(), 5, .1, .1, .1, 0);
        world.playSound(rightClicked.getLocation(), Sound.BLOCK_DECORATED_POT_INSERT, 1, 1);

        rightClicked.remove();
    }

    private void givePlayerQuest(@NotNull Player player, IQuest quest) {
        ItemStack questToken = quest.getQuestToken();
        ItemStack descriptionItem = quest.getQuestDescriptionItem();

        player.give(questToken);
        player.give(descriptionItem);
    }

    public QuestManager(CobaltKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        Bukkit.getScheduler().runTaskTimer(CobaltKingdoms.getInstance(), this::tickQuestGiverEntities, 1, 20 * 7);
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

    private IQuest createRandomQuest(TownData startTown) {
        int difficulty = random.nextInt(0, 3);
        return QuestItemDelivery.create(startTown, difficulty);
    }

    public void summonQuestGiver(@NotNull Location spawnPosition, TownData town) {
        if (!spawnPosition.isChunkLoaded()) return;

        World world = spawnPosition.getWorld();
        Block highestBlock = world.getHighestBlockAt(spawnPosition.getBlockX(), spawnPosition.getBlockZ());

        Location spawnLocationOnGround = new Location(spawnPosition.getWorld(), spawnPosition.x(), highestBlock.getY() + random.nextInt(1, 5), spawnPosition.z());

        IQuest quest = createRandomQuest(town);
        ACTIVE_QUESTS.put(quest.getId(), quest);

        CobaltKingdoms.getInstance().getLogger().info("Spawning quest giver at " + spawnLocationOnGround.toVector());

        Parrot questGiver = world.spawn(spawnLocationOnGround, Parrot.class, parrot -> {
            parrot.setGlowing(true);
            parrot.getPersistentDataContainer().set(QUEST_GIVER_ID_KEY, PersistentDataType.STRING, quest.getId().toString());
            parrot.customName(quest.getEntityName());
            parrot.setCustomNameVisible(false);
            parrot.setVariant(quest.getQuestType().parrotVariant);
        });
        world.spawnParticle(Particle.CLOUD, spawnLocationOnGround, 5, .1, .1, .1, 0);
        world.playSound(spawnLocationOnGround, Sound.BLOCK_DECORATED_POT_INSERT, 1, 1);

        QUEST_GIVER_ENTITIES.put(spawnPosition, questGiver);
    }
}
