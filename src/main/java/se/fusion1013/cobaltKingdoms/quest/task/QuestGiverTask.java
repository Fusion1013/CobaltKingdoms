package se.fusion1013.cobaltKingdoms.quest.task;

import org.bukkit.Location;
import org.bukkit.entity.Parrot;

import java.util.HashMap;
import java.util.Map;

public class QuestGiverTask implements Runnable {

    // TODO: Replace with a list of some custom class
    // TODO: Store the spawn time and remove the entity after some time
    public static final Map<Location, Parrot> QUEST_GIVER_ENTITIES = new HashMap<>();

    @Override
    public void run() {
        tickQuestGiverEntities();
    }

    private void tickQuestGiverEntities() {
        for (Map.Entry<Location, Parrot> locationParrotEntry : QUEST_GIVER_ENTITIES.entrySet()) {
            Location location = locationParrotEntry.getKey();
            Parrot parrot = locationParrotEntry.getValue();

            if (location.isChunkLoaded()) {
                parrot.getPathfinder().moveTo(location);
            } else {
                parrot.remove();
            }
        }

    }

}
