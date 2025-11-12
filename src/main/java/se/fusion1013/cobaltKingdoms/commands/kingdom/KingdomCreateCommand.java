package se.fusion1013.cobaltKingdoms.commands.kingdom;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.ResponseType;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomManager;

import java.util.Random;

public class KingdomCreateCommand {

    private static final KingdomManager KINGDOM_MANAGER = CobaltCore.getInstance().getManager(CobaltKingdoms.getInstance(), KingdomManager.class);
    private static final LocaleManager LOCALE = CobaltCore.getInstance().getManager(CobaltCore.getInstance(), LocaleManager.class);

    public static CommandAPICommand register() {
        return new CommandAPICommand("create")
                .withPermission("cobalt.kingdom.commands.kingdom.create")
                .withArguments(new StringArgument("name"))
                .executesPlayer((sender, args) -> {

                    // Try to create the kingdom
                    String kingdomName = (String) args.get("name");
                    Response response = KINGDOM_MANAGER.createKingdom(kingdomName, sender.getUniqueId());

                    // Send response to player & announce the creation of the kingdom (if success)
                    StringPlaceholders placeholders = StringPlaceholders.builder()
                            .addPlaceholder("player", sender.getName())
                            .addPlaceholder("kingdom", kingdomName)
                            .addPlaceholder("reason", response.message())
                            .build();

                    if (response.type() == ResponseType.OK) {
                        LOCALE.sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.kingdom.create.succeed", placeholders);
                        sender.playSound(sender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        for (int i = 0; i < 5; i++) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    spawnRandomFirework(sender);
                                }
                            }.runTaskLater(CobaltKingdoms.getInstance(), i * 4);
                        }
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (player == sender) continue;
                            LOCALE.sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.kingdom.create.announce", placeholders);
                        }
                    } else {
                        placeholders.addPlaceholder("reason", response.message());
                        LOCALE.sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.kingdom.create.fail");
                    }
                });
    }

    private static void spawnRandomFirework(Player player) {
        Location loc = player.getLocation();
        Firework firework = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK_ROCKET);
        FireworkMeta meta = firework.getFireworkMeta();

        Random random = new Random();

        // Pick 1-3 random colors
        Color[] possibleColors = {
                Color.AQUA, Color.BLUE, Color.FUCHSIA, Color.GREEN, Color.LIME,
                Color.MAROON, Color.NAVY, Color.ORANGE, Color.PURPLE, Color.RED, Color.SILVER, Color.WHITE, Color.YELLOW
        };

        int colorCount = 1 + random.nextInt(3);
        Color[] colors = new Color[colorCount];
        for (int i = 0; i < colorCount; i++) {
            colors[i] = possibleColors[random.nextInt(possibleColors.length)];
        }

        // Pick 1-2 random fade colors
        int fadeCount = 1 + random.nextInt(2);
        Color[] fades = new Color[fadeCount];
        for (int i = 0; i < fadeCount; i++) {
            fades[i] = possibleColors[random.nextInt(possibleColors.length)];
        }

        // Random firework type
        FireworkEffect.Type[] types = FireworkEffect.Type.values();
        FireworkEffect.Type type = types[random.nextInt(types.length)];

        // Random trail & flicker
        boolean flicker = random.nextBoolean();
        boolean trail = random.nextBoolean();

        // Build the effect
        FireworkEffect effect = FireworkEffect.builder()
                .withColor(colors)
                .withFade(fades)
                .with(type)
                .flicker(flicker)
                .trail(trail)
                .build();

        meta.addEffect(effect);

        // Random power 1-3
        meta.setPower(1 + random.nextInt(3));

        firework.setFireworkMeta(meta);
    }

    private static void spawnCoolFirework(Player player) {
        Location loc = player.getLocation();

        // Spawn the firework entity
        Firework firework = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK_ROCKET);

        // Configure its meta
        FireworkMeta meta = firework.getFireworkMeta();

        // Create a "cool-looking" effect
        FireworkEffect effect = FireworkEffect.builder()
                .withColor(Color.AQUA, Color.PURPLE, Color.BLUE)
                .withFade(Color.WHITE, Color.SILVER)
                .flicker(true)
                .trail(true)
                .with(FireworkEffect.Type.BURST)
                .build();

        meta.addEffect(effect);

        // Power controls height — 1 = low, 2 = medium, 3+ = high
        meta.setPower(2);

        firework.setFireworkMeta(meta);
    }
}
