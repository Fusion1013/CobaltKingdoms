package se.fusion1013.cobaltKingdoms.kingdom.town;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.config.town.TownLevelConfig;
import se.fusion1013.cobaltKingdoms.database.kingdom.town.ITownRepository;
import se.fusion1013.cobaltKingdoms.database.quest.IQuestRepository;
import se.fusion1013.cobaltKingdoms.quest.QuestEntity;
import se.fusion1013.cobaltKingdoms.quest.QuestManager;
import se.fusion1013.cobaltKingdoms.quest.QuestStatus;
import se.fusion1013.cobaltKingdoms.quest.item_delivery.ItemDeliveryQuestEntity;

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

        TownManager.getInstance().onTownSpawn(this::trySpawnQuestEntities);
    }

    @Override
    public void disable() {

    }

    private void loadConfigValues() {
        FileConfiguration config = CobaltKingdoms.getInstance().getConfig();
        ConfigurationSection townConfig = config.getConfigurationSection("town");
        if (townConfig == null) return;

        int questSpawnDelayMinutes = townConfig.getInt("quest_spawn_delay_m");
        int questSpawnDelaySeconds = townConfig.getInt("quest_spawn_delay_s");
        this.QuestSpawnDelay = 20 * questSpawnDelaySeconds + 20 * 60 * questSpawnDelayMinutes;
    }

    private void createNewQuests() {
        for (TownEntity town : TownManager.getInstance().getTowns()) {
            createNewQuest(town);
        }
    }

    private void createNewQuest(TownEntity town) { // TODO: Quests should be created from the QuestManager
        List<QuestEntity> quests = questRepository.getQuests(town).stream().filter(q -> q.getStatus() == QuestStatus.NEW || q.getStatus() == QuestStatus.ACTIVE).toList();
        TownLevelConfig levelConfig = town.getLevelConfig();
        if (quests.size() >= levelConfig.getMaxSimultaneousQuests()) return;

        List<TownEntity> list = townRepository.getTowns().stream().filter(t -> !t.getId().equals(town.getId())).toList();
        if (list.isEmpty()) return;

        ItemDeliveryQuestEntity quest = ItemDeliveryQuestEntity.createRandom(town, list.get(random.nextInt(list.size())));
        questRepository.insertQuest(quest);

        CobaltKingdoms.getInstance().getLogger().info("Created new quest");
    }

    private void trySpawnQuestEntities(TownEntity town) {
        CobaltKingdoms.getInstance().getLogger().info("Trying to spawn quest entities");
        Location townCenter = town.getLocation();
        if (!townCenter.isChunkLoaded()) {
            CobaltKingdoms.getInstance().getLogger().info("Not chunk loaded");
            return;
        }

        List<QuestEntity> townQuests = questRepository.getQuests().stream()
                .filter(q -> q.getQuestData().shouldShowInMenu(town, null))
                .toList();
        CobaltKingdoms.getInstance().getLogger().info("Found quests: " + townQuests.size());
        for (QuestEntity quest : townQuests) {// Summon new quest giver entity
            int xPos = random.nextInt(-6, 6);
            int yPos = random.nextInt(-6, 6);

            QuestManager.getInstance().summonQuestMarker(townCenter.clone().add(xPos, 0, yPos), quest);
        }

    }
}
