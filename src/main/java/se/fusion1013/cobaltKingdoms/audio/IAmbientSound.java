package se.fusion1013.cobaltKingdoms.audio;

import se.fusion1013.cobaltCore.manager.registry.IRegistryItem;

public interface IAmbientSound extends IRegistryItem {

    String getKey();

    int getLengthTicks();

    int getOverlapTicks();

}
