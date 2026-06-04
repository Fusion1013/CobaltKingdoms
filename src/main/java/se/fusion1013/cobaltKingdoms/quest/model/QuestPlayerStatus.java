package se.fusion1013.cobaltKingdoms.quest.model;

public record QuestPlayerStatus(Long questId, String playerName, QuestStatus status, QuestType questType) {
}
