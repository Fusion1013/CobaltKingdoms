package se.fusion1013.cobaltKingdoms.quest.model;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.config.KingdomsConfig;
import se.fusion1013.cobaltKingdoms.quest.repository.IQuestRepository;
import se.fusion1013.cobaltKingdoms.town.config.TownConfig;
import se.fusion1013.cobaltKingdoms.town.config.TownLevelConfig;
import se.fusion1013.cobaltKingdoms.town.model.Town;

import java.util.List;

public class QuestMenu extends Menu {

    public QuestMenu(Town startTown, @NotNull Player player) {
        this.setSize(9 * 4);

        TownConfig townConfig = KingdomsConfig.getTownConfig();
        TownLevelConfig townLevelConfig = townConfig.getTownLevelConfig(startTown.getExperience());

        this.setTitle("Town level " + townLevelConfig.getLevel() + " [" + startTown.getExperience() + "/" + townConfig.getNextLevelXpThreshold(townLevelConfig.getLevel()) + "xp] ");

        int maxTradeRoutes = 9 * 4;
        int tradeRouteCount = 0;

        List<AbstractQuest> quests = DataManager.getInstance().getDao(IQuestRepository.class).getQuests();

        // Add buttons for each trade destination
        for (AbstractQuest quest : quests) {
            if (tradeRouteCount >= maxTradeRoutes) break;
            if (quest.getQuestStatus() != QuestStatus.NEW) continue;

            if (!quest.shouldShowInMenu(startTown, player)) continue;

            // Create the button for each trader
            this.addButton(new Button(tradeRouteCount) {

                @Override
                public ItemStack getItem() {
                    return quest.getButtonItem();
                }

                @Override
                public void onClick(Player player) {
                    checkQuestRequirements(player, startTown, quest);
                }
            });

            tradeRouteCount++;
        }
    }

    private void checkQuestRequirements(Player player, Town town, AbstractQuest quest) {
        Response canClaim = quest.canClaim(player);
        if (canClaim.error()) {
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 1);
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.quests.claim_fail", StringPlaceholders.builder()
                    .addPlaceholder("reason", canClaim.message())
                    .build());
            return;
        }

        boolean isValidQuest = quest.validateQuest(player);

        if (isValidQuest) {
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
            new ConfirmMenu(town, quest).displayTo(player);
        } else {
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 1);
        }
    }

    private class ConfirmMenu extends Menu {

        public ConfirmMenu(Town town, AbstractQuest quest) {
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
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 1);
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
                    initiateTradeMission(player, town, quest);
                    player.closeInventory();
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                }
            });
        }

        private void initiateTradeMission(Player player, Town town, AbstractQuest quest) {
            PlayerQuest activeQuest;
            quest.acquireLock();
            try {
                if (quest.getQuestStatus() != QuestStatus.NEW) {
                    player.sendMessage(Component.text(
                            "This mission has already been taken by another player.", NamedTextColor.RED));
                    return;
                } else if (!quest.isValid()) {
                    player.sendMessage(Component.text(
                            "This mission is invalid, try refreshing the menu.", NamedTextColor.RED));
                    return;
                }

                activeQuest = PlayerQuest.initiateQuest(player, quest);
            } finally {
                quest.releaseLock();
            }

            quest.start(player, town.getLocation());
            IQuestRepository questRepository = DataManager.getInstance().getDao(IQuestRepository.class);
            questRepository.updateStatus(quest.getQuestId(), QuestStatus.ACTIVE);
            questRepository.createPlayerQuest(activeQuest);
        }
    }
}