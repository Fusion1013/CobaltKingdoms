package se.fusion1013.cobaltKingdoms.quest.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltKingdoms.config.KingdomsConfig;
import se.fusion1013.cobaltKingdoms.config.town.TownConfig;
import se.fusion1013.cobaltKingdoms.config.town.TownLevelConfig;
import se.fusion1013.cobaltKingdoms.database.quest.IQuestRepository;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownEntity;
import se.fusion1013.cobaltKingdoms.quest.ActivePlayerQuestEntity;
import se.fusion1013.cobaltKingdoms.quest.IQuestData;
import se.fusion1013.cobaltKingdoms.quest.QuestEntity;
import se.fusion1013.cobaltKingdoms.quest.QuestStatus;

public class QuestMenu extends Menu {

    public QuestMenu(TownEntity startTown) {
        this.setSize(9 * 4);

        TownConfig townConfig = KingdomsConfig.getTownConfig();
        TownLevelConfig townLevelConfig = townConfig.getTownLevelConfig(startTown.getExperience());

        this.setTitle("Town level " + townLevelConfig.getLevel() + " [" + startTown.getExperience() + "/" + townConfig.getNextLevelXpThreshold(townLevelConfig.getLevel()) + "xp] ");

        int maxTradeRoutes = 9 * 4;
        int tradeRouteCount = 0;

        // Add buttons for each trade destination
        for (QuestEntity quest : startTown.getQuests()) {
            if (tradeRouteCount >= maxTradeRoutes) break;
            if (quest.getStatus() != QuestStatus.NEW) continue;

            IQuestData questData = quest.getQuestData();
            if (questData == null) continue;

            // Create the button for each trader
            this.addButton(new Button(tradeRouteCount) {

                @Override
                public ItemStack getItem() {
                    return questData.getButtonItem();
                }

                @Override
                public void onClick(Player player) {
                    checkMissionRequirements(player, startTown, quest, questData);
                }
            });

            tradeRouteCount++;
        }
    }

    private void checkMissionRequirements(Player player, TownEntity town, QuestEntity quest, IQuestData questData) {
        if (quest.getStatus() != QuestStatus.NEW) {
            player.sendMessage(Component.text(
                    "This mission has already been taken by another player.", NamedTextColor.RED));
            return;
        }

        boolean isValidQuest = questData.validateQuest(player);

        if (isValidQuest) {
            new ConfirmMenu(town, quest, questData).displayTo(player);
        }
    }

    private class ConfirmMenu extends Menu {

        public ConfirmMenu(TownEntity town, QuestEntity quest, IQuestData questData) {
            this.setSize(9);
            this.setTitle("Accept Quest?");

            this.addButton(new Button(2) {
                @Override
                public ItemStack getItem() {
                    final ItemStack item = new ItemStack(Material.RED_TERRACOTTA);
                    final ItemMeta meta = item.getItemMeta();
                    meta.displayName(Component.text("Deny", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
                    item.setItemMeta(meta);
                    return item;
                }

                @Override
                public void onClick(Player player) {
                    QuestMenu outerMenu = QuestMenu.this;
                    outerMenu.displayTo(player);
                }
            });

            this.addButton(new Button(6) {
                @Override
                public ItemStack getItem() {
                    final ItemStack item = new ItemStack(Material.LIME_TERRACOTTA);
                    final ItemMeta meta = item.getItemMeta();
                    meta.displayName(Component.text("Confirm", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
                    item.setItemMeta(meta);
                    return item;
                }

                @Override
                public void onClick(Player player) {
                    initiateTradeMission(player, town, quest, questData);
                    player.closeInventory();
                }
            });
        }

        private void initiateTradeMission(Player player, TownEntity town, QuestEntity quest, IQuestData questData) {
            ActivePlayerQuestEntity activeQuest;
            quest.acquireLock();
            try {
                if (quest.getStatus() != QuestStatus.NEW) {
                    player.sendMessage(Component.text(
                            "This mission has already been taken by another player.", NamedTextColor.RED));
                    return;
                } else if (!quest.isValid()) {
                    player.sendMessage(Component.text(
                            "This mission is invalid, try refreshing the menu.", NamedTextColor.RED));
                    return;
                }

                activeQuest = ActivePlayerQuestEntity.initiateMission(player, quest);
            } finally {
                quest.releaseLock();
            }

            questData.start(player, town.getLocation());
            IQuestRepository data = DataManager.getInstance().getDao(IQuestRepository.class);
            data.updateStatus(quest.getId(), QuestStatus.ACTIVE);
            data.insertActiveQuest(activeQuest);
        }
    }
}