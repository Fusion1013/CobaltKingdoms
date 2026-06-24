package se.fusion1013.cobaltKingdoms.audio;

import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.variable.IntVariable;
import se.fusion1013.cobaltCore.variable.StringVariable;

public final class AmbientSound implements IAmbientSound {

    private final StringVariable internalName = new StringVariable("internal_name");
    private final StringVariable key = new StringVariable("key");
    private final IntVariable lengthSeconds = new IntVariable("length_seconds");
    private final IntVariable overlapSeconds = new IntVariable("overlap_seconds");

    public AmbientSound(ConfigurationSection yaml) {
        internalName.load(yaml);
        key.load(yaml);
        lengthSeconds.load(yaml);
        overlapSeconds.load(yaml);
    }

    public AmbientSound(JsonObject json) {
    }

    @Override
    public String getKey() {
        return key.getValue();
    }

    public int getLengthTicks() {
        return lengthSeconds.getValue() * 20;
    }

    public int getOverlapTicks() {
        return overlapSeconds.getValue() * 20;
    }

    @Override
    public String getInternalName() {
        return internalName.getValue();
    }
}
