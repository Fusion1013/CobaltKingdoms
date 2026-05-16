package se.fusion1013.cobaltKingdoms.quest.bounty;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.database.quest.IQuestRepository;
import se.fusion1013.cobaltKingdoms.database.quest.bounty.IBountyRepository;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownEntity;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownJailEntity;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownManager;
import se.fusion1013.cobaltKingdoms.quest.ActivePlayerQuestEntity;
import se.fusion1013.cobaltKingdoms.quest.QuestEntity;
import se.fusion1013.cobaltKingdoms.quest.QuestStatus;
import se.fusion1013.cobaltKingdoms.quest.QuestType;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class BountyManager extends Manager<CobaltKingdoms> implements Listener {

    private static final Random random = new Random();
    private static final DataManager dataManager = DataManager.getInstance();
    private static final IQuestRepository questRepository = dataManager.getDao(IQuestRepository.class);
    private static final IBountyRepository bountyRepository = dataManager.getDao(IBountyRepository.class);

    public Response create(Player owner, PlayerProfile target, String reason, ItemStack reward) {
        BountyPlayerStatusEntity playerBountyStatus = bountyRepository.getPlayerBountyStatus(target.getId(), target.getName());
        if (!playerBountyStatus.isBountiesEnabled()) return Response.error("Target does not have bounties enabled");

        if (owner.getUniqueId().equals(target.getId())) return Response.error("Cannot create a bounty for yourself");

        List<BountyQuestEntity> oldBounty = bountyRepository.getBounties(owner, target)
                .stream().filter(bq -> {
                    QuestEntity quest = bq.getQuest();
                    if (quest.getStatus() == QuestStatus.NEW) return true;
                    return quest.getStatus() == QuestStatus.ACTIVE;
                }).toList();
        if (!oldBounty.isEmpty()) return Response.error("You already have an active bounty for that player");

        List<TownEntity> towns = TownManager.getInstance().getPlayerTowns(owner);
        if (towns == null || towns.isEmpty())
            return Response.error("You need to be a part of a town to create a bounty");
        TownEntity town = towns.getFirst();

        QuestEntity quest = new QuestEntity(QuestType.BOUNTY, new Date(), 0, 0, 0, 0, QuestStatus.NEW, town, town);
        quest.setCanDespawn(false);

        BountyQuestEntity bounty = new BountyQuestEntity();
        bounty.setTarget(target);
        bounty.setOwner(owner);
        bounty.setQuest(quest);
        bounty.setReason(reason);
        bounty.setReward(reward);

        bountyRepository.insertQuest(bounty);
        LocaleManager.getInstance().broadcastMessage(CobaltKingdoms.getInstance(), "kingdoms.quests.bounty.create_broadcast", StringPlaceholders.builder()
                .addPlaceholder("player", owner.getName())
                .addPlaceholder("target", target.getName())
                .build());

        return Response.ok("Created new bounty");
    }

    public Response recall(Player owner, PlayerProfile target) {
        List<BountyQuestEntity> oldBounty = bountyRepository.getBounties(owner, target)
                .stream().filter(bq -> {
                    QuestEntity quest = bq.getQuest();
                    if (quest.getStatus() == QuestStatus.NEW) return true;
                    return quest.getStatus() == QuestStatus.ACTIVE;
                }).toList();
        if (oldBounty.isEmpty()) return Response.error("You do not have an active bounty for this player");

        oldBounty.forEach(b -> {
            questRepository.updateStatus(b.getQuest().getId(), QuestStatus.DESPAWNED);
        });
        return Response.ok("Recalled quest");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player deadPlayer = event.getPlayer();
        Player killer = deadPlayer.getKiller();

        if (killer == null) return;

        // Check if dead player has active bounty
        List<BountyQuestEntity> bounties = bountyRepository.getBounties(deadPlayer.getUniqueId());
        if (bounties.isEmpty()) return;

        List<BountyQuestEntity> filteredBounties = bounties.stream()
                .filter(q -> q.getQuest().getStatus() == QuestStatus.NEW || q.getQuest().getStatus() == QuestStatus.ACTIVE)
                .toList();
        if (filteredBounties.isEmpty()) return;

        for (BountyQuestEntity bounty : filteredBounties) {
            tryCompleteBounty(bounty, killer, deadPlayer);
        }
    }

    private void tryCompleteBounty(BountyQuestEntity bounty, Player killer, Player deadPlayer) {
        if (bounty == null) return;

        QuestEntity quest = bounty.getQuest();
        if (quest.getStatus() != QuestStatus.ACTIVE) return;

        Optional<List<ActivePlayerQuestEntity>> killerQuestsOptional = questRepository.getActivePlayerQuestsByPlayer(killer);
        if (killerQuestsOptional.isEmpty()) return;

        List<ActivePlayerQuestEntity> killerQuests = killerQuestsOptional.get();
        boolean hasQuest = killerQuests.stream().anyMatch(aq -> aq.getQuest().getId().equals(quest.getId()));

        if (!hasQuest) return;

        setPlayerRespawnJail(quest.getStartTown(), deadPlayer);

        Bukkit.getScheduler().runTaskLater(CobaltKingdoms.getInstance(), () -> {
            bounty.tryComplete(killer, killer.getLocation(), null);

            ItemStack bountyCoin = BountyQuestUtil.getBountyItem(deadPlayer);
            if (bountyCoin != null) {
                killer.give(bountyCoin);
            }

            questRepository.updateStatus(quest.getId(), QuestStatus.COMPLETED);

            // Increment status
            BountyPlayerStatusEntity killerBountyStatus = bountyRepository.getPlayerBountyStatus(killer);
            killerBountyStatus.incrementCompleted();
            bountyRepository.insertPlayerBountyStatus(killerBountyStatus);

            BountyPlayerStatusEntity targetBountyStatus = bountyRepository.getPlayerBountyStatus(deadPlayer);
            targetBountyStatus.incrementKilled();
            bountyRepository.insertPlayerBountyStatus(targetBountyStatus);

            LocaleManager.getInstance().broadcastMessage(CobaltKingdoms.getInstance(), "kingdoms.quests.bounty.complete", StringPlaceholders.builder()
                    .addPlaceholder("killer", killer.getName())
                    .addPlaceholder("target", deadPlayer.getName())
                    .build());
            killer.playSound(killer, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);

            ItemStack reward = bounty.getReward();
            if (reward == null || reward.isEmpty()) return;

            killer.give(reward);
        }, 1);
    }

    private void setPlayerRespawnJail(TownEntity town, Player player) {
        if (town == null) return;

        List<TownJailEntity> jails = TownManager.getInstance().getJails(town);
        if (jails.isEmpty()) return;

        TownJailEntity jail = jails.get(random.nextInt(jails.size()));
        if (jail == null) return;

        player.setRespawnLocation(jail.getLocation(), true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        List<BountyQuestEntity> bounties = bountyRepository.getBounties(player.getUniqueId());
        if (bounties.isEmpty()) return;

        List<BountyQuestEntity> filteredBounties = bounties.stream()
                .filter(q -> q.getQuest().getStatus() == QuestStatus.NEW || q.getQuest().getStatus() == QuestStatus.ACTIVE)
                .toList();
        if (filteredBounties.isEmpty()) return;

        BountyQuestEntity bounty = filteredBounties.getFirst();
        if (bounty == null) return;

        LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.quests.bounty.join_warning", StringPlaceholders.builder()
                .addPlaceholder("player", bounty.getOwnerPlayerName())
                .build());
    }

    public BountyManager(CobaltKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltKingdoms.getInstance());
    }

    @Override
    public void disable() {

    }

    private static BountyManager INSTANCE;

    public static BountyManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BountyManager(CobaltKingdoms.getInstance());
        }
        return INSTANCE;
    }

    public Response setPlayerBountiesEnabled(Player sender, boolean enabled) {
        BountyPlayerStatusEntity playerBountyStatus = bountyRepository.getPlayerBountyStatus(sender);

        Date updateTimestamp = playerBountyStatus.getUpdateTimestamp();
        if (updateTimestamp != null) {
            // Check if player can update their status
            Duration waitTime = Duration.ofDays(7);
            Instant updateTimeInstant = updateTimestamp.toInstant();

            Instant endTime = updateTimeInstant.plus(waitTime);
            Instant currentTime = Instant.now();

            Duration remaining = Duration.between(currentTime, endTime);

            long days = remaining.toDays();
            long hours = remaining.toHoursPart();
            long minutes = remaining.toMinutesPart();

            String formatted = String.format("%d days, %d hours, %d minutes",
                    days, hours, minutes);

            if (endTime.isAfter(currentTime))
                return Response.error("You cannot change your bounty status for another " + formatted);
        }

        playerBountyStatus.setBountiesEnabled(enabled);
        playerBountyStatus.setUpdateTimestamp(new Date());
        bountyRepository.insertPlayerBountyStatus(playerBountyStatus);
        return Response.ok("Updated status");
    }

    @EventHandler
    public void onPlayerEditBook(PlayerEditBookEvent event) {
        if (!event.isSigning()) return;

        Player sendingPlayer = event.getPlayer();
        BookMeta bookMeta = event.getNewBookMeta();

        if (!bookMeta.getPersistentDataContainer().has(new NamespacedKey(CobaltCore.getInstance(), "bounty_letter")))
            return;

        String targetName = bookMeta.getTitle();
        if (targetName == null) {
            event.setCancelled(true);
            return;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetName);
        Response response = create(sendingPlayer, offlinePlayer.getPlayerProfile(), bookMeta.getPage(1), null);
        if (response.ok()) {
            sendingPlayer.getInventory().getItemInMainHand().setAmount(0);
        } else {
            event.setCancelled(true);
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), sendingPlayer, "kingdoms.quests.bounty.create_fail", StringPlaceholders.builder()
                    .addPlaceholder("reason", response.message())
                    .build());
        }

    }
}
