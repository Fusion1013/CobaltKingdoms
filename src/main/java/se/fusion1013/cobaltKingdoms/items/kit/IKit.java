package se.fusion1013.cobaltKingdoms.items.kit;

import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.manager.registry.IRegistryItem;

public interface IKit extends IRegistryItem {

    String getId();

    void apply(Player player);

}
