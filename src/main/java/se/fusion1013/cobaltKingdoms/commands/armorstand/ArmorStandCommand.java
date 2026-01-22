package se.fusion1013.cobaltKingdoms.commands.armorstand;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.entities.armorstand.ArmorStandManager;
import se.fusion1013.cobaltKingdoms.entities.armorstand.ArmorStandPose;

import java.util.Arrays;
import java.util.Collection;

public class ArmorStandCommand {

    private static final ArmorStandManager ARMOR_STAND_MANAGER = ArmorStandManager.getInstance();

    public static void register() {
        new CommandAPICommand("armorstand")
                .withPermission("cobalt.kingdoms.armorstand")
                .withSubcommand(ArmorStandCommand.createSetCommand())
                .withSubcommand(ArmorStandCommand.createPoseCommand())
                .register();
    }

    private static CommandAPICommand createPoseCommand() {
        return new CommandAPICommand("pose")
                .withPermission("cobalt.kingdoms.armorstand.pose")
                .withSubcommand(ArmorStandCommand.createPosePresetCommand());
    }

    private static CommandAPICommand createPosePresetCommand() {
        return new CommandAPICommand("preset")
                .withPermission("cobalt.kingdoms.armorstand.pose.preset")
                .withArguments(new StringArgument("pose").replaceSuggestions(ArgumentSuggestions.strings(ArmorStandManager.getArmorStandPoseNames())))
                .executesPlayer(ArmorStandCommand::setPresetPose);
    }

    private static void setPresetPose(Player player, CommandArguments commandArguments) {
        ArmorStand armorStand = getClosestArmorStand(player.getWorld(), player.getLocation());

        if (armorStand == null) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.armorstand.set.fail_no_armor_stand");
            return;
        }

        String pose = (String) commandArguments.get("pose");
        ArmorStandPose armorStandPose = ArmorStandManager.getArmorStandPose(pose);

        if (armorStandPose != null) {
            armorStandPose.apply(armorStand);
        } else {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.armorstand.pose.preset_not_found", StringPlaceholders.builder()
                    .addPlaceholder("pose", pose)
                    .build());
            return;
        }

        LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.armorstand.pose.preset.set", StringPlaceholders.builder()
                .addPlaceholder("pose", pose)
                .build());
    }

    private static CommandAPICommand createSetCommand() {
        return new CommandAPICommand("set")
                .withPermission("cobalt.kingdoms.armorstand.set")
                .withSubcommand(ArmorStandCommand.createSetArmsCommand())
                .withSubcommand(ArmorStandCommand.createSetBaseCommand())
                .withSubcommand(ArmorStandCommand.createSetInvulnerableCommand())
                .withSubcommand(ArmorStandCommand.createSetLockedSlotsCommand())
                .withSubcommand(ArmorStandCommand.createSetSmallCommand());
    }

    private static CommandAPICommand createSetLockedSlotsCommand() {
        return new CommandAPICommand("lock")
                .withPermission("cobalt.kingdoms.armorstand.set.lock")
                .withArguments(new StringArgument("slot").replaceSuggestions(ArgumentSuggestions.strings(Arrays.stream(EquipmentSlot.values()).map(Enum::toString).toList())))
                .withArguments(new StringArgument("lock_type").replaceSuggestions(ArgumentSuggestions.strings(Arrays.stream(ArmorStand.LockType.values()).map(Enum::toString).toList())))
                .withOptionalArguments(new BooleanArgument("lock"))
                .executesPlayer(ArmorStandCommand::setLock);
    }

    private static void setLock(Player player, CommandArguments commandArguments) {
        ArmorStand armorStand = getClosestArmorStand(player.getWorld(), player.getLocation());

        if (armorStand == null) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.armorstand.set.fail_no_armor_stand");
            return;
        }

        EquipmentSlot equipmentSlot = EquipmentSlot.valueOf((String) commandArguments.get("slot"));
        ArmorStand.LockType lockType = ArmorStand.LockType.valueOf((String) commandArguments.get("lock_type"));
        boolean lock = commandArguments.get("lock") == null || (boolean) commandArguments.get("lock");

        if (lock) {
            armorStand.addEquipmentLock(equipmentSlot, lockType);
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.armorstand.set.lock_set", StringPlaceholders.builder()
                    .addPlaceholder("slot", equipmentSlot.toString())
                    .addPlaceholder("type", lockType.toString())
                    .build());
        } else {
            armorStand.removeEquipmentLock(equipmentSlot, lockType);
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.armorstand.set.lock_remove", StringPlaceholders.builder()
                    .addPlaceholder("slot", equipmentSlot.toString())
                    .addPlaceholder("type", lockType.toString())
                    .build());
        }
    }

    private static CommandAPICommand createSetInvulnerableCommand() {
        return new CommandAPICommand("invulnerable")
                .withPermission("cobalt.kingdoms.armorstand.set.invulnerable")
                .withArguments(new BooleanArgument("invulnerable"))
                .executesPlayer(ArmorStandCommand::setInvulnerable);
    }

    private static void setInvulnerable(Player player, CommandArguments commandArguments) {
        ArmorStand armorStand = getClosestArmorStand(player.getWorld(), player.getLocation());

        if (armorStand == null) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.armorstand.set.fail_no_armor_stand");
            return;
        }

        boolean value = (boolean) commandArguments.get("invulnerable");
        armorStand.setInvulnerable(value);
        LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.armorstand.set.invulnerable", StringPlaceholders.builder()
                .addPlaceholder("value", value)
                .build());
    }

    private static CommandAPICommand createSetBaseCommand() {
        return new CommandAPICommand("base")
                .withPermission("cobalt.kingdoms.armorstand.set.base")
                .withArguments(new BooleanArgument("base"))
                .executesPlayer(ArmorStandCommand::setBase);
    }

    private static void setBase(Player player, CommandArguments commandArguments) {
        ArmorStand armorStand = getClosestArmorStand(player.getWorld(), player.getLocation());

        if (armorStand == null) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.armorstand.set.fail_no_armor_stand");
            return;
        }

        boolean value = (boolean) commandArguments.get("base");
        armorStand.setBasePlate(value);
        LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.armorstand.set.base", StringPlaceholders.builder()
                .addPlaceholder("value", value)
                .build());
    }

    private static CommandAPICommand createSetSmallCommand() {
        return new CommandAPICommand("small")
                .withPermission("cobalt.kingdoms.armorstand.set.small")
                .withArguments(new BooleanArgument("small"))
                .executesPlayer(ArmorStandCommand::setSmall);
    }

    private static void setSmall(Player player, CommandArguments commandArguments) {
        ArmorStand armorStand = getClosestArmorStand(player.getWorld(), player.getLocation());

        if (armorStand == null) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.armorstand.set.fail_no_armor_stand");
            return;
        }

        boolean value = (boolean) commandArguments.get("small");
        armorStand.setSmall(value);
        LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.armorstand.set.small", StringPlaceholders.builder()
                .addPlaceholder("value", value)
                .build());
    }

    private static CommandAPICommand createSetArmsCommand() {
        return new CommandAPICommand("arms")
                .withPermission("cobalt.kingdoms.armorstand.set.arms")
                .withArguments(new BooleanArgument("arms"))
                .executesPlayer(ArmorStandCommand::setArms);
    }

    private static void setArms(Player player, CommandArguments commandArguments) {
        ArmorStand armorStand = getClosestArmorStand(player.getWorld(), player.getLocation());

        if (armorStand == null) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.armorstand.set.fail_no_armor_stand");
            return;
        }

        boolean value = (boolean) commandArguments.get("arms");
        armorStand.setArms(value);
        LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.armorstand.set.arms", StringPlaceholders.builder()
                .addPlaceholder("value", value)
                .build());
    }

    private static ArmorStand getClosestArmorStand(World world, Location location) {
        Collection<ArmorStand> entities = world.getNearbyEntitiesByType(ArmorStand.class, location, 2);
        double closest = Double.MAX_VALUE;
        ArmorStand closestEntity = null;
        for (ArmorStand armorStand : entities) {
            Location armorStandLocation = armorStand.getLocation();
            double distance = armorStandLocation.distanceSquared(location);
            if (!(distance < closest)) continue;
            closest = distance;
            closestEntity = armorStand;
        }
        return closestEntity;
    }

}
