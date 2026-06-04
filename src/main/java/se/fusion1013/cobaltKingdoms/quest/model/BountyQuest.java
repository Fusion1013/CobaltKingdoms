package se.fusion1013.cobaltKingdoms.quest.model;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.HexUtils;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.quest.repository.IBountyRepository;
import se.fusion1013.cobaltKingdoms.quest.repository.IQuestRepository;
import se.fusion1013.cobaltKingdoms.quest.util.BountyQuestUtil;
import se.fusion1013.cobaltKingdoms.quest.util.QuestUtil;
import se.fusion1013.cobaltKingdoms.town.model.Town;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static se.fusion1013.cobaltKingdoms.quest.util.QuestUtil.formatMaterialName;

public class BountyQuest extends AbstractQuest {

    private static final IBountyRepository bountyRepository = DataManager.getInstance().getDao(IBountyRepository.class);

    private Long id;
    private UUID ownerPlayerId;
    private String ownerPlayerName;
    private UUID targetPlayerId;
    private String targetPlayerName;
    private String reason;
    private ItemStack reward;

    public BountyQuest() {
        super(QuestType.BOUNTY);
    }

    public BountyQuest(Long questId) {
        super(questId, QuestType.BOUNTY);
    }

    @Override
    public boolean tryComplete(@NotNull Player player, @NotNull Location location, Long locationId) {
        return false;
    }

    @Override
    public void start(@NotNull Player player, @NotNull Location location) {
        LocaleManager.getInstance().broadcastMessage(CobaltKingdoms.getInstance(), "kingdoms.quests.bounty.accept", StringPlaceholders.builder()
                .addPlaceholder("player", player.getDisplayName())
                .addPlaceholder("target", targetPlayerName)
                .build());
    }

    @Override
    public void fail(Player player, QuestFailReason reason) {
        LocaleManager.getInstance().broadcastMessage(CobaltKingdoms.getInstance(), "kingdoms.quests.bounty.fail", StringPlaceholders.builder()
                .addPlaceholder("player", player.getDisplayName())
                .addPlaceholder("target", targetPlayerName)
                .build());

        BountyPlayerStatus killerBountyStatus = bountyRepository.getPlayerBountyStatus(player);
        killerBountyStatus.incrementFailed();
        bountyRepository.insertPlayerBountyStatus(killerBountyStatus);

        BountyPlayerStatus targetBountyStatus = bountyRepository.getPlayerBountyStatus(targetPlayerId, targetPlayerName);
        targetBountyStatus.incrementEvaded();
        bountyRepository.insertPlayerBountyStatus(targetBountyStatus);

        if (reason != QuestFailReason.DEATH) return;

        // Give the target a bounty coin
        ItemStack bountyCoin = BountyQuestUtil.getBountyItem(player);
        if (bountyCoin == null) return;

        Player targetPlayer = Bukkit.getPlayer(targetPlayerId);
        if (targetPlayer == null || !targetPlayer.isOnline()) return;

        targetPlayer.give(bountyCoin);
    }

    @Override
    public ItemStack getButtonItem() {
        if (!isValid()) return new ItemStack(Material.BARRIER);

        ItemStack item = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile targetProfile = Bukkit.createProfile(targetPlayerId);
        meta.setPlayerProfile(targetProfile);

        meta.setDisplayName(HexUtils.colorify(getTitle()));

        List<String> lore = new ArrayList<>();
        lore.add("&zOffered by: &7" + ownerPlayerName);
        lore.add("&zTarget: &7" + targetPlayerName);
        lore.add("&zTime Limit: &7" + QuestUtil.formatDuration(getDuration()));

        lore.add("");
        lore.addAll(QuestUtil.wrapText(reason, 30).stream().map(k -> "&7" + k).toList());
        lore.add("");

        List<ItemStack> rewards = new ArrayList<>();
        ItemStack bountyCoin = BountyQuestUtil.getBountyItem(targetPlayerId, targetPlayerName);
        if (bountyCoin != null) rewards.add(bountyCoin);
        if (reward != null && reward.getItemMeta() != null) rewards.add(reward);

        if (!rewards.isEmpty()) {
            lore.add("&zReward:");
            for (ItemStack rewardItem : rewards) {
                String name = rewardItem.getItemMeta().hasDisplayName() ? rewardItem.getItemMeta().getDisplayName() : formatMaterialName(rewardItem.getType().name());
                lore.add("&7- " + HexUtils.stripColorCodes(name) + " &7[&z" + rewardItem.getAmount() + "&7]");
            }
        } else {
            lore.add("&zReward: &7No reward");
        }

        lore.replaceAll(HexUtils::colorify);

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    @Override
    public String getTitle() {
        if (!isValid()) return "Something went wrong";
        return QuestUtil.formatTitle("Bounty for " + targetPlayerName, getQuestType().symbol);
    }

    @Override
    public Response canClaim(Player player) {
        if (player.getUniqueId().equals(targetPlayerId))
            return Response.error("Cannot claim a bounty with you as the target");
        if (player.getUniqueId().equals(ownerPlayerId)) return Response.error("Cannot claim a bounty you created");
        if (getQuestStatus() != QuestStatus.NEW && getQuestStatus() != QuestStatus.ACTIVE)
            return Response.error("Quest is not active");
        return Response.ok("Claimed bounty");
    }

    @Override
    public ItemStack getInstructionsItem() {
        return new ItemStack(Material.WRITTEN_BOOK);
    }

    @Override
    public int getDuration() {
        return 1000 * 60 * 60 * 8;
    }

    @Override
    public int getXpValue() {
        return 0;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean validateQuest(Player player) {
        return player.getUniqueId() != targetPlayerId;
    }

    @Override
    public boolean shouldShowInMenu(Town town, Player player) {
        if (getQuestStatus() == QuestStatus.FAILED || getQuestStatus() == QuestStatus.DESPAWNED || getQuestStatus() == QuestStatus.COMPLETED)
            return false;
        if (player == null) return true;

        // Check if player already has this quest active
        List<PlayerQuest> playerQuestsByPlayer = DataManager.getInstance().getDao(IQuestRepository.class).getPlayerQuestsByPlayer(player);
        if (!playerQuestsByPlayer.isEmpty()) {
            for (PlayerQuest playerQuest : playerQuestsByPlayer) {
                if (Objects.equals(playerQuest.getId(), getQuestId())) return false;
            }
        }

        return !player.getUniqueId().equals(targetPlayerId);
    }

    public void setTarget(PlayerProfile player) {
        this.targetPlayerName = player.getName();
        this.targetPlayerId = player.getId();
    }

    public void setOwner(Player player) {
        this.ownerPlayerName = player.getName();
        this.ownerPlayerId = player.getUniqueId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getOwnerPlayerId() {
        return ownerPlayerId;
    }

    public void setOwnerPlayerId(UUID ownerPlayerId) {
        this.ownerPlayerId = ownerPlayerId;
    }

    public String getOwnerPlayerName() {
        return ownerPlayerName;
    }

    public void setOwnerPlayerName(String ownerPlayerName) {
        this.ownerPlayerName = ownerPlayerName;
    }

    public UUID getTargetPlayerId() {
        return targetPlayerId;
    }

    public void setTargetPlayerId(UUID targetPlayerId) {
        this.targetPlayerId = targetPlayerId;
    }

    public String getTargetPlayerName() {
        return targetPlayerName;
    }

    public void setTargetPlayerName(String targetPlayerName) {
        this.targetPlayerName = targetPlayerName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ItemStack getReward() {
        return reward;
    }

    public void setReward(ItemStack reward) {
        this.reward = reward;
    }
}
