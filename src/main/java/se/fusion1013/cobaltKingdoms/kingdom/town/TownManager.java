package se.fusion1013.cobaltKingdoms.kingdom.town;

import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.config.KingdomsConfig;
import se.fusion1013.cobaltKingdoms.config.town.TownConfig;
import se.fusion1013.cobaltKingdoms.config.town.TownLevelConfig;
import se.fusion1013.cobaltKingdoms.database.kingdom.town.ITownRepository;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomData;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomInfo;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomManager;
import se.fusion1013.cobaltKingdoms.quest.QuestManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class TownManager extends Manager<CobaltKingdoms> implements Listener {

    public static final NamespacedKey TOWN_ENTITY_KEY = new NamespacedKey(CobaltKingdoms.getInstance(), "town_entity");
    private static final ITownRepository townRepository = DataManager.getInstance().getDao(ITownRepository.class);

    private static final List<Consumer<TownEntity>> onTownSpawn = new ArrayList<>();

    private int TownVerificationTimer = 20 * 60;

    // ##%%##%%## TOWN ##%%##%%## //

    public Response createTown(String townName, Player player, Location location) {
        KingdomInfo playerKingdomInfo = KingdomManager.getInstance().getPlayerKingdomInfo(player.getUniqueId());
        if (playerKingdomInfo == null) return Response.error("You are not part of a kingdom");

        KingdomData kingdomData = KingdomManager.getInstance().getKingdomData(playerKingdomInfo.name());

        List<TownMemberEntity> townMember = townRepository.getTownMember(player.getUniqueId());
        if (townMember != null && !townMember.isEmpty() && !player.isOp())
            return Response.error("You are already a member of a town");

        TownEntity townWithSameName = townRepository.getTownByName(townName);
        if (townWithSameName != null && !player.isOp()) return Response.error("A town with that name already exists");

        Response canPlaceHere = verifyTownPlacement(location, null);
        if (canPlaceHere.error() && !player.isOp())
            return Response.error("Invalid town placement, " + canPlaceHere.message());

        TownEntity newTown = new TownEntity(townName, kingdomData.getId(), player.getUniqueId(), location);
        DataManager.getInstance().getDao(ITownRepository.class).createTown(player, newTown);

        return Response.ok("Created new town");
    }

    public Response moveTown(Player player, String townName, Location newLocation) {
        TownEntity townEntity = getTown(townName);
        if (townEntity == null) return Response.error("Could not find town with that name");

        if (!townEntity.getOwnerId().equals(player.getUniqueId()) && !player.isOp())
            return Response.error("You do not have permission to move this town");

        Response canPlaceHere = verifyTownPlacement(newLocation, townEntity.getId());
        if (canPlaceHere.error()) return Response.error("Invalid town placement, " + canPlaceHere.message());

        removeTownEntities(townEntity);
        townEntity.moveTo(newLocation);
        DataManager.getInstance().getDao(ITownRepository.class).updateTown(townEntity);

        return Response.ok("Moved town");
    }

    public Response deleteTown(Player player, String townName) {
        TownEntity town = townRepository.getTownByName(townName);
        if (town == null) return Response.error("Could not find town");

        if (!town.getOwnerId().equals(player.getUniqueId()) && !player.isOp())
            return Response.error("You do not have permission to remove this town");

        DataManager.getInstance().getDao(ITownRepository.class).deleteTown(town.getId());
        removeTownEntities(town);

        return Response.ok("Removed town");
    }

    public Response hasTownEditPermissions(Player player, String townName) {
        TownEntity town = townRepository.getTownByName(townName);
        if (town == null) return Response.error("Could not find town");
        return hasTownEditPermissions(player, town);
    }

    private static @NotNull Response hasTownEditPermissions(Player player, TownEntity town) {
        if (!town.getOwnerId().equals(player.getUniqueId()) && !player.isOp())
            return Response.error("You do not have permission to remove this town");

        return Response.ok("You have permission to edit this town");
    }

    public Response addPlayer(String townName, Player invitePlayer) {
        TownEntity town = getTown(townName);
        if (town == null) return Response.error("Could not find town");

        townRepository.addTownMember(town.getId(), invitePlayer);
        return Response.ok("Added player to town");
    }

    public Response removeTownPlayer(Player player, Player kickPlayer) {
        TownEntity playerTown = TownManager.getInstance().getPlayerTown(player);
        if (playerTown == null) return Response.error("You are not part of a town");

        if (playerTown.getOwnerId().equals(kickPlayer.getUniqueId()))
            return Response.error("Cannot remove the owner of the town");

        Response hasTownEditPermissions = TownManager.getInstance().hasTownEditPermissions(player, playerTown.getName());
        if (hasTownEditPermissions.error() && !(player.getUniqueId().equals(kickPlayer.getUniqueId())))
            return hasTownEditPermissions;

        townRepository.removePlayerMember(kickPlayer);
        return Response.ok("Removed player from town");
    }

    // ##%%##%%## JAIL ##%%##%%## //

    public Response createJail(Player player, String jailName, Location location) {
        TownEntity town = getPlayerTown(player);
        if (town == null) return Response.error("You are not a member of a town");

        Response hasEditPermissions = hasTownEditPermissions(player, town);
        if (hasEditPermissions.error()) return hasEditPermissions;

        TownJailEntity jail = new TownJailEntity();
        jail.setName(jailName);
        jail.setTown(town);
        jail.setLocation(location);

        townRepository.createJail(jail);
        return Response.ok("Created jail");
    }

    public Response deleteJail(Player player, String jailName) {
        TownEntity town = getPlayerTown(player);
        if (town == null) return Response.error("You are not a member of a town");

        Response hasEditPermissions = hasTownEditPermissions(player, town);
        if (hasEditPermissions.error()) return hasEditPermissions;

        boolean deleted = townRepository.deleteJail(town.getId(), jailName);
        if (deleted) return Response.ok("Deleted jail");
        return Response.error("Could not find jail");
    }

    // ##%%##%%## OTHER ##%%##%%## //

    private void removeTownEntities(TownEntity town) {
        Location location = town.getLocation();
        World world = location.getWorld();

        Collection<Entity> entities = world.getNearbyEntities(location, 16, 16, 16, entity -> entity.getPersistentDataContainer().has(TOWN_ENTITY_KEY) || entity.getPersistentDataContainer().has(QuestManager.QUEST_GIVER_ID_KEY));
        for (Entity entity : entities) {
            entity.remove();
        }
    }

    private Response verifyTownPlacement(Location location, Long ignoreTownWithId) {
        List<TownEntity> towns = townRepository.getTowns();
        TownConfig townConfig = KingdomsConfig.getTownConfig();

        if (location.getBlock().getType() != Material.AIR)
            return Response.error("Town center needs to be an air block");
        if (location.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR)
            return Response.error("Town needs to be placed on the ground");

        for (TownEntity town : towns) {
            if (town.getId().equals(ignoreTownWithId)) continue;
            double distance = town.getLocation().distance(location);
            if (distance < townConfig.getTownMinSpacing()) return Response.error("Too close to another town");
        }

        return Response.ok("Valid town location");
    }

    private void spawnTownEntity(TownEntity town) {
        Location location = town.getLocation();
        World world = location.getWorld();

        Collection<Mannequin> nearbyEntitiesByType = world.getNearbyEntitiesByType(Mannequin.class, location, 5, v -> v.getPersistentDataContainer().has(TOWN_ENTITY_KEY));
        if (!nearbyEntitiesByType.isEmpty()) return;

        world.spawn(location, Mannequin.class, mannequin -> {
            mannequin.setInvulnerable(true);
            mannequin.setAI(false);
            mannequin.setGlowing(true);
            mannequin.getPersistentDataContainer().set(TOWN_ENTITY_KEY, PersistentDataType.LONG, town.getId());
            mannequin.setProfile(ResolvableProfile.resolvableProfile().name("Fusion1013").build());
            mannequin.customName(Component.text(town.getName()));
        });

        CobaltKingdoms.getInstance().getLogger().info("Ticking town spawn listeners: " + onTownSpawn.size());
        for (Consumer<TownEntity> townEntityConsumer : onTownSpawn) {
            townEntityConsumer.accept(town);
        }

    }

    public boolean isTown(Entity entity, Long townId) {
        if (!entity.getPersistentDataContainer().has(TOWN_ENTITY_KEY)) return false;
        Long keyValue = entity.getPersistentDataContainer().get(TOWN_ENTITY_KEY, PersistentDataType.LONG);
        if (keyValue == null) return false;
        return keyValue.equals(townId);
    }

    public TownEntity getTown(Entity entity) {
        Long key = entity.getPersistentDataContainer().get(TOWN_ENTITY_KEY, PersistentDataType.LONG);
        return getTowns().stream().filter(t -> Objects.equals(t.getId(), key)).findFirst().orElse(null);
    }

    public TownManager(CobaltKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        loadConfigValues();

        Bukkit.getScheduler().runTaskTimer(CobaltKingdoms.getInstance(), this::displayTownParticles, 1, 10);
        Bukkit.getScheduler().runTaskTimer(CobaltKingdoms.getInstance(), this::tickTownVerification, 1, TownVerificationTimer);
        Bukkit.getPluginManager().registerEvents(this, CobaltKingdoms.getInstance());
    }

    private void displayTownParticles() {
        townRepository.getTowns().forEach(town -> {
            Location location = town.getLocation();
            World world = location.getWorld();
            world.spawnParticle(Particle.END_ROD, location, 3, .2, .2, .2, 0);
        });
    }

    private void tickTownVerification() {
        townRepository.getTowns().forEach(town -> {
            if (isTownLoaded(town)) {
                spawnTownEntity(town);
            } else {
                removeTownEntities(town);
            }
        });
    }

    private boolean isTownLoaded(TownEntity town) {
        double closestPlayerDistance = Double.MAX_VALUE;
        for (Player p : Bukkit.getOnlinePlayers()) {
            double distance = p.getLocation().distanceSquared(town.getLocation());
            if (distance < closestPlayerDistance) closestPlayerDistance = distance;
        }
        return closestPlayerDistance < 16 * 16;
    }

    private void loadConfigValues() {
        FileConfiguration config = CobaltKingdoms.getInstance().getConfig();
        ConfigurationSection townConfig = config.getConfigurationSection("town");
        if (townConfig == null) return;

        int townVerificationTimerMinutes = townConfig.getInt("town_verification_timer_m");
        int townVerificationTimerSeconds = townConfig.getInt("town_verification_timer_s");
        this.TownVerificationTimer = 20 * townVerificationTimerSeconds + 20 * 60 * townVerificationTimerMinutes;
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

    // ##%%##%%## SUBSCRIPTIONS ##%%##%%## //

    public void onTownSpawn(Consumer<TownEntity> eventConsumer) {
        onTownSpawn.add(eventConsumer);
    }

    // ##%%##%%## GETTERS / SETTERS ##%%##%%## //

    public List<TownEntity> getTowns() {
        return townRepository.getTowns();
    }

    public TownEntity getPlayerTown(Player player) {
        return townRepository.getTownByOwner(player.getUniqueId());
    }

    public TownEntity getTown(String townName) {
        return townRepository.getTownByName(townName);
    }

    public String[] getTownNames() {
        return getTowns().stream().map(TownEntity::getName).toArray(String[]::new);
    }

    public String[] getJailNames(Player player) {
        return getJails(player).stream().map(TownJailEntity::getName).toArray(String[]::new);
    }

    public List<TownJailEntity> getJails(Player player) {
        TownEntity playerTown = getPlayerTown(player);
        return townRepository.getJails(playerTown.getId());
    }

    public TownJailEntity getJail(Player player, String jailName) {
        TownEntity playerTown = getPlayerTown(player);
        return townRepository.getJailByName(playerTown.getId(), jailName).orElse(null);
    }

    public List<TownMemberEntity> getTownMembers(Long townId) {
        return townRepository.getTownMembersByTownId(townId);
    }

    public void setTownLevel(String townName, int level) {
        TownEntity town = getTown(townName);
        if (town == null) return;

        TownConfig townConfig = KingdomsConfig.getTownConfig();

        int totalXp = 0;
        for (int i = 0; i <= level; i++) {
            TownLevelConfig townLevelConfig = townConfig.getTownLevelConfigFromLevel(i);
            if (townLevelConfig == null) continue;
            totalXp += townLevelConfig.getXpThreshold();
        }
        town.setExperience(totalXp);
        townRepository.updateTown(town);
    }
}
