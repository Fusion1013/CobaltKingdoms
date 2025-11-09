package se.fusion1013.cobaltKingdoms;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.CobaltPlugin;
import se.fusion1013.cobaltKingdoms.events.PortalEvents;
import se.fusion1013.cobaltKingdoms.pigeon.PigeonEvents;

public final class CobaltKingdoms extends JavaPlugin implements CobaltPlugin {

    private static CobaltKingdoms INSTANCE;

    public CobaltKingdoms() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        CobaltCore.getInstance().registerCobaltPlugin(this);
    }

    @Override
    public void onDisable() {
        CobaltCore.getInstance().disableCobaltPlugin(this);
    }

    @Override
    public void registerListeners() {
        CobaltPlugin.super.registerListeners();
        Bukkit.getPluginManager().registerEvents(new PigeonEvents(), this);
        Bukkit.getPluginManager().registerEvents(new PortalEvents(), this);
    }

    public static CobaltKingdoms getInstance() {
        return INSTANCE;
    }

    @Override
    public String getInternalName() {
        return "CobaltKingdoms";
    }
}
