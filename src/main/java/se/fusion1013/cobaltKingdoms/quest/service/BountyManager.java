package se.fusion1013.cobaltKingdoms.quest.service;

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
import se.fusion1013.cobaltKingdoms.quest.model.*;
import se.fusion1013.cobaltKingdoms.quest.repository.IBountyRepository;
import se.fusion1013.cobaltKingdoms.quest.repository.IQuestRepository;
import se.fusion1013.cobaltKingdoms.quest.util.BountyQuestUtil;
import se.fusion1013.cobaltKingdoms.town.model.Town;
import se.fusion1013.cobaltKingdoms.town.model.TownJail;
import se.fusion1013.cobaltKingdoms.town.service.TownJailManager;
import se.fusion1013.cobaltKingdoms.town.service.TownManager;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class BountyManager extends Manager<CobaltKingdoms> implements Listener {

    private static final Random random = new Random();
    private static final DataManager dataManager = DataManager.getInstance();
    private static final IQuestRepository questRepository = dataManager.getDao(IQuestRepository.class);
    private static final IBountyRepository bountyRepository = dataManager.getDao(IBountyRepository.class);

    public Response create(Player owner, PlayerProfile target, String reason, ItemStack reward) {
        BountyPlayerStatus playerBountyStatus = bountyRepository.getPlayerBountyStatus(owner);
        if (!playerBountyStatus.isBountiesEnabled()) return Response.error("You do not have bounties enabled");

        BountyPlayerStatus targetBountyStatus = bountyRepository.getPlayerBountyStatus(target.getId(), target.getName());
        if (!targetBountyStatus.isBountiesEnabled()) return Response.error("Target does not have bounties enabled");

        if (owner.getUniqueId().equals(target.getId())) return Response.error("Cannot create a bounty for yourself");

        List<BountyQuest> oldBounty = bountyRepository.getBounties(owner, target)
                .stream().filter(bq -> {
                    if (bq.getQuestStatus() == QuestStatus.NEW) return true;
                    return bq.getQuestStatus() == QuestStatus.ACTIVE;
                }).toList();
        if (!oldBounty.isEmpty()) return Response.error("You already have an active bounty for that player");

        List<Town> towns = TownManager.getInstance().getPlayerTowns(owner);
        if (towns == null || towns.isEmpty())
            return Response.error("You need to be a part of a town to create a bounty");
        Town town = towns.getFirst();

        BountyQuest bounty = new BountyQuest();
        bounty.setQuestType(QuestType.BOUNTY);
        bounty.setCreatedTimestamp(new Date());
        bounty.setMinRequirementValue(-1);
        bounty.setMaxRequirementValue(-1);
        bounty.setMinRewardValue(-1);
        bounty.setMaxRewardValue(-1);
        bounty.setQuestStatus(QuestStatus.NEW);
        bounty.setStartTown(town);
        bounty.setEndTown(town);
        bounty.setCanDespawn(false);
        bounty.setTarget(target);
        bounty.setOwner(owner);
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
        List<BountyQuest> oldBounty = bountyRepository.getBounties(owner, target)
                .stream().filter(bq -> {
                    if (bq.getQuestStatus() == QuestStatus.NEW) return true;
                    return bq.getQuestStatus() == QuestStatus.ACTIVE;
                }).toList();
        if (oldBounty.isEmpty()) return Response.error("You do not have an active bounty for this player");

        oldBounty.forEach(b -> {
            questRepository.updateStatus(b.getQuestId(), QuestStatus.DESPAWNED);
        });
        return Response.ok("Recalled quest");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player deadPlayer = event.getPlayer();
        Player killer = deadPlayer.getKiller();

        if (killer == null) return;

        // Check if dead player has active bounty
        List<BountyQuest> bounties = bountyRepository.getBounties(deadPlayer.getUniqueId());
        if (bounties.isEmpty()) return;

        List<BountyQuest> filteredBounties = bounties.stream()
                .filter(q -> q.getQuestStatus() == QuestStatus.NEW || q.getQuestStatus() == QuestStatus.ACTIVE)
                .toList();
        if (filteredBounties.isEmpty()) return;

        for (BountyQuest bounty : filteredBounties) {
            tryCompleteBounty(bounty, killer, deadPlayer);
        }
    }

    private void tryCompleteBounty(BountyQuest bounty, Player killer, Player deadPlayer) {
        if (bounty == null) return;

        if (bounty.getQuestStatus() != QuestStatus.ACTIVE) return;

        List<PlayerQuest> killerQuests = questRepository.getPlayerQuestsByPlayer(killer);
        if (killerQuests.isEmpty()) return;

        boolean hasQuest = killerQuests.stream().anyMatch(aq -> aq.getQuest().getQuestId().equals(bounty.getQuestId()));

        if (!hasQuest) return;

        setPlayerRespawnJail(bounty.getStartTown(), deadPlayer);

        Bukkit.getScheduler().runTaskLater(CobaltKingdoms.getInstance(), () -> {
            bounty.tryComplete(killer, killer.getLocation(), null);

            ItemStack bountyCoin = BountyQuestUtil.getBountyItem(deadPlayer);
            if (bountyCoin != null) {
                killer.give(bountyCoin);
            }

            questRepository.updateStatus(bounty.getQuestId(), QuestStatus.COMPLETED);

            // Increment status
            BountyPlayerStatus killerBountyStatus = bountyRepository.getPlayerBountyStatus(killer);
            killerBountyStatus.incrementCompleted();
            bountyRepository.insertPlayerBountyStatus(killerBountyStatus);

            BountyPlayerStatus targetBountyStatus = bountyRepository.getPlayerBountyStatus(deadPlayer);
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

    private void setPlayerRespawnJail(Town town, Player player) {
        if (town == null) return;

        List<TownJail> jails = TownJailManager.getInstance().getJails(town);
        if (jails.isEmpty()) return;

        TownJail jail = jails.get(random.nextInt(jails.size()));
        if (jail == null) return;

        player.setRespawnLocation(jail.getLocation(), true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        List<BountyQuest> bounties = bountyRepository.getBounties(player.getUniqueId());
        if (bounties.isEmpty()) return;

        List<BountyQuest> filteredBounties = bounties.stream()
                .filter(q -> q.getQuestStatus() == QuestStatus.NEW || q.getQuestStatus() == QuestStatus.ACTIVE)
                .toList();
        if (filteredBounties.isEmpty()) return;

        BountyQuest bounty = filteredBounties.getFirst();
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
        BountyPlayerStatus playerBountyStatus = bountyRepository.getPlayerBountyStatus(sender);

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
