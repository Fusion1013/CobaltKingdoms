package se.fusion1013.cobaltKingdoms.raid.model;

import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.manager.registry.IRegistryItem;
import se.fusion1013.cobaltCore.variable.IntVariable;
import se.fusion1013.cobaltCore.variable.StringVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RaidWaveDefinition implements IRegistryItem {

    private final StringVariable internalName = new StringVariable("internal_name");
    private final List<RaidGroupDefinition> raidGroupDefinitions = new ArrayList<>();

    private final IntVariable spawnRadius = new IntVariable("radius");
    private final IntVariable spawnAttempts = new IntVariable("attempts");

    public RaidWaveDefinition(YamlConfiguration yaml) {
        internalName.load(yaml);
        List<Map<?, ?>> spawnGroups = yaml.getMapList("spawn_groups");
        for (Map<?, ?> group : spawnGroups) {
            YamlConfiguration groupYaml = toYaml(group);
            raidGroupDefinitions.add(new RaidGroupDefinition(groupYaml));
        }

        ConfigurationSection spawnConfiguration = yaml.getConfigurationSection("spawn");
        spawnRadius.load(spawnConfiguration);
        spawnAttempts.load(spawnConfiguration);
    }

    private static YamlConfiguration toYaml(Map<?, ?> map) {
        YamlConfiguration section = new YamlConfiguration();

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            section.set(String.valueOf(entry.getKey()), entry.getValue());
        }

        return section;
    }

    public RaidWaveDefinition(JsonObject json) {

    }

    public List<RaidGroupDefinition> getRaidGroupDefinitions() {
        return raidGroupDefinitions;
    }

    @Override
    public String getInternalName() {
        return internalName.getValue();
    }

    public int getSpawnAttempts() {
        return spawnAttempts.getValue();
    }

    public int getSpawnRadius() {
        return spawnRadius.getValue();
    }
}
