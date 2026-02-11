package se.fusion1013.cobaltKingdoms.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

public class HeightCommand {
    public static void register() {
        new CommandAPICommand("height")
                .withPermission("cobalt.kingdoms.height")
                .withSubcommand(HeightCommand.createSetCommand())
                .withSubcommand(HeightCommand.createQueryCommand())
                .register();
    }

    private static CommandAPICommand createSetCommand() {
        return new CommandAPICommand("set")
                .withPermission("cobalt.kingdoms.height.set")
                .withArguments(new DoubleArgument("height", 0.7, 1.3))
                .withOptionalArguments(new EntitySelectorArgument.OnePlayer("player"))
                .executesPlayer(HeightCommand::setPlayerHeight);
    }

    private static void setPlayerHeight(Player player, CommandArguments commandArguments) {
        double height = (double) commandArguments.get("height");
        Player targetPlayer = commandArguments.get("player") != null ? (Player) commandArguments.get("player") : player;

        if (targetPlayer.getUniqueId() != player.getUniqueId() && !player.hasPermission("cobalt.kingdoms.armorstand.set_other_player")) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.height.set_only_self");
            return;
        }

        if (targetPlayer.getAttribute(Attribute.SCALE) != null)
            targetPlayer.getAttribute(Attribute.SCALE).setBaseValue(height);
        if (targetPlayer.getAttribute(Attribute.MAX_HEALTH) != null) {
            int targetHealth = 20;
            if (height < 1) targetHealth = 18;
            targetPlayer.getAttribute(Attribute.MAX_HEALTH).setBaseValue(targetHealth);
        }

        if (targetPlayer.getGameMode() == GameMode.SURVIVAL) {
            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 0));
            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 30, 4));
            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 60 * 10, 1));

            LocaleManager.getInstance().sendMessage("", targetPlayer, "kingdoms.commands.height.set.survival", StringPlaceholders.builder()
                    .addPlaceholder("height", height)
                    .build());
        } else {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.height.set", StringPlaceholders.builder()
                    .addPlaceholder("height", height)
                    .build());
        }
    }

    private static CommandAPICommand createQueryCommand() {
        return new CommandAPICommand("query")
                .withPermission("cobalt.kingdoms.height.query")
                .withOptionalArguments(new EntitySelectorArgument.OnePlayer("player"))
                .executesPlayer(HeightCommand::getPlayerHeight);
    }

    private static void getPlayerHeight(Player player, CommandArguments commandArguments) {
        Player targetPlayer = commandArguments.get("player") != null ? (Player) commandArguments.get("player") : player;

        double height = targetPlayer.getAttribute(Attribute.SCALE).getBaseValue();
        double maxHealth = targetPlayer.getAttribute(Attribute.MAX_HEALTH).getBaseValue();

        LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.height.query", StringPlaceholders.builder()
                .addPlaceholder("height", height)
                .addPlaceholder("max_health", maxHealth)
                .build());
    }
}
