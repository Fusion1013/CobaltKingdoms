package se.fusion1013.cobaltKingdoms.entities.armorstand.loader;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.loader.IFileLoaderComponent;
import se.fusion1013.cobaltKingdoms.entities.armorstand.ArmorStandPose;

public interface IArmorStandPoseLoaderComponent extends IFileLoaderComponent<ArmorStandPose.Builder> {
    void load(YamlConfiguration yaml, ArmorStandPose.Builder builder);

    void load(JsonObject json, ArmorStandPose.Builder builder);
}
