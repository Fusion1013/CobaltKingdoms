package se.fusion1013.cobaltKingdoms.audio;

import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.variable.StringVariable;

public class AmbientSoundCollection implements IAmbientSoundCollection {

    private final StringVariable internalName = new StringVariable("internal_name");
    private final StringVariable soundEvents = new StringVariable("sounds");

    public AmbientSoundCollection(ConfigurationSection yaml) {
        internalName.load(yaml);
        soundEvents.load(yaml);
    }

    public AmbientSoundCollection(JsonObject json) {
    }

    @Override
    public String[] getSounds() {
        return soundEvents.getValueList().toArray(new String[0]);
    }

    @Override
    public String getInternalName() {
        return internalName.getValue();
    }
}
