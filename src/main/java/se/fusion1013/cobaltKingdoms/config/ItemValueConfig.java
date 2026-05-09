package se.fusion1013.cobaltKingdoms.config;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ItemValueConfig extends Manager<CobaltKingdoms> {

    private static final Map<String, Float> itemValues = new HashMap<>();

    private static void initialize() throws IOException, InvalidConfigurationException {
        CobaltKingdoms plugin = CobaltKingdoms.getInstance();

        File file = new File(plugin.getDataFolder(), "item_values.yml");

        if (!file.exists()) {
            plugin.getLogger().warning("item_values.yml not found, creating default file, which needs to be edited!");
            plugin.saveResource("item_values.yml", false);
        }

        YamlConfiguration config = new YamlConfiguration();
        config.options().parseComments(true);
        config.load(file);

        for (String key : config.getKeys(false)) {
            int value = config.getInt(key);
            itemValues.put(key, (float) value);
        }

        plugin.getLogger().info("Loaded " + itemValues.size() + " item values.");
    }

    public ItemValueConfig(CobaltKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        try {
            initialize();
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void disable() {

    }

    public static Optional<Float> getMaterialValue(String itemName) {
        return Optional.ofNullable(itemValues.get(itemName));
    }

//    public static Optional<Float> getItemStackValue(ItemStack itemStack) {
//        Optional<Float> matValue = getMaterialValue(itemStack.getType());
//        if (matValue.isPresent())
//            return Optional.of(matValue.get() * itemStack.getAmount());
//        return Optional.empty();
//    }

    public static boolean hasValueForMaterial(Material material) {
        return itemValues.containsKey(material);
    }
}
