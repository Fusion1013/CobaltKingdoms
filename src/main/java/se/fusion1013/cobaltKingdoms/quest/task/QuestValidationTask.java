package se.fusion1013.cobaltKingdoms.quest.task;

import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltKingdoms.quest.model.AbstractQuest;
import se.fusion1013.cobaltKingdoms.quest.model.PlayerQuest;
import se.fusion1013.cobaltKingdoms.quest.model.QuestStatus;
import se.fusion1013.cobaltKingdoms.quest.repository.IQuestRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class QuestValidationTask implements Runnable {

    private static final DataManager dataManager = DataManager.getInstance();
    private static final IQuestRepository questRepository = dataManager.getDao(IQuestRepository.class);

    @Override
    public void run() {
        validateQuests();
    }

    private void validateQuests() {
        List<AbstractQuest> quests = questRepository.getQuestsWithoutStatus(QuestStatus.DESPAWNED);
        despawnInvalidQuests(quests);
        despawnExpiredQuests(quests);
        despawnExpiredPlayerQuests();

    }

    private static void despawnExpiredPlayerQuests() {
        // Iterate over quests that are active
        List<PlayerQuest> activeQuests = questRepository.getPlayerQuests();
        List<PlayerQuest> activeQuestsFiltered = activeQuests.stream()
                .filter(q -> q.getQuest().getQuestStatus() == QuestStatus.NEW || q.getQuest().getQuestStatus() == QuestStatus.ACTIVE)
                .toList();

        for (PlayerQuest quest : activeQuestsFiltered) {
            Instant expiryTime = quest.getExpiryTime().toInstant();
            if (expiryTime.isAfter(Instant.now())) continue;
            if (!quest.getQuest().canDespawn()) continue;

            // Mark the quest as failed
            questRepository.updateStatus(quest.getQuest().getQuestId(), QuestStatus.FAILED);
            // TODO: Send a message to the player if they are online
        }
    }

    private static void despawnExpiredQuests(List<AbstractQuest> quests) {
        List<AbstractQuest> newQuests = quests.stream().filter(q -> q.getQuestStatus() == QuestStatus.NEW).toList();

        // Iterate over quests that have not been claimed
        for (AbstractQuest quest : newQuests) {
            Instant createdTimestamp = quest.getCreatedTimestamp().toInstant();
            Instant expiresAt = createdTimestamp.plus(Duration.ofMinutes(60));
            if (expiresAt.isAfter(Instant.now())) continue;
            if (!quest.canDespawn()) continue;

            // Mark the quest as despawned
            questRepository.updateStatus(quest.getQuestId(), QuestStatus.DESPAWNED);
        }
    }

    private static void despawnInvalidQuests(List<AbstractQuest> quests) {
        for (AbstractQuest quest : quests) {
            if (quest.isValid()) continue;
            questRepository.updateStatus(quest.getQuestId(), QuestStatus.DESPAWNED);
        }
    }

}
