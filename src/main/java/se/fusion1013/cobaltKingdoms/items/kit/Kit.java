package se.fusion1013.cobaltKingdoms.items.kit;

import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltKingdoms.player.character.CharacterProfileManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Kit implements IKit {

    private final String internalName;

    private ItemStack helmetItem;
    private ItemStack chestplateItem;
    private ItemStack leggingsItem;
    private ItemStack bootsItem;

    private final List<ItemStack> items = new ArrayList<>();

    private String character;

    private boolean teleport;
    private int xTeleport;
    private int yTeleport;
    private int zTeleport;
    private int yaw;
    private int pitch;

    private final List<PotionEffect> effects = new ArrayList<>();

    private Kit(String internalName, ConfigurationSection yaml) {
        this.internalName = internalName;
        load(yaml);
    }

    private void load(ConfigurationSection yaml) {
        helmetItem = getItem("helmet", yaml);
        chestplateItem = getItem("chestplate", yaml);
        leggingsItem = getItem("leggings", yaml);
        bootsItem = getItem("boots", yaml);

        if (yaml.contains("items")) {
            List<String> itemStrings = yaml.getStringList("items");
            itemStrings.forEach(i -> items.add(CustomItemManager.getItemStack(i)));
        }

        if (yaml.contains("character")) character = yaml.getString("character");

        if (yaml.contains("teleport")) {
            teleport = true;
            ConfigurationSection teleportYaml = yaml.getConfigurationSection("teleport");
            xTeleport = teleportYaml.getInt("x");
            yTeleport = teleportYaml.getInt("y");
            zTeleport = teleportYaml.getInt("z");

            if (teleportYaml.contains("yaw")) yaw = teleportYaml.getInt("yaw");
            if (teleportYaml.contains("pitch")) pitch = teleportYaml.getInt("pitch");
        }

        if (yaml.contains("effects")) {
            List<Map<?, ?>> mapList = yaml.getMapList("effects");
            for (Map<?, ?> map : mapList) {
                String effectName = (String) map.get("effect");
                int amplifier = (int) map.get("amplifier");
                int duration = (int) map.get("duration");
                boolean hideParticles = map.containsKey("hide_particles") && (boolean) map.get("hide_particles");
                PotionEffectType type = Registry.POTION_EFFECT_TYPE.get(new NamespacedKey(Key.MINECRAFT_NAMESPACE, effectName));
                PotionEffect effect = new PotionEffect(type, duration, amplifier, false, !hideParticles);
                effects.add(effect);
            }
        }
    }

    private static ItemStack getItem(String name, ConfigurationSection yaml) {
        if (!yaml.contains(name)) return null;
        String itemString = yaml.getString(name);
        if (itemString == null) return null;
        return CustomItemManager.getItemStack(itemString);
    }

    public static IKit create(ConfigurationSection yaml) {
        String internalName = yaml.getString("internal_name");
        return new Kit(internalName, yaml);
    }

    @Override
    public void apply(Player player) {
        player.getInventory().clear();

        if (helmetItem != null) player.getInventory().setItem(EquipmentSlot.HEAD, helmetItem.clone());
        if (chestplateItem != null) player.getInventory().setItem(EquipmentSlot.CHEST, chestplateItem.clone());
        if (leggingsItem != null) player.getInventory().setItem(EquipmentSlot.LEGS, leggingsItem.clone());
        if (bootsItem != null) player.getInventory().setItem(EquipmentSlot.FEET, bootsItem.clone());

        for (ItemStack item : items) {
            player.getInventory().addItem(item.clone());
        }

        if (character != null && !character.isEmpty()) {
            CharacterProfileManager.getInstance().setActiveCharacter(player, character);
        }

        if (teleport) {
            player.teleport(new Location(player.getWorld(), xTeleport, yTeleport, zTeleport, yaw, pitch));
        }

        for (PotionEffect effect : effects) {
            player.addPotionEffect(new PotionEffect(effect.getType(), effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.hasParticles()));
        }

        player.updateInventory();

        // TODO
    }

    @Override
    public String getId() {
        return internalName;
    }

    @Override
    public String getInternalName() {
        return getId();
    }
}
