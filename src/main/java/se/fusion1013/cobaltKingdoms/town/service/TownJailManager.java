package se.fusion1013.cobaltKingdoms.town.service;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.town.model.Town;
import se.fusion1013.cobaltKingdoms.town.model.TownJail;
import se.fusion1013.cobaltKingdoms.town.repository.ITownRepository;

import java.util.List;

public class TownJailManager extends Manager<CobaltKingdoms> {

    private static final TownManager townManager = TownManager.getInstance();
    private static final ITownRepository townRepository = DataManager.getInstance().getDao(ITownRepository.class);

    // ##%%##%%## JAIL ##%%##%%## //

    public Response createJail(Player player, String jailName, Location location) {
        Town town = townManager.getPlayerOwnedTown(player);
        if (town == null) return Response.error("You are not a member of a town");

        Response hasEditPermissions = TownManager.hasTownEditPermissions(player, town);
        if (hasEditPermissions.error()) return hasEditPermissions;

        TownJail jail = new TownJail();
        jail.setName(jailName);
        jail.setLocation(location);
        jail.setTownId(town.getId());

        townRepository.createJail(jail);
        return Response.ok("Created jail");
    }

    public Response deleteJail(Player player, String jailName) {
        Town town = townManager.getPlayerOwnedTown(player);
        if (town == null) return Response.error("You are not a member of a town");

        Response hasEditPermissions = TownManager.hasTownEditPermissions(player, town);
        if (hasEditPermissions.error()) return hasEditPermissions;

        boolean deleted = townRepository.deleteJail(town.getId(), jailName);
        if (deleted) return Response.ok("Deleted jail");
        return Response.error("Could not find jail");
    }

    public String[] getJailNames(Player player) {
        return getJails(player).stream().map(TownJail::getName).toArray(String[]::new);
    }

    public List<TownJail> getJails(Player player) {
        Town playerTown = townManager.getPlayerOwnedTown(player);
        return townRepository.getJails(playerTown.getId());
    }

    public List<TownJail> getJails(Town town) {
        return townRepository.getJails(town.getId());
    }

    public TownJail getJail(Player player, String jailName) {
        Town playerTown = townManager.getPlayerOwnedTown(player);
        return townRepository.getJailByName(playerTown.getId(), jailName).orElse(null);
    }

    public TownJailManager(CobaltKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    private static TownJailManager INSTANCE;

    public static TownJailManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TownJailManager(CobaltKingdoms.getInstance());
        }
        return INSTANCE;
    }
}
