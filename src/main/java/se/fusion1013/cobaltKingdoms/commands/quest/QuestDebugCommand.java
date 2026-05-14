package se.fusion1013.cobaltKingdoms.commands.quest;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.config.KingdomsConfig;
import se.fusion1013.cobaltKingdoms.config.quest.QuestConfig;
import se.fusion1013.cobaltKingdoms.quest.*;

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
        List<QuestEntity> list = QuestManager.getInstance().getAllQuests().stream().filter(q -> q.getStatus() == QuestStatus.NEW || q.getStatus() == QuestStatus.ACTIVE).toList();
        for (QuestEntity quest : list) {
            QuestManager.getInstance().setQuestStatus(quest.getId(), QuestStatus.DESPAWNED);
        }
    }

    private static void sampleItemDistribution(Player player, CommandArguments args) {
        QuestConfig questConfig = KingdomsConfig.getQuestConfig();

        CobaltKingdoms.getInstance().getLogger().info("---- SAMPLING REQUIREMENT POOL ----");
        samplePool(questConfig.getRequirementPool());

        CobaltKingdoms.getInstance().getLogger().info("---- SAMPLING REWARD POOL ----");
        samplePool(questConfig.getRewardPool());
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
