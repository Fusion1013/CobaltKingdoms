package se.fusion1013.cobaltKingdoms.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.player.PlayerData;
import se.fusion1013.cobaltKingdoms.player.PlayerManager;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class DiscordManager extends Manager<CobaltKingdoms> implements Listener {

    private static JDA JDA;

    public DiscordManager(CobaltKingdoms plugin) {
        super(plugin);
        try {
            initDiscord();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void initDiscord() throws InterruptedException {
        FileConfiguration config = CobaltKingdoms.getInstance().getConfig();
        if (!config.contains("discord-token")) return;
        String token = config.getString("discord-token");
        JDA = JDABuilder.createDefault(token, EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_VOICE_STATES))
                .addEventListeners(new SlashCommandListener())
                .build()
                .awaitReady();

        registerCommands();
    }

    private void registerCommands() {
        CommandListUpdateAction commands = JDA.updateCommands();
        commands.addCommands(Commands.slash("info", "Server info command")).queue();
    }

    private void updatePlayerCount(Set<Player> players) {

        int nInCharacter = 0;
        int nOpenForInCharacter = 0;
        int nOutOfCharacter = 0;
        int nAfk = 0;

        for (Player p : players) {
            PlayerData playerData = PlayerManager.getPlayerData(p.getUniqueId());
            switch (playerData.getPlayerStatus()) {
                case IN_CHARACTER -> nInCharacter++;
                case OPEN_FOR_IN_CHARACTER -> nOpenForInCharacter++;
                case OUT_OF_CHARACTER -> nOutOfCharacter++;
                case AFK -> nAfk++;
            }
        }

        final String playerCountMessage = "| IC: " + nInCharacter + " | OIC: " + nOpenForInCharacter + " | OOC: " + nOutOfCharacter + " | AFK: " + nAfk + " |";

        long playerCount = players.size();
        Bukkit.getScheduler().runTaskLaterAsynchronously(CobaltKingdoms.getInstance(), () -> {
            JDA.getPresence().setActivity(Activity.of(Activity.ActivityType.CUSTOM_STATUS, "Players Online: " + playerCount));
        }, 1);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Set<Player> set = new HashSet<>(Bukkit.getOnlinePlayers());
        set.add(event.getPlayer());
        updatePlayerCount(set);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Set<Player> set = new HashSet<>(Bukkit.getOnlinePlayers());
        set.remove(event.getPlayer());
        updatePlayerCount(set);
    }

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltKingdoms.getInstance());
        updatePlayerCount(new HashSet<>(Bukkit.getOnlinePlayers()));
    }

    @Override
    public void disable() {

    }
}
