package se.fusion1013.cobaltKingdoms.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.audio.AmbientSoundManager;
import se.fusion1013.cobaltKingdoms.audio.AudioRegionManager;
import se.fusion1013.cobaltKingdoms.audio.IAmbientSoundCollection;

import java.util.HashMap;
import java.util.Map;

public class AudioRegionCommand {

    private static final Map<Location, AudioRegionManager> AUDIO_REGION_MANAGERS = new HashMap<>();

    public static void register() {
        new CommandAPICommand("audio_region")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "audio_region"))
                .withSubcommand(AudioRegionCommand.collectionCommand())
                .withArguments(new LocationArgument("corner1", LocationType.BLOCK_POSITION))
                .withArguments(new LocationArgument("corner2", LocationType.BLOCK_POSITION))
                .withArguments(new TextArgument("sounds"))
                .executes(AudioRegionCommand::tickAudioRegion)
                .register();
    }

    private static CommandAPICommand collectionCommand() {
        return new CommandAPICommand("collection")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "audio_region.collection"))
                .withArguments(new LocationArgument("corner1", LocationType.BLOCK_POSITION))
                .withArguments(new LocationArgument("corner2", LocationType.BLOCK_POSITION))
                .withArguments(new StringArgument("audio_collection").replaceSuggestions(ArgumentSuggestions.strings(k -> AmbientSoundManager.getAmbientSoundCollectionNames())))
                .executes(AudioRegionCommand::tickAudioRegionCollection);
    }

    private static void tickAudioRegionCollection(CommandSender sender, CommandArguments args) {
        Location corner1 = (Location) args.get("corner1");
        Location corner2 = (Location) args.get("corner2");
        String collectionName = (String) args.get("audio_collection");

        IAmbientSoundCollection collection = AmbientSoundManager.getAmbientSoundCollection(collectionName);
        String sounds = String.join(",", collection.getSounds());

        if (sender instanceof Player player) {
            tickAudioRegionAtLocation(player.getLocation(), corner1, corner2, sounds);
        }

        if (sender instanceof BlockCommandSender blockCommandSender) {
            tickAudioRegionAtLocation(blockCommandSender.getBlock().getLocation(), corner1, corner2, sounds);
        }
    }

    private static void tickAudioRegion(CommandSender sender, CommandArguments args) {
        Location corner1 = (Location) args.get("corner1");
        Location corner2 = (Location) args.get("corner2");
        String sounds = (String) args.get("sounds");

        if (sender instanceof Player player) {
            tickAudioRegionAtLocation(player.getLocation(), corner1, corner2, sounds);
        }

        if (sender instanceof BlockCommandSender blockCommandSender) {
            tickAudioRegionAtLocation(blockCommandSender.getBlock().getLocation(), corner1, corner2, sounds);
        }
    }

    private static void tickAudioRegionAtLocation(Location location, Location corner1, Location corner2, String sounds) {
        AudioRegionManager audioRegion = AUDIO_REGION_MANAGERS.computeIfAbsent(location, k -> new AudioRegionManager());

        Bukkit.getOnlinePlayers().forEach(p -> {
            audioRegion.tickRegion(corner1, corner2, p, AmbientSoundManager.fromString(sounds));
        });
    }

}
