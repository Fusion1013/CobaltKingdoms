package se.fusion1013.cobaltKingdoms.raid.model;

import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.variable.IntVariable;
import se.fusion1013.cobaltCore.variable.StringVariable;

public class RaidGroupDefinition {

    private final StringVariable entity = new StringVariable("entity");
    private final IntVariable amount = new IntVariable("amount");
    private final IntVariable weight = new IntVariable("weight");

    public RaidGroupDefinition(ConfigurationSection yaml) {
        entity.load(yaml);
        amount.load(yaml);
        weight.load(yaml);
    }

    public String getEntityName() {
        return entity.getValue();
    }

    public int getRandomAmount() {
        return amount.getValue();
    }

    public int getWeight() {
        return weight.getValue();
    }

}
