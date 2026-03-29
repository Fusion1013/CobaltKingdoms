package se.fusion1013.cobaltKingdoms.items.kit;

import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.manager.registry.FileLoadedRegistry;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

public class KitManager extends Manager<CobaltKingdoms> {

    private static final FileLoadedRegistry<IKit> KITS = new FileLoadedRegistry<>(
            CobaltKingdoms.getInstance(),
            "kit",
            Kit::create,
            Kit::create,
            (p, k) -> {
            }
    );

    public KitManager(CobaltKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        KITS.reload();
    }

    @Override
    public void disable() {

    }

    public static boolean applyKit(Player player, String kitId) {
        IKit kit = getKit(kitId);
        if (kit == null) return false;
        kit.apply(player);
        return true;
    }

    public static IKit getKit(String id) {
        return KITS.get(id);
    }

    public static String[] getKitIds() {
        return KITS.getNames();
    }

    private static KitManager INSTANCE;

    public static KitManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new KitManager(CobaltKingdoms.getInstance());
        }
        return INSTANCE;
    }
}
