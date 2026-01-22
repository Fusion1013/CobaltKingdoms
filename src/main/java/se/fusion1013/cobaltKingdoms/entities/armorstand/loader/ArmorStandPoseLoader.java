package se.fusion1013.cobaltKingdoms.entities.armorstand.loader;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.loader.AbstractFileLoader;
import se.fusion1013.cobaltCore.loader.IFileLoaderComponent;
import se.fusion1013.cobaltKingdoms.entities.armorstand.ArmorStandPose;

public class ArmorStandPoseLoader extends AbstractFileLoader<ArmorStandPose, ArmorStandPose.Builder> {

    private static final IArmorStandPoseLoaderComponent[] LOADERS = new IArmorStandPoseLoaderComponent[]{
            new ArmorStandPoseRotationsLoader()
    };

    @Override
    public ArmorStandPose load(YamlConfiguration yaml) {
        String internalName = yaml.getString("internal_name");
        if (internalName == null) return null;

        ArmorStandPose.Builder builder = new ArmorStandPose.Builder(internalName);
        for (IArmorStandPoseLoaderComponent loader : LOADERS) loader.load(yaml, builder);
        return builder.build();
    }

    @Override
    public ArmorStandPose load(JsonObject json) {
        return null;
    }

    @Override
    protected IFileLoaderComponent<ArmorStandPose.Builder>[] getLoaders() {
        return LOADERS;
    }

}
