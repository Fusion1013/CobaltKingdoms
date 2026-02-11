package se.fusion1013.cobaltKingdoms.items.kit;

import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.util.INameProvider;

public interface IKit extends INameProvider {

    String getId();

    void apply(Player player);

}
