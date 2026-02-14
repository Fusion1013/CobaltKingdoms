package se.fusion1013.cobaltKingdoms.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltKingdoms.player.PlayerData;
import se.fusion1013.cobaltKingdoms.player.PlayerManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SlashCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "info" -> {
                Collection<Player> players = Bukkit.getOnlinePlayers().stream().filter(p -> !p.isOp()).collect(Collectors.toSet());

                int nInCharacter = 0;
                List<Player> inCharacter = new ArrayList<>();

                int nOpenForInCharacter = 0;
                List<Player> openForInCharacter = new ArrayList<>();

                int nOutOfCharacter = 0;
                List<Player> outOfCharacter = new ArrayList<>();

                int nAfk = 0;
                List<Player> afk = new ArrayList<>();

                for (Player p : players) {
                    PlayerData playerData = PlayerManager.getPlayerData(p.getUniqueId());
                    switch (playerData.getPlayerStatus()) {
                        case IN_CHARACTER -> {
                            nInCharacter++;
                            inCharacter.add(p);
                        }
                        case OPEN_FOR_IN_CHARACTER -> {
                            nOpenForInCharacter++;
                            openForInCharacter.add(p);
                        }
                        case OUT_OF_CHARACTER -> {
                            nOutOfCharacter++;
                            outOfCharacter.add(p);
                        }
                        case AFK -> {
                            nAfk++;
                            afk.add(p);
                        }
                    }
                }

                sendStatusEmbed(event, nInCharacter, inCharacter, nOpenForInCharacter, openForInCharacter, nOutOfCharacter, outOfCharacter, nAfk, afk);
            }
        }
    }

    public void sendStatusEmbed(SlashCommandInteractionEvent event,
                                int nInCharacter, List<Player> inCharacter,
                                int nOpenForInCharacter, List<Player> openForInCharacter,
                                int nOutOfCharacter, List<Player> outOfCharacter,
                                int nAfk, List<Player> afk) {

        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("Server Roleplay Status");
        embed.setColor(Color.CYAN);

        embed.addField(
                "🟢 In Character (" + nInCharacter + ")",
                formatPlayerList(inCharacter),
                false
        );

        embed.addField(
                "🟡 Open For RP (" + nOpenForInCharacter + ")",
                formatPlayerList(openForInCharacter),
                false
        );

        embed.addField(
                "🔴 Out Of Character (" + nOutOfCharacter + ")",
                formatPlayerList(outOfCharacter),
                false
        );

        embed.addField(
                "⚫ AFK (" + nAfk + ")",
                formatPlayerList(afk),
                false
        );

        event.replyEmbeds(embed.build()).queue();
    }

    private String formatPlayerList(List<Player> players) {
        if (players == null || players.isEmpty()) {
            return "None";
        }

        return players.stream()
                .map(Player::getName)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
    }

}
