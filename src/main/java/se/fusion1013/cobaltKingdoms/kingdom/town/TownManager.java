package se.fusion1013.cobaltKingdoms.kingdom.town;

import com.destroystokyo.paper.profile.ProfileProperty;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import io.papermc.paper.entity.LookAnchor;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
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
import se.fusion1013.cobaltKingdoms.database.kingdom.town.TownEntity;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomData;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomInfo;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomManager;
import se.fusion1013.cobaltKingdoms.quest.QuestManager;

import java.util.*;
import java.util.function.Consumer;

public class TownManager extends Manager<CobaltKingdoms> implements Listener {

    public static final NamespacedKey TOWN_ENTITY_KEY = new NamespacedKey(CobaltKingdoms.getInstance(), "town_entity");
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

        removeTownEntities(town);
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
        removeTownEntities(town);

        return Response.ok("Removed town");
    }

    public Response hasTownEditPermissions(Player player, String townName) {
        Town town = townRepository.getTownByName(townName);
        if (town == null) return Response.error("Could not find town");
        return hasTownEditPermissions(player, town);
    }

    private static @NotNull Response hasTownEditPermissions(Player player, Town town) {
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

    // ##%%##%%## JAIL ##%%##%%## //

    public Response createJail(Player player, String jailName, Location location) {
        Town town = getPlayerOwnedTown(player);
        if (town == null) return Response.error("You are not a member of a town");

        Response hasEditPermissions = hasTownEditPermissions(player, town);
        if (hasEditPermissions.error()) return hasEditPermissions;

        TownJail jail = new TownJail();
        jail.setName(jailName);
        jail.setLocation(location);

        townRepository.createJail(jail);
        return Response.ok("Created jail");
    }

    public Response deleteJail(Player player, String jailName) {
        Town town = getPlayerOwnedTown(player);
        if (town == null) return Response.error("You are not a member of a town");

        Response hasEditPermissions = hasTownEditPermissions(player, town);
        if (hasEditPermissions.error()) return hasEditPermissions;

        boolean deleted = townRepository.deleteJail(town.getId(), jailName);
        if (deleted) return Response.ok("Deleted jail");
        return Response.error("Could not find jail");
    }

    // ##%%##%%## OTHER ##%%##%%## //

    private void removeTownEntities(Town town) {
        Location location = town.getLocation();
        World world = location.getWorld();

        Collection<Entity> entities = world.getNearbyEntities(location, 16, 16, 16, entity -> entity.getPersistentDataContainer().has(TOWN_ENTITY_KEY) || entity.getPersistentDataContainer().has(QuestManager.QUEST_GIVER_ID_KEY));
        for (Entity entity : entities) {
            entity.remove();
        }
    }

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
        removeTownEntities(town);

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
        removeTownEntities(town);

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
        removeTownEntities(town);

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
        removeTownEntities(town);

        return Response.ok("Modified town title greeting");
    }

    private void spawnTownEntity(Town town) {
        Location location = town.getLocation();
        World world = location.getWorld();

        Collection<Mannequin> nearbyEntitiesByType = world.getNearbyEntitiesByType(Mannequin.class, location, 5, v -> v.getPersistentDataContainer().has(TOWN_ENTITY_KEY));
        if (!nearbyEntitiesByType.isEmpty()) {
            for (Mannequin mannequin : nearbyEntitiesByType) {
                updateTownEntity(mannequin);
            }
            return;
        }

        String skin = town.getAppearance() == null ? "" : town.getAppearance().getSkin() == null ? "" : town.getAppearance().getSkin();

        world.spawn(location, Mannequin.class, mannequin -> {
            mannequin.setInvulnerable(true);
            mannequin.setAI(false);
            mannequin.getPersistentDataContainer().set(TOWN_ENTITY_KEY, PersistentDataType.LONG, town.getId());

            String texture = town.getAppearance().getTexture();
            if (texture == null || texture.isEmpty()) {
                mannequin.setProfile(ResolvableProfile.resolvableProfile().name(skin).build());
            } else {
                mannequin.setProfile(ResolvableProfile.resolvableProfile().addProperty(new ProfileProperty("textures", texture)).build());
            }

            mannequin.customName(Component.text(town.getDisplayName()));
            mannequin.setImmovable(true);
            mannequin.setDescription(Component.text("Town"));
        });
        
        for (Consumer<Town> townEntityConsumer : onTownSpawn) {
            townEntityConsumer.accept(town);
        }

    }

    private void updateTownEntity(Mannequin mannequin) {
        Location location = mannequin.getLocation();
        World world = location.getWorld();
        Collection<Player> nearbyPlayers = world.getNearbyPlayers(location, 12);
        if (nearbyPlayers.isEmpty()) {
            mannequin.setGlowing(false);
            return;
        } else {
            mannequin.setGlowing(true);
        }

        Optional<Player> first = nearbyPlayers.stream().findFirst();
        Player player = first.get();

        mannequin.lookAt(player.getEyeLocation(), LookAnchor.EYES);
    }

    public boolean isTown(Entity entity, Long townId) {
        if (!entity.getPersistentDataContainer().has(TOWN_ENTITY_KEY)) return false;
        Long keyValue = entity.getPersistentDataContainer().get(TOWN_ENTITY_KEY, PersistentDataType.LONG);
        if (keyValue == null) return false;
        return keyValue.equals(townId);
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

        Bukkit.getScheduler().runTaskTimer(CobaltKingdoms.getInstance(), this::displayTownParticles, 1, 10);
        Bukkit.getScheduler().runTaskTimer(CobaltKingdoms.getInstance(), this::tickTownVerification, 1, this.TOWN_CONFIG.getTownVerificationTimer());
        Bukkit.getPluginManager().registerEvents(this, CobaltKingdoms.getInstance());
    }

    private void displayTownParticles() {
        townRepository.getTowns().forEach(town -> {
            Location location = town.getLocation();
            World world = location.getWorld();
            world.spawnParticle(Particle.END_ROD, location, 3, .2, .2, .2, 0);

            townRepository.getJails(town.getId()).forEach(jail -> {
                Location jailLocation = jail.getLocation();
                world.spawnParticle(Particle.CRIT, jailLocation, 3, .2, .2, .2, 0);
            });
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

    private boolean isTownLoaded(Town town) {
        double closestPlayerDistance = Double.MAX_VALUE;
        for (Player p : Bukkit.getOnlinePlayers()) {
            double distance = p.getLocation().distanceSquared(town.getLocation());
            if (distance < closestPlayerDistance) closestPlayerDistance = distance;
        }
        return closestPlayerDistance < this.TOWN_CONFIG.getTownSpawnDistance() * this.TOWN_CONFIG.getTownSpawnDistance();
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

    public void onTownSpawn(Consumer<Town> eventConsumer) {
        onTownSpawn.add(eventConsumer);
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

    public String[] getJailNames(Player player) {
        return getJails(player).stream().map(TownJail::getName).toArray(String[]::new);
    }

    public List<TownJail> getJails(Player player) {
        Town playerTown = getPlayerOwnedTown(player);
        return townRepository.getJails(playerTown.getId());
    }

    public List<TownJail> getJails(TownEntity town) {
        return townRepository.getJails(town.getId());
    }

    public TownJail getJail(Player player, String jailName) {
        Town playerTown = getPlayerOwnedTown(player);
        return townRepository.getJailByName(playerTown.getId(), jailName).orElse(null);
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
}
