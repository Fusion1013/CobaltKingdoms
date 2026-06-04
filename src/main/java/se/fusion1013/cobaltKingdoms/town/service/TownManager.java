package se.fusion1013.cobaltKingdoms.town.service;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.config.KingdomsConfig;
import se.fusion1013.cobaltKingdoms.kingdom.model.KingdomData;
import se.fusion1013.cobaltKingdoms.kingdom.model.KingdomInfo;
import se.fusion1013.cobaltKingdoms.kingdom.service.KingdomManager;
import se.fusion1013.cobaltKingdoms.town.config.TownConfig;
import se.fusion1013.cobaltKingdoms.town.config.TownLevelConfig;
import se.fusion1013.cobaltKingdoms.town.model.Town;
import se.fusion1013.cobaltKingdoms.town.model.TownAppearance;
import se.fusion1013.cobaltKingdoms.town.model.TownMember;
import se.fusion1013.cobaltKingdoms.town.repository.ITownRepository;
import se.fusion1013.cobaltKingdoms.town.task.TownParticleTask;
import se.fusion1013.cobaltKingdoms.town.task.TownSpawnerTask;
import se.fusion1013.cobaltKingdoms.town.util.TownUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static se.fusion1013.cobaltKingdoms.town.util.TownUtil.TOWN_ENTITY_KEY;

public class TownManager extends Manager<CobaltKingdoms> implements Listener {

    private static final ITownRepository townRepository = DataManager.getInstance().getDao(ITownRepository.class);

    private static final List<Consumer<Town>> onTownSpawn = new ArrayList<>();

    private TownConfig TOWN_CONFIG;

    // ##%%##%%## TOWN ##%%##%%## //

    public Response createTown(String townName, String displayName, Player player, Location location) {
        KingdomInfo playerKingdomInfo = KingdomManager.getInstance().getPlayerKingdomInfo(player.getUniqueId());
        if (playerKingdomInfo == null) return Response.error("You are not part of a kingdom");

        KingdomData kingdomData = KingdomManager.getInstance().getKingdomData(playerKingdomInfo.name());

        List<TownMember> townMember = townRepository.getTownMember(player.getUniqueId());
        if (townMember != null && !townMember.isEmpty() && !player.isOp())
            return Response.error("You are already a member of a town");

        Town townWithSameName = townRepository.getTownByName(townName);
        if (townWithSameName != null && !player.isOp()) return Response.error("A town with that name already exists");

        Response canPlaceHere = verifyTownPlacement(location, null);
        if (canPlaceHere.error() && !player.isOp())
            return Response.error("Invalid town placement, " + canPlaceHere.message());

        Town newTown = new Town(player);
        newTown.setName(townName);
        newTown.setKingdomId(kingdomData.getId());
        newTown.setOwnerId(player.getUniqueId());
        newTown.setLocation(location);
        newTown.setDisplayName(displayName);

        DataManager.getInstance().getDao(ITownRepository.class).createTown(player, newTown);

        return Response.ok("Created new town");
    }

    public Response moveTown(Player player, String townName, Location newLocation) {
        Town town = getTown(townName);
        if (town == null) return Response.error("Could not find town with that name");

        if (!town.getOwnerId().equals(player.getUniqueId()) && !player.isOp())
            return Response.error("You do not have permission to move this town");

        Response canPlaceHere = verifyTownPlacement(newLocation, town.getId());
        if (canPlaceHere.error()) return Response.error("Invalid town placement, " + canPlaceHere.message());

        TownUtil.removeTownEntities(town);
        town.moveTo(newLocation);
        DataManager.getInstance().getDao(ITownRepository.class).updateTown(town);

        return Response.ok("Moved town");
    }

    public Response deleteTown(Player player, String townName) {
        Town town = townRepository.getTownByName(townName);
        if (town == null) return Response.error("Could not find town");

        if (!town.getOwnerId().equals(player.getUniqueId()) && !player.isOp())
            return Response.error("You do not have permission to remove this town");

        DataManager.getInstance().getDao(ITownRepository.class).deleteTown(town.getId());
        TownUtil.removeTownEntities(town);

        return Response.ok("Removed town");
    }

    public Response hasTownEditPermissions(Player player, String townName) {
        Town town = townRepository.getTownByName(townName);
        if (town == null) return Response.error("Could not find town");
        return hasTownEditPermissions(player, town);
    }

    public static @NotNull Response hasTownEditPermissions(Player player, Town town) {
        if (!town.getOwnerId().equals(player.getUniqueId()) && !player.isOp())
            return Response.error("You do not have permission to remove this town");

        return Response.ok("You have permission to edit this town");
    }

    public Response addPlayer(String townName, Player invitePlayer) {
        Town town = getTown(townName);
        if (town == null) return Response.error("Could not find town");

        townRepository.addTownMember(town.getId(), invitePlayer);
        return Response.ok("Added player to town");
    }

    public Response removeTownPlayer(Player player, Player kickPlayer) {
        Town playerTown = TownManager.getInstance().getPlayerOwnedTown(player);
        if (playerTown == null) return Response.error("You are not part of a town");

        if (playerTown.getOwnerId().equals(kickPlayer.getUniqueId()))
            return Response.error("Cannot remove the owner of the town");

        Response hasTownEditPermissions = TownManager.getInstance().hasTownEditPermissions(player, playerTown.getName());
        if (hasTownEditPermissions.error() && !(player.getUniqueId().equals(kickPlayer.getUniqueId())))
            return hasTownEditPermissions;

        townRepository.removePlayerMember(kickPlayer);
        return Response.ok("Removed player from town");
    }

    // ##%%##%%## OTHER ##%%##%%## //

    private Response verifyTownPlacement(Location location, Long ignoreTownWithId) {
        List<Town> towns = townRepository.getTowns();
        TownConfig townConfig = KingdomsConfig.getTownConfig();

        if (location.getBlock().getType() != Material.AIR)
            return Response.error("Town center needs to be an air block");
        if (location.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR)
            return Response.error("Town needs to be placed on the ground");

        for (Town town : towns) {
            if (town.getId().equals(ignoreTownWithId)) continue;
            double distance = town.getLocation().distance(location);
            if (distance < townConfig.getTownMinSpacing()) return Response.error("Too close to another town");
        }

        return Response.ok("Valid town location");
    }

    // ##%%##%%## COSMETICS ##%%##%%## //

    public Response modifySkin(Player player, String townName, String skin) {
        Town town = getTown(townName);
        if (town == null) return Response.error("Could not find town");

        Response response = hasTownEditPermissions(player, town);
        if (response.error()) return response;

        TownAppearance appearance = town.getAppearance();
        appearance.setSkin(skin);
        town.setAppearance(appearance);

        townRepository.updateTown(town);
        TownUtil.removeTownEntities(town);

        return Response.ok("Modified town skin");
    }

    public Response modifyTexture(Player player, String townName, String texture) {
        Town town = getTown(townName);
        if (town == null) return Response.error("Could not find town");

        Response response = hasTownEditPermissions(player, town);
        if (response.error()) return response;

        TownAppearance appearance = town.getAppearance();
        appearance.setTexture(texture);
        town.setAppearance(appearance);

        townRepository.updateTown(town);
        TownUtil.removeTownEntities(town);

        return Response.ok("Modified town texture");
    }

    public Response modifyChatGreeting(Player player, String townName, String greeting) {
        Town town = getTown(townName);
        if (town == null) return Response.error("Could not find town");

        Response response = hasTownEditPermissions(player, town);
        if (response.error()) return response;

        TownAppearance appearance = town.getAppearance();
        appearance.setChatGreeting(greeting);
        town.setAppearance(appearance);

        townRepository.updateTown(town);
        TownUtil.removeTownEntities(town);

        return Response.ok("Modified town chat greeting");
    }

    public Response modifyTitleGreeting(Player player, String townName, String greeting) {
        Town town = getTown(townName);
        if (town == null) return Response.error("Could not find town");

        Response response = hasTownEditPermissions(player, town);
        if (response.error()) return response;

        TownAppearance appearance = town.getAppearance();
        appearance.setTitleGreeting(greeting);
        town.setAppearance(appearance);

        townRepository.updateTown(town);
        TownUtil.removeTownEntities(town);

        return Response.ok("Modified town title greeting");
    }

    public Town getTown(Entity entity) {
        Long key = entity.getPersistentDataContainer().get(TOWN_ENTITY_KEY, PersistentDataType.LONG);
        return getTowns().stream().filter(t -> Objects.equals(t.getId(), key)).findFirst().orElse(null);
    }

    public TownManager(CobaltKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        loadConfigValues();

        Bukkit.getScheduler().runTaskTimer(CobaltKingdoms.getInstance(), new TownParticleTask(), 1, 10);
        Bukkit.getScheduler().runTaskTimer(CobaltKingdoms.getInstance(), new TownSpawnerTask(), 1, this.TOWN_CONFIG.getTownVerificationTimer());
        Bukkit.getPluginManager().registerEvents(this, CobaltKingdoms.getInstance());
    }

    private void loadConfigValues() {
        TOWN_CONFIG = KingdomsConfig.getTownConfig();
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

    public void subscribeOnTownSpawn(Consumer<Town> eventConsumer) {
        onTownSpawn.add(eventConsumer);
    }

    public void onTownSpawn(Town town) {
        for (Consumer<Town> townEntityConsumer : onTownSpawn) {
            townEntityConsumer.accept(town);
        }
    }

    // ##%%##%%## GETTERS / SETTERS ##%%##%%## //

    public List<Town> getTowns() {
        return townRepository.getTowns();
    }

    public Town getPlayerOwnedTown(Player player) {
        return townRepository.getTownByOwner(player.getUniqueId());
    }

    // TODO: Probably remove
    public List<Town> getPlayerTowns(Player player) {
        return townRepository.getTownsWithMember(player.getUniqueId());
    }

    public Town getTown(String townName) {
        return townRepository.getTownByName(townName);
    }

    public String[] getTownNames() {
        return getTowns().stream().map(Town::getName).toArray(String[]::new);
    }

    public String[] getTownDisplayNames() {
        return getTowns().stream().map(Town::getDisplayName).toArray(String[]::new);
    }

    public List<TownMember> getTownMembers(Long townId) {
        return townRepository.getTownMembersByTownId(townId);
    }

    public void setTownLevel(String townName, int level) {
        Town town = getTown(townName);
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

    public Town getTown(Long townId) {
        return townRepository.getTown(townId);
    }
}
