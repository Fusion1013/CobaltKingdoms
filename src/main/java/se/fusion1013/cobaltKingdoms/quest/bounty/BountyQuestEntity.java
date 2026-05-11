package se.fusion1013.cobaltKingdoms.quest.bounty;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.util.HexUtils;
import se.fusion1013.cobaltKingdoms.database.ItemStackPersister;
import se.fusion1013.cobaltKingdoms.database.quest.IQuestRepository;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownEntity;
import se.fusion1013.cobaltKingdoms.quest.ActivePlayerQuestEntity;
import se.fusion1013.cobaltKingdoms.quest.IQuestData;
import se.fusion1013.cobaltKingdoms.quest.QuestEntity;
import se.fusion1013.cobaltKingdoms.quest.QuestStatus;

import java.util.*;

@DatabaseTable(tableName = "quest_bounty")
public class BountyQuestEntity implements IQuestData {

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
    public Component getTitle() {
        if (!quest.isValid()) return Component.text("Something went wrong");
        Component typeSymbol = Component.text(" [" + quest.getQuestType().symbol + "] ").color(quest.getQuestType().textColor);
        Component titleText = Component.text("Bounty for " + targetPlayerName).color(NamedTextColor.GRAY);
        return typeSymbol.append(titleText).append(typeSymbol).decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public ItemStack getButtonItem() {
        if (!quest.isValid()) return new ItemStack(Material.BARRIER);

        ItemStack item = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile targetProfile = Bukkit.createProfile(targetPlayerId);
        meta.setPlayerProfile(targetProfile);

        meta.displayName(getTitle());

        List<String> lore = new ArrayList<>();
        lore.add("&zOffered by: &7" + ownerPlayerName);
        lore.add("&zTarget: &7" + targetPlayerName);

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
