package se.fusion1013.cobaltKingdoms.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.util.HexUtils;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager extends Manager<CobaltKingdoms> implements Listener {

    private static final Map<UUID, PlayerData> PLAYER_DATA = new HashMap<>();

    public void setPlayerStatus(UUID player, PlayerStatus status) {
        PlayerData playerData = getPlayerData(player);
        playerData.setPlayerStatus(status);
        updatePlayerTabVisual(player);
    }

    public void setColorPrefix(UUID player, String colorPrefix) {
        PlayerData playerData = getPlayerData(player);
        playerData.setColorPrefix(colorPrefix);
        updatePlayerTabVisual(player);
    }

    private PlayerData getPlayerData(UUID player) {
        return PLAYER_DATA.computeIfAbsent(player, k -> new PlayerData("", PlayerStatus.IN_CHARACTER));
    }


    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updatePlayerTabVisual(player.getUniqueId());
    }

    public void updatePlayerTabVisual(UUID playerId) {
        PlayerData playerData = getPlayerData(playerId);
        updatePlayerTabVisual(playerId, playerData);
    }


    private void updatePlayerTabVisual(UUID playerId, PlayerData playerData) {
        Player player = Bukkit.getPlayer(playerId);
        if (player == null) return;
        player.setPlayerListName(HexUtils.colorify("&7[&3" + playerData.getPlayerStatus().prefix + "&7] " + playerData.getColorPrefix() + player.getName()));
    }


    public PlayerManager(CobaltKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }
}
