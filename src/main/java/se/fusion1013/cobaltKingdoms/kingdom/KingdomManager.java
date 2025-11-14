package se.fusion1013.cobaltKingdoms.kingdom;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.ResponseType;
import se.fusion1013.cobaltKingdoms.database.kingdom.IKingdomDao;
import se.fusion1013.cobaltKingdoms.player.PlayerManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KingdomManager extends Manager<CobaltKingdoms> implements Listener {

    private static final Map<UUID, KingdomData> KINGDOMS = new HashMap<>();

    public boolean hasPermission(UUID playerId, KingdomPermission permission) {
        KingdomInfo kingdomInfo = getPlayerKingdomInfo(playerId);
        if (kingdomInfo == null) return false;
        if (kingdomInfo.owner().equals(playerId)) return true;
        // TODO: Add permission check for elevated players
        return false;
    }

    public KingdomInfo getPlayerKingdomInfo(UUID playerId) {
        KingdomData kingdomData = getPlayerKingdom(playerId);
        if (kingdomData == null) return null;
        return getKingdomInfo(kingdomData.getName());
    }

    public boolean isPlayerInKingdom(UUID playerId) {
        return getPlayerKingdom(playerId) != null;
    }

    public Response deleteKingdom(String kingdomName) {
        KingdomData kingdomData = getKingdomData(kingdomName);
        if (kingdomData == null) return new Response(ResponseType.FAIL, "Could not find kingdom with that name");

        resetPlayerColorPrefix(kingdomData.getMembers());

        KINGDOMS.remove(kingdomData.getId());
        IKingdomDao kingdomDao = DataManager.getInstance().getDao(IKingdomDao.class);
        kingdomDao.deleteKingdom(kingdomData.getId());

        return new Response(ResponseType.OK, "Removed kingdom");
    }

    private static void resetPlayerColorPrefix(List<UUID> players) {
        PlayerManager playerManager = CobaltCore.getInstance().getManager(CobaltKingdoms.getInstance(), PlayerManager.class);
        players.forEach(k -> playerManager.setColorPrefix(k, ""));
        players.forEach(playerManager::updatePlayerTabVisual);
    }

    public Response createKingdom(String kingdomName, UUID owner) {
        KingdomData playerKingdom = getPlayerKingdom(owner);
        if (playerKingdom != null) return new Response(ResponseType.FAIL, "Player already owns a kingdom");

        KingdomData existingNameKingdom = getKingdomData(kingdomName);
        if (existingNameKingdom != null) return new Response(ResponseType.FAIL, "Kingdom with that name already exists");

        KingdomData kingdomData = new KingdomData(kingdomName, UUID.randomUUID(), owner);

        Player player = Bukkit.getPlayer(owner);
        if (player == null) return new Response(ResponseType.FAIL, "Could not find player");

        KINGDOMS.put(kingdomData.getId(), kingdomData);
        IKingdomDao kingdomDao = DataManager.getInstance().getDao(IKingdomDao.class);
        kingdomDao.insertKingdom(kingdomData);

        return new Response(ResponseType.OK, "Kingdom created");
    }

    public Response setKingdomColor(String kingdomName, String colorPrefix) {
        KingdomData kingdomData = getKingdomData(kingdomName);
        if (kingdomData == null) return new Response(ResponseType.FAIL, "Could not find the kingdom");

        kingdomData.setColorPrefix(colorPrefix);

        IKingdomDao kingdomDao = DataManager.getInstance().getDao(IKingdomDao.class);
        kingdomDao.insertKingdom(kingdomData); // TODO: Replace with some other database operation

        PlayerManager playerManager = CobaltCore.getInstance().getManager(CobaltKingdoms.getInstance(), PlayerManager.class);
        kingdomData.getMembers().forEach(k -> playerManager.setColorPrefix(k, colorPrefix));
        kingdomData.getMembers().forEach(playerManager::updatePlayerTabVisual);

        return new Response(ResponseType.OK, "Changed the kingdom color");
    }

    private KingdomData getPlayerKingdom(UUID playerId) {
        for (KingdomData data : KINGDOMS.values()) {
            if (data.isPlayerMember(playerId)) return data;
        }
        return null;
    }

    private KingdomData getPlayerOwnedKingdom(UUID playerId) {
        for (KingdomData data : KINGDOMS.values()) {
            if (data.getOwner().equals(playerId)) return data;
        }
        return null;
    }

    private KingdomData getKingdomData(String name) {
        for (KingdomData data : KINGDOMS.values()) {
            if (data.getName().equalsIgnoreCase(name)) return data;
        }
        return null;
    }

    public List<String> getKingdomNames() {
        return KINGDOMS.values().stream().map(KingdomData::getName).toList();
    }

    public List<String> getKingdomNames(UUID playerId) {
        return KINGDOMS.values().stream().filter(kd -> kd.isPlayerMember(playerId)).map(KingdomData::getName).toList();
    }

    public boolean addPlayerToKingdom(String kingdomName, UUID playerId) {
        KingdomData kingdomData = getKingdomData(kingdomName);
        if (kingdomData == null) return false;

        if (kingdomData.isPlayerMember(playerId)) return false;

        KingdomData playerOldKingdom = getPlayerKingdom(playerId);
        if (playerOldKingdom != null) return false;

        kingdomData.addMember(playerId);
        IKingdomDao kingdomDao = DataManager.getInstance().getDao(IKingdomDao.class);
        kingdomDao.insertPlayer(playerId, kingdomData.getId());

        PlayerManager playerManager = CobaltCore.getInstance().getManager(CobaltKingdoms.getInstance(), PlayerManager.class);
        playerManager.setColorPrefix(playerId, kingdomData.getColorPrefix());
        return true;
    }

    public KingdomInfo getKingdomInfo(String kingdomName) {
        KingdomData kingdomData = getKingdomData(kingdomName);
        if (kingdomData == null) return null;

        return new KingdomInfo(kingdomName, kingdomData.getOwner(), kingdomData.getMembers());
    }

    public List<KingdomInfo> getKingdomInfo() {
        return KINGDOMS.values().stream().map(kingdomData -> new KingdomInfo(kingdomData.getName(), kingdomData.getOwner(), kingdomData.getMembers())).toList();
    }

    public Response leaveKingdom(@NotNull UUID playerId) {

        // Check if player is a member of a kingdom
        KingdomData kingdomData = getPlayerKingdom(playerId);
        if (kingdomData == null) return new Response(ResponseType.FAIL, "Player is not a member of a kingdom");

        // Check if player is the owner of the kingdom
        if (kingdomData.getOwner().equals(playerId)) return new Response(ResponseType.FAIL, "Cannot leave your own kingdom");

        // Remove player from database and kingdom data
        kingdomData.removeMember(playerId);
        DataManager dataManager = DataManager.getInstance();
        IKingdomDao kingdomDao = dataManager.getDao(IKingdomDao.class);

        kingdomDao.removePlayer(playerId, kingdomData.getId());

        PlayerManager playerManager = CobaltCore.getInstance().getManager(CobaltKingdoms.getInstance(), PlayerManager.class);
        playerManager.setColorPrefix(playerId, "");
        return new Response(ResponseType.OK, "Removed player from kingdom");
    }


    public KingdomManager(CobaltKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        PlayerManager playerManager = CobaltCore.getInstance().getManager(CobaltKingdoms.getInstance(), PlayerManager.class);
        DataManager dataManager = DataManager.getInstance();
        IKingdomDao kingdomDao = dataManager.getDao(IKingdomDao.class);
        List<KingdomData> kingdomDataList = kingdomDao.getKingdomData();
        for (KingdomData data : kingdomDataList) {
            KINGDOMS.put(data.getId(), data);
            data.getMembers().forEach(k -> playerManager.setColorPrefix(k, data.getColorPrefix()));
        }
    }

    @Override
    public void disable() {

    }

    private static KingdomManager INSTANCE;

    public static KingdomManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new KingdomManager(CobaltKingdoms.getInstance());
        }
        return INSTANCE;
    }
}
