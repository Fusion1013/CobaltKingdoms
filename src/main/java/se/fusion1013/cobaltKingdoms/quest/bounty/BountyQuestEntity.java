package se.fusion1013.cobaltKingdoms.quest.bounty;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
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
import se.fusion1013.cobaltKingdoms.database.ItemStackPersister;
import se.fusion1013.cobaltKingdoms.database.quest.IQuestRepository;
import se.fusion1013.cobaltKingdoms.database.quest.bounty.IBountyRepository;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownEntity;
import se.fusion1013.cobaltKingdoms.quest.*;

import java.util.*;

import static se.fusion1013.cobaltKingdoms.quest.QuestUtil.formatMaterialName;

@DatabaseTable(tableName = "quest_bounty")
public class BountyQuestEntity implements IQuestData {

    private static final IBountyRepository bountyRepository = DataManager.getInstance().getDao(IBountyRepository.class);

    @DatabaseField(generatedId = true, columnName = "id")
    private Long id;

    @DatabaseField(columnName = "owner_player_id")
    private UUID ownerPlayerId;

    @DatabaseField(columnName = "owner_player_name")
    private String ownerPlayerName;

    @DatabaseField(columnName = "target_player_id")
    private UUID targetPlayerId;

    @DatabaseField(columnName = "target_player_name")
    private String targetPlayerName;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, columnName = "quest")
    private QuestEntity quest;

    @DatabaseField(columnName = "reason")
    private String reason;

    @DatabaseField(columnName = "reward", persisterClass = ItemStackPersister.class)
    private ItemStack reward;

    @Override
    public boolean tryComplete(Player player, @NotNull Location location, TownEntity clickedTown) {
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
    public ItemStack getInstructionsItem() {
        return new ItemStack(Material.WRITTEN_BOOK);
    }

    @Override
    public boolean validateQuest(Player player) {
        return player.getUniqueId() != targetPlayerId;
    }

    @Override
    public String getTitle() {
        if (!quest.isValid()) return "Something went wrong";
        return QuestUtil.formatTitle("Bounty for " + targetPlayerName, quest.getQuestType().symbol);
    }

    @Override
    public String getSymbol() {
        return quest.getQuestType().symbol;
    }

    @Override
    public ItemStack getButtonItem() {
        if (!quest.isValid()) return new ItemStack(Material.BARRIER);

        ItemStack item = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile targetProfile = Bukkit.createProfile(targetPlayerId);
        meta.setPlayerProfile(targetProfile);

        meta.setDisplayName(getTitle());

        List<String> lore = new ArrayList<>();
        lore.add("&zOffered by: &7" + ownerPlayerName);
        lore.add("&zTarget: &7" + targetPlayerName);
        lore.add("&zTime Limit: &7" + QuestUtil.formatDuration(getDuration()));

        List<ItemStack> rewards = new ArrayList<>();
        ItemStack bountyCoin = BountyQuestUtil.getBountyItem(targetPlayerId, targetPlayerName);
        if (bountyCoin != null) rewards.add(bountyCoin);
        if (reward != null && reward.getItemMeta() != null) rewards.add(reward);

        if (!rewards.isEmpty()) {
            lore.add("&zReward:");
            for (ItemStack rewardItem : rewards) {
                String name = rewardItem.getItemMeta().hasDisplayName() ? rewardItem.getItemMeta().getDisplayName() : formatMaterialName(rewardItem.getType().name());
                lore.add("&7- " + HexUtils.colorify(name) + " &7[&z" + rewardItem.getAmount() + "&7]");
            }
        } else {
            lore.add("&zReward: &7No reward");
        }


        lore.add("");
        lore.add("&7" + reason);

        lore.replaceAll(HexUtils::colorify);

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    @Override
    public int getXpValue() {
        return 0;
    }

    @Override
    public boolean shouldShowInMenu(TownEntity town, Player player) {
        if (quest.getStatus() == QuestStatus.FAILED || quest.getStatus() == QuestStatus.DESPAWNED || quest.getStatus() == QuestStatus.COMPLETED)
            return false;
        if (player == null) return true;

        // Check if player already has this quest active
        Optional<List<ActivePlayerQuestEntity>> activePlayerQuestsByPlayer = DataManager.getInstance().getDao(IQuestRepository.class).getActivePlayerQuestsByPlayer(player);
        if (activePlayerQuestsByPlayer.isPresent()) {
            for (ActivePlayerQuestEntity playerQuest : activePlayerQuestsByPlayer.get()) {
                if (Objects.equals(playerQuest.getId(), quest.getId())) return false;
            }
        }

        return !player.getUniqueId().equals(targetPlayerId);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void fail(Player player, QuestFailReason reason) {
        LocaleManager.getInstance().broadcastMessage(CobaltKingdoms.getInstance(), "kingdoms.quests.bounty.fail", StringPlaceholders.builder()
                .addPlaceholder("player", player.getDisplayName())
                .addPlaceholder("target", targetPlayerName)
                .build());

        BountyPlayerStatusEntity killerBountyStatus = bountyRepository.getPlayerBountyStatus(player);
        killerBountyStatus.incrementFailed();
        bountyRepository.insertPlayerBountyStatus(killerBountyStatus);

        BountyPlayerStatusEntity targetBountyStatus = bountyRepository.getPlayerBountyStatus(targetPlayerId, targetPlayerName);
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
    public Response canClaim(Player player) {
        if (player.getUniqueId().equals(targetPlayerId))
            return Response.error("Cannot claim a bounty with you as the target");
        if (player.getUniqueId().equals(ownerPlayerId)) return Response.error("Cannot claim a bounty you created");
        if (quest.getStatus() != QuestStatus.NEW && quest.getStatus() != QuestStatus.ACTIVE)
            return Response.error("Quest is not active");
        return Response.ok("Claimed bounty");
    }

    @Override
    public int getDuration() {
        return 1000 * 60 * 60 * 8;
    }

    public Long getId() {
        return id;
    }

    public void setTarget(PlayerProfile player) {
        this.targetPlayerName = player.getName();
        this.targetPlayerId = player.getId();
    }

    public void setOwner(Player player) {
        this.ownerPlayerName = player.getName();
        this.ownerPlayerId = player.getUniqueId();
    }

    public UUID getTargetPlayerId() {
        return targetPlayerId;
    }

    public void setTargetPlayerId(UUID targetPlayerId) {
        this.targetPlayerId = targetPlayerId;
    }

    public QuestEntity getQuest() {
        return quest;
    }

    public void setQuest(QuestEntity quest) {
        this.quest = quest;
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
