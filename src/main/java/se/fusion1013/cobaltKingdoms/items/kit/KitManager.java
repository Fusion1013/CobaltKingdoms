package se.fusion1013.cobaltKingdoms.items.kit;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.CobaltPlugin;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.util.FileUtil;
import se.fusion1013.cobaltCore.util.IFileConstructor;
import se.fusion1013.cobaltCore.util.INameProvider;
import se.fusion1013.cobaltCore.util.IProviderStorage;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

import java.util.HashMap;
import java.util.Map;

public class KitManager extends Manager<CobaltKingdoms> {

    private static final Map<String, IKit> KITS = new HashMap<>();

    public KitManager(CobaltKingdoms plugin) {
        super(plugin);
    }

    public static void loadKitFiles(CobaltPlugin plugin, boolean overwrite) {
        FileUtil.loadFilesInto(plugin, "kit/", new IProviderStorage() {
            @Override
            public void put(String key, INameProvider provider) {
                register(provider);
            }

            @Override
            public boolean has(String key) {
                return getKit(key) != null;
            }

            @Override
            public INameProvider get(String key) {
                return getKit(key);
            }
        }, new IFileConstructor() {
            @Override
            public INameProvider createFrom(YamlConfiguration yaml) {
                return Kit.create(yaml);
            }

            @Override
            public INameProvider createFrom(JsonObject json) {
                return null;
            }
        }, overwrite);
    }

    public static void reloadKits() {
        for (CobaltPlugin plugin : CobaltCore.getRegisteredCobaltPlugins()) {
            loadKitFiles(plugin, true);
        }
    }

    @Override
    public void reload() {
    }

    @Override
    public void disable() {

    }

    public static IKit register(INameProvider kit) {
        return register((IKit) kit);
    }

    public static boolean applyKit(Player player, String kitId) {
        IKit kit = getKit(kitId);
        if (kit == null) return false;
        kit.apply(player);
        return true;
    }

    public static IKit register(IKit kit) {
        KITS.put(kit.getId(), kit);
        return kit;
    }

    public static IKit getKit(String id) {
        return KITS.get(id);
    }

    public static String[] getKitIds() {
        return KITS.keySet().toArray(new String[0]);
    }

    private static KitManager INSTANCE;

    public static KitManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new KitManager(CobaltKingdoms.getInstance());
        }
        return INSTANCE;
    }
}
