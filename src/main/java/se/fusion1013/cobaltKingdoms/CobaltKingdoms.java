package se.fusion1013.cobaltKingdoms;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.CobaltPlugin;
import se.fusion1013.cobaltKingdoms.commands.kingdom.KingdomCommand;
import se.fusion1013.cobaltKingdoms.commands.StatusCommand;
import se.fusion1013.cobaltKingdoms.database.KingdomDataManager;
import se.fusion1013.cobaltKingdoms.events.AnvilEvents;
import se.fusion1013.cobaltKingdoms.events.FishingEvents;
import se.fusion1013.cobaltKingdoms.events.ItemEvents;
import se.fusion1013.cobaltKingdoms.events.PortalEvents;
import se.fusion1013.cobaltKingdoms.villager.VillagerEvents;
import se.fusion1013.cobaltKingdoms.items.KingdomItems;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomManager;
import se.fusion1013.cobaltKingdoms.pigeon.PigeonEvents;
import se.fusion1013.cobaltKingdoms.player.PlayerManager;
import se.fusion1013.cobaltKingdoms.villager.VillagerManager;

public class CobaltKingdoms extends JavaPlugin implements CobaltPlugin {

    private static CobaltKingdoms INSTANCE;

    public CobaltKingdoms() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        CobaltCore.getInstance().registerCobaltPlugin(this);
        KingdomItems.register();
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
        Bukkit.getPluginManager().registerEvents(new VillagerEvents(), this);
        Bukkit.getPluginManager().registerEvents(CobaltCore.getInstance().getManager(this, KingdomManager.class), this);
        Bukkit.getPluginManager().registerEvents(CobaltCore.getInstance().getManager(this, PlayerManager.class), this);
        Bukkit.getPluginManager().registerEvents(new AnvilEvents(), this);
        Bukkit.getPluginManager().registerEvents(new FishingEvents(), this);
        Bukkit.getPluginManager().registerEvents(new ItemEvents(), this);
    }

    @Override
    public void reloadManagers() {
        CobaltPlugin.super.reloadManagers();
        CobaltCore.getInstance().getManager(this, KingdomDataManager.class);
        CobaltCore.getInstance().getManager(this, KingdomManager.class);
        CobaltCore.getInstance().getManager(this, VillagerManager.class);
    }

    @Override
    public void registerCommands() {
        CobaltPlugin.super.registerCommands();
        KingdomCommand.register();
        StatusCommand.register();
    }

    public static CobaltKingdoms getInstance() {
        return INSTANCE;
    }

    @Override
    public String getInternalName() {
        return "CobaltKingdoms";
    }

    @Override
    public String getPrefix() {
        return "prefix.kingdoms";
    }
}
