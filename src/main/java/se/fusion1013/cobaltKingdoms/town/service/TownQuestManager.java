package se.fusion1013.cobaltKingdoms.town.service;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.config.KingdomsConfig;
import se.fusion1013.cobaltKingdoms.quest.model.AbstractQuest;
import se.fusion1013.cobaltKingdoms.quest.repository.IQuestRepository;
import se.fusion1013.cobaltKingdoms.quest.service.QuestManager;
import se.fusion1013.cobaltKingdoms.town.config.TownConfig;
import se.fusion1013.cobaltKingdoms.town.model.Town;
import se.fusion1013.cobaltKingdoms.town.repository.ITownRepository;

import java.util.List;
import java.util.Random;

public class TownQuestManager extends Manager<CobaltKingdoms> {

    private static final IQuestRepository questRepository = DataManager.getInstance().getDao(IQuestRepository.class);
    private static final ITownRepository townRepository = DataManager.getInstance().getDao(ITownRepository.class);
    private static final Random random = new Random();

    private int QuestSpawnDelay = 20 * 60 * 60;

    public TownQuestManager(CobaltKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        loadConfigValues();
        Bukkit.getScheduler().runTaskTimer(CobaltKingdoms.getInstance(), this::createNewQuests, 1, QuestSpawnDelay);

        TownManager.getInstance().subscribeOnTownSpawn(this::trySpawnQuestEntities);
    }

    @Override
    public void disable() {

    }

    private void loadConfigValues() {
        TownConfig townConfig = KingdomsConfig.getTownConfig();
        this.QuestSpawnDelay = 20 * townConfig.getQuestSpawnDelaySeconds();
    }

    private void createNewQuests() {
        for (Town town : TownManager.getInstance().getTowns()) {
            createNewQuest(town);
        }
    }

    private void createNewQuest(Town town) { // TODO: Quests should be created from the QuestManager
        QuestManager.getInstance().createRandomQuest(town);
    }

    private void trySpawnQuestEntities(Town town) {
        Location townCenter = town.getLocation();
        if (!townCenter.isChunkLoaded()) return;
        if (true) return;

        List<AbstractQuest> townQuests = questRepository.getQuests().stream()
                .filter(q -> q.shouldShowInMenu(town, null))
                .toList();
        for (AbstractQuest quest : townQuests) {// Summon new quest giver entity
            int xPos = random.nextInt(-6, 6);
            int yPos = random.nextInt(-6, 6);

            QuestManager.getInstance().summonQuestMarker(townCenter.clone().add(xPos, 0, yPos), quest);
        }

    }
}
