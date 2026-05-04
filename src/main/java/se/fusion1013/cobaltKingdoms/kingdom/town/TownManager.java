package se.fusion1013.cobaltKingdoms.kingdom.town;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.database.kingdom.town.ITownDao;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomData;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomInfo;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomManager;
import se.fusion1013.cobaltKingdoms.quest.QuestManager;

import java.util.*;

public class TownManager extends Manager<CobaltKingdoms> implements Listener {

    public static final NamespacedKey TOWN_ENTITY_KEY = new NamespacedKey(CobaltKingdoms.getInstance(), "town_entity");

    private static final Map<UUID, TownData> TOWNS = new HashMap<>();
    private static final Random random = new Random();

    private static final int QuestRefreshTimer = 20 * 60 * 30;
    private static final int QuestEntityRespawnTimer = 20 * 60 * 90;

    public boolean createTown(String townName, Player player, Location location) {
        KingdomInfo playerKingdomInfo = KingdomManager.getInstance().getPlayerKingdomInfo(player.getUniqueId());
        if (playerKingdomInfo == null) return false;

        KingdomData kingdomData = KingdomManager.getInstance().getKingdomData(playerKingdomInfo.name());

        TownData newTown = new TownData(townName, kingdomData.getId(), player.getUniqueId(), location);
        TOWNS.put(newTown.uuid(), newTown);
        DataManager.getInstance().getDao(ITownDao.class).insertTown(newTown);

        spawnTownEntity(location, newTown.uuid());

        return true;
    }

    public boolean moveTown(Player player, String townName, Location newLocation) {
        TownData townData = getTown(townName);
        if (townData == null) return false;
        if (townData.ownerUuid() != player.getUniqueId() || !player.isOp()) return false;

        removeTownEntities(townData);

        townData.moveTo(newLocation);

        TOWNS.put(townData.uuid(), townData);
        DataManager.getInstance().getDao(ITownDao.class).insertTown(townData);

        spawnTownEntity(newLocation, townData.uuid());

        return true;
    }

    public boolean deleteTown(Player player, String townName) {
        for (TownData town : TOWNS.values()) {
            if (!town.townName().equalsIgnoreCase(townName)) continue;
            if (town.ownerUuid() != player.getUniqueId()) continue;

            TOWNS.remove(town.uuid());
            DataManager.getInstance().getDao(ITownDao.class).deleteTown(town.uuid());
            removeTownEntities(town);
            return true;
        }

        return false;
    }

    private void removeTownEntities(TownData town) {
        Location location = town.getLocation();
        World world = location.getWorld();

        Collection<Entity> entities = world.getNearbyEntities(location, 16, 16, 16, entity -> entity.getPersistentDataContainer().has(TOWN_ENTITY_KEY) || entity.getPersistentDataContainer().has(QuestManager.QUEST_GIVER_ID_KEY));
        for (Entity entity : entities) {
            entity.remove();
        }
    }

    private void spawnTownEntity(Location location, UUID townId) {
        World world = location.getWorld();

        world.spawn(location, Villager.class, villager -> {
            villager.setInvulnerable(true);
            villager.setAI(false);
            villager.setGlowing(true);
            villager.setProfession(Villager.Profession.NITWIT);
            villager.getPersistentDataContainer().set(TOWN_ENTITY_KEY, PersistentDataType.STRING, townId.toString());

            ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
            ItemMeta meta = chestplate.getItemMeta();
            meta.addEnchant(Enchantment.THORNS, 3, false);
            meta.setUnbreakable(true);
            chestplate.setItemMeta(meta);
            villager.getEquipment().setChestplate(chestplate);
        });
    }

    public boolean isTown(Entity entity, UUID townId) {
        if (!entity.getPersistentDataContainer().has(TOWN_ENTITY_KEY)) return false;
        String keyValue = entity.getPersistentDataContainer().get(TOWN_ENTITY_KEY, PersistentDataType.STRING);
        if (keyValue == null) return false;
        return keyValue.equalsIgnoreCase(townId.toString());
    }

    public TownManager(CobaltKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        ITownDao townDao = DataManager.getInstance().getDao(ITownDao.class);
        townDao.getTownData().forEach(t -> {
            TOWNS.put(t.uuid(), t);
        });

        Bukkit.getScheduler().runTaskTimer(CobaltKingdoms.getInstance(), () -> tickQuests(), 1, QuestRefreshTimer);
        Bukkit.getScheduler().runTaskTimer(CobaltKingdoms.getInstance(), this::tickTownVerification, 1, 20 * 60);
        Bukkit.getPluginManager().registerEvents(this, CobaltKingdoms.getInstance());
    }

    private void tickTownVerification() {
        for (TownData town : TOWNS.values()) {
            Location location = town.getLocation();
            World world = location.getWorld();

            Collection<Entity> entities = world.getNearbyEntities(location, 5, 5, 5, entity -> entity.getPersistentDataContainer().has(TOWN_ENTITY_KEY));
            if (entities.isEmpty()) {
                spawnTownEntity(location, town.uuid());
            }
        }
    }

    private void tickQuests() {
        for (TownData town : TOWNS.values()) {
            tickQuests(town);
        }
    }

    private void tickQuests(TownData town) {
        Location townCenter = town.getLocation();
        World world = townCenter.getWorld();
        if (!townCenter.isChunkLoaded()) return;

        Collection<Parrot> questGiverEntities = townCenter.getNearbyEntitiesByType(Parrot.class,
                16,
                parrot -> parrot.getPersistentDataContainer().has(QuestManager.QUEST_GIVER_ID_KEY)
        );

        // Remove quest givers if they have been alive for too long
        for (Parrot parrot : questGiverEntities) {
            if (parrot.getTicksLived() <= QuestEntityRespawnTimer) continue;
            parrot.remove();
            world.spawnParticle(Particle.CLOUD, parrot.getLocation(), 5, .1, .1, .1, 0);
            world.playSound(parrot.getLocation(), Sound.BLOCK_DECORATED_POT_INSERT, 1, 1);
        }

        int questGiversToSummon = 3 - questGiverEntities.size();
        if (questGiversToSummon <= 0) return;

        // Summon new quest giver entity
        int xPos = random.nextInt(-6, 6);
        int yPos = random.nextInt(-6, 6);

        QuestManager.getInstance().summonQuestGiver(townCenter.clone().add(xPos, 0, yPos), town);
    }

    @Override
    public void disable() {

    }

    private static TownManager INSTANCE;

    public static TownManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TownManager(CobaltKingdoms.getInstance());
        }
        return INSTANCE;
    }

    public List<TownData> getTowns() {
        return TOWNS.values().stream().toList();
    }

    public TownData getPlayerTown(Player player) {
        return TOWNS.values().stream().filter(t -> t.ownerUuid() == player.getUniqueId()).findFirst().orElse(null);
    }

    public TownData getTown(String townName) {
        return TOWNS.values().stream().filter(t -> t.townName().equalsIgnoreCase(townName)).findFirst().orElse(null);
    }

    public String[] getTownNames() {
        return getTowns().stream().map(TownData::townName).toArray(String[]::new);
    }
}
