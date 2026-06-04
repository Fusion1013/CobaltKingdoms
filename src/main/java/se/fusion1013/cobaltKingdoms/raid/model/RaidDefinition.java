package se.fusion1013.cobaltKingdoms.raid.model;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.manager.registry.IRegistryItem;
import se.fusion1013.cobaltCore.variable.StringVariable;
import se.fusion1013.cobaltKingdoms.raid.service.RaidWaveService;

import java.util.List;

public class RaidDefinition implements IRegistryItem {

    private final StringVariable internalName = new StringVariable("internal_name");
    private final StringVariable waves = new StringVariable("waves");

    public RaidDefinition(YamlConfiguration yaml) {
        internalName.load(yaml);
        waves.load(yaml);
    }

    public RaidDefinition(JsonObject json) {

    }

    public RaidWaveDefinition getWave(int index) {
        List<String> waveNames = waves.getValueList();
        if (waveNames.size() <= index || index < 0) return null;

        String waveName = waveNames.get(index);
        return RaidWaveService.getInstance().getWave(waveName);
    }

    @Override
    public String getInternalName() {
        return internalName.getValue();
    }
}
