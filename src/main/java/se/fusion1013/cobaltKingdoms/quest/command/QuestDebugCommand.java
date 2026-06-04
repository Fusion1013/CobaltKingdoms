package se.fusion1013.cobaltKingdoms.quest.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.config.KingdomsConfig;
import se.fusion1013.cobaltKingdoms.config.quest.QuestArtifactHuntConfig;
import se.fusion1013.cobaltKingdoms.config.quest.QuestConfig;
import se.fusion1013.cobaltKingdoms.config.quest.QuestItemDeliveryConfig;
import se.fusion1013.cobaltKingdoms.quest.model.AbstractQuest;
import se.fusion1013.cobaltKingdoms.quest.model.QuestItem;
import se.fusion1013.cobaltKingdoms.quest.model.QuestStatus;
import se.fusion1013.cobaltKingdoms.quest.service.QuestManager;
import se.fusion1013.cobaltKingdoms.quest.util.QuestUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestDebugCommand {

    private static final int MAX_SAMPLES = 500000;

    public static CommandAPICommand register() {
        return new CommandAPICommand("debug")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "debug"))
                .withSubcommand(new CommandAPICommand("item_distribution")
                        .executesPlayer(QuestDebugCommand::sampleItemDistribution))
                .withSubcommand(new CommandAPICommand("despawn_all")
                        .executesPlayer(QuestDebugCommand::despawnAllQuests));
    }

    private static void despawnAllQuests(Player player, CommandArguments args) {
        List<AbstractQuest> list = QuestManager.getInstance().getAllQuests().stream().filter(q -> q.getQuestStatus() == QuestStatus.NEW || q.getQuestStatus() == QuestStatus.ACTIVE).toList();
        for (AbstractQuest quest : list) {
            QuestManager.getInstance().setQuestStatus(quest.getQuestId(), QuestStatus.DESPAWNED);
        }
    }

    private static void sampleItemDistribution(Player player, CommandArguments args) {
        QuestConfig questConfig = KingdomsConfig.getQuestConfig();
        QuestArtifactHuntConfig artifactHuntConfig = questConfig.getArtifactHuntConfig();
        QuestItemDeliveryConfig itemDeliveryConfig = questConfig.getItemDeliveryConfig();

        CobaltKingdoms.getInstance().getLogger().info("ARTIFACT HUNT");
        CobaltKingdoms.getInstance().getLogger().info("---- SAMPLING REWARD POOL ----");
        samplePool(artifactHuntConfig.getRewardPool());


        CobaltKingdoms.getInstance().getLogger().info("ITEM DELIVERY");
        CobaltKingdoms.getInstance().getLogger().info("---- SAMPLING REQUIREMENT POOL ----");
        samplePool(itemDeliveryConfig.getRequirementPool());

        CobaltKingdoms.getInstance().getLogger().info("---- SAMPLING REWARD POOL ----");
        samplePool(itemDeliveryConfig.getRewardPool());
    }

    private static void samplePool(Map<QuestItem, Double> pool) {
        Map<String, Integer> counts = new HashMap<>();

        for (int i = 0; i < MAX_SAMPLES; i++) {
            QuestItem questItem = QuestUtil.sampleRandomItem(pool);
            Integer currentValue = counts.getOrDefault(questItem.itemName(), 0);
            counts.put(questItem.itemName(), currentValue + 1);
        }

        printSortedByValueDesc(counts);

    }

    private static void printSortedByValueDesc(Map<String, Integer> counts) {
        counts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry ->
                        {
                            double value = entry.getValue();
                            double percentage = (value / MAX_SAMPLES) * 100;
                            String percentageFormatted = formatDouble(percentage, 3);


                            CobaltKingdoms.getInstance().getLogger().info(" - " + entry.getKey() + " = " + percentageFormatted + "% [" + (int) value + "]");
                        }
                );
    }

    public static String formatDouble(double value, int decimals) {
        return String.format("%." + decimals + "f", value);
    }

}
