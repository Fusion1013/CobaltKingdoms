package se.fusion1013.cobaltKingdoms.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.util.HexUtils;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PlayerManager extends Manager<CobaltKingdoms> implements Listener {

    private static final Map<UUID, PlayerData> PLAYER_DATA = new HashMap<>();
    private static final ScoreboardManager SCOREBOARD_MANAGER = Bukkit.getScoreboardManager();
    private static final Scoreboard SCOREBOARD = SCOREBOARD_MANAGER.getMainScoreboard();

    public void setPlayerStatus(UUID playerUuid, PlayerStatus status) {
        PlayerData playerData = getPlayerData(playerUuid);
        playerData.setPlayerStatus(status);
        updatePlayerStatus(Objects.requireNonNull(Bukkit.getPlayer(playerUuid)));
    }

    public void setColorPrefix(UUID playerUuid, String colorPrefix) {
        PlayerData playerData = getPlayerData(playerUuid);
        playerData.setColorPrefix(colorPrefix);
        Player player = Bukkit.getPlayer(playerUuid);
        if (player == null) return;
        updatePlayerStatus(player);
    }

    private PlayerData getPlayerData(UUID player) {
        return PLAYER_DATA.computeIfAbsent(player, k -> new PlayerData("", PlayerStatus.IN_CHARACTER));
    }


    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updatePlayerStatus(player);
        LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(),
                player,
                "kingdoms.status.player_join",
                StringPlaceholders.builder().addPlaceholder("status", getPlayerData(player.getUniqueId()).getPlayerStatus().title).build());
    }

    public void updatePlayerStatus(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        if (player == null) return;
        updatePlayerStatus(player);
    }

    public void updatePlayerStatus(Player player) {
        UUID playerId = player.getUniqueId();
        PlayerData playerData = getPlayerData(playerId);
        updatePlayerTabVisual(playerId, playerData);

        Team playerTeam = getOrCreateTeam(player);
        updateTeamPrefix(playerTeam, player, playerData);
    }


    private void updatePlayerTabVisual(UUID playerId, PlayerData playerData) {
        Player player = Bukkit.getPlayer(playerId);
        if (player == null) return;

        String adminPrefix = player.isOp() ? "&7[<g:#2991f2:#9d4bdb>MOD&7] " : "";

        player.setPlayerListName(HexUtils.colorify(adminPrefix + "&7[&3" + playerData.getPlayerStatus().colorPrefix + playerData.getPlayerStatus().prefix + "&7] " + playerData.getColorPrefix() + player.getName()));
    }

    private static Team getOrCreateTeam(Player player) {
        Team team = getOrCreateTeam(player.getName());
        team.addPlayer(player);
        return team;
    }

    private static Team getOrCreateTeam(String name) {
        return SCOREBOARD.getTeam(name) == null ? SCOREBOARD.registerNewTeam(name) : SCOREBOARD.getTeam(name);
    }

    private static void updateTeamPrefix(Team team, Player player, PlayerData playerData) {
        String statusColor = "";
        String statusAbbreviation = "";
        switch (playerData.getPlayerStatus()) {
            case IN_CHARACTER -> {
                statusColor = "&a";
                statusAbbreviation = "IC";
            }
            case OUT_OF_CHARACTER -> {
                statusColor = "&e";
                statusAbbreviation = "OOC";
            }
            case AFK -> {
                statusColor = "&c";
                statusAbbreviation = "AFK";
            }
        }
        team.setPrefix(HexUtils.colorify("&7[" + statusColor + statusAbbreviation + "&7] "));
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
