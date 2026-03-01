package se.fusion1013.cobaltKingdoms.entities.armorstand;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.CobaltPlugin;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.util.FileUtil;
import se.fusion1013.cobaltCore.util.IFileConstructor;
import se.fusion1013.cobaltCore.util.INameProvider;
import se.fusion1013.cobaltCore.util.IProviderStorage;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.entities.armorstand.loader.ArmorStandPoseLoader;

import java.util.HashMap;
import java.util.Map;

public class ArmorStandManager extends Manager<CobaltKingdoms> {

    private static final Map<String, ArmorStandPose> POSES = new HashMap<>();
    private static final ArmorStandPoseLoader POSE_LOADER = new ArmorStandPoseLoader();

    public ArmorStandManager(CobaltKingdoms plugin) {
        super(plugin);
        INSTANCE = this;
    }

    public static void loadArmorStandFiles(CobaltPlugin plugin, boolean overwrite) {
        FileUtil.loadFilesInto(plugin, "armor_stand/", new IProviderStorage<INameProvider>() {
            @Override
            public void put(String key, INameProvider provider) {
                register(provider);
            }

            @Override
            public boolean has(String key) {
                return getArmorPose(key) != null;
            }

            @Override
            public INameProvider get(String key) {
                return getArmorPose(key);
            }
        }, new IFileConstructor() {
            @Override
            public INameProvider createFrom(YamlConfiguration yamlConfiguration) {
                return POSE_LOADER.load(yamlConfiguration);
            }

            @Override
            public INameProvider createFrom(JsonObject jsonObject) {
                return POSE_LOADER.load(jsonObject);
            }
        }, overwrite);
    }

    public static void reloadPoses() {
        for (CobaltPlugin plugin : CobaltCore.getRegisteredCobaltPlugins()) {
            loadArmorStandFiles(plugin, true);
        }
    }

    @Override
    public void reload() {
        reloadPoses();
    }

    @Override
    public void disable() {

    }

    public static ArmorStandPose register(INameProvider pose) {
        return register((ArmorStandPose) pose);
    }

    public static ArmorStandPose register(ArmorStandPose pose) {
        POSES.put(pose.getInternalName(), pose);
        return pose;
    }

    public static ArmorStandPose getArmorStandPose(String id) {
        return POSES.getOrDefault(id, null);
    }

    public static String[] getArmorStandPoseNames() {
        return POSES.keySet().toArray(new String[0]);
    }

    public static ArmorStandPose getArmorPose(String key) {
        return POSES.get(key);
    }

    private static ArmorStandManager INSTANCE;

    public static ArmorStandManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ArmorStandManager(CobaltKingdoms.getInstance());
        }
        return INSTANCE;
    }
}
