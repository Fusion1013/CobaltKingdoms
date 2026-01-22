package se.fusion1013.cobaltKingdoms.entities.armorstand.loader;

import com.google.gson.JsonObject;
import io.papermc.paper.math.Rotations;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltKingdoms.entities.armorstand.ArmorStandPose;

public class ArmorStandPoseRotationsLoader implements IArmorStandPoseLoaderComponent {
    @Override
    public void load(YamlConfiguration yaml, ArmorStandPose.Builder builder) {
        loadRotations(yaml, builder);
    }

    private void loadRotations(YamlConfiguration yaml, ArmorStandPose.Builder builder) {
        ConfigurationSection body = yaml.getConfigurationSection("body");
        ConfigurationSection head = yaml.getConfigurationSection("head");
        ConfigurationSection leftArm = yaml.getConfigurationSection("left_arm");
        ConfigurationSection leftLeg = yaml.getConfigurationSection("left_leg");
        ConfigurationSection rightArm = yaml.getConfigurationSection("right_arm");
        ConfigurationSection rightLeg = yaml.getConfigurationSection("right_leg");

        builder.setBodyRotation(getRotations(body));
        builder.setHeadRotation(getRotations(head));
        builder.setLeftArmRotation(getRotations(leftArm));
        builder.setLeftLegRotation(getRotations(leftLeg));
        builder.setRightArmRotation(getRotations(rightArm));
        builder.setRightLegRotation(getRotations(rightLeg));
    }

    private Rotations getRotations(ConfigurationSection yaml) {
        double x = yaml.getDouble("x");
        double y = yaml.getDouble("y");
        double z = yaml.getDouble("z");
        return Rotations.ofDegrees(x, y, z);
    }

    @Override
    public void load(JsonObject json, ArmorStandPose.Builder builder) {
        loadRotations(json, builder);
    }

    private void loadRotations(JsonObject json, ArmorStandPose.Builder builder) {

    }
}
