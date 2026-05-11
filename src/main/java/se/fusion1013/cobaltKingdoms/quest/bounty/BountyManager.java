package se.fusion1013.cobaltKingdoms.quest.bounty;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.database.quest.IQuestRepository;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownEntity;
import se.fusion1013.cobaltKingdoms.kingdom.town.TownManager;
import se.fusion1013.cobaltKingdoms.quest.QuestEntity;
import se.fusion1013.cobaltKingdoms.quest.QuestStatus;
import se.fusion1013.cobaltKingdoms.quest.QuestType;

import java.util.Date;
import java.util.List;

public class BountyManager extends Manager<CobaltKingdoms> {

    private static final DataManager dataManager = DataManager.getInstance();
    private static final IQuestRepository questRepository = dataManager.getDao(IQuestRepository.class);

    public Response create(Player owner, PlayerProfile target, String reason, ItemStack reward) {
        List<BountyQuestEntity> oldBounty = questRepository.getBounty(owner, target)
                .stream().filter(bq -> {
                    QuestEntity quest = bq.getQuest();
                    if (quest.getStatus() == QuestStatus.NEW) return true;
                    return quest.getStatus() == QuestStatus.ACTIVE;
                }).toList();
        if (!oldBounty.isEmpty()) return Response.error("You already have an active bounty for that player");

        TownEntity town = TownManager.getInstance().getPlayerTown(owner);
        if (town == null) return Response.error("You need to be a part of a town to create a bounty");

        QuestEntity quest = new QuestEntity(QuestType.Bounty, new Date(), 0, 0, 0, 0, QuestStatus.NEW, town, town);
        quest.setCanDespawn(false);

        BountyQuestEntity bounty = new BountyQuestEntity();
        bounty.setTarget(target);
        bounty.setOwner(owner);
        bounty.setQuest(quest);
        bounty.setReason(reason);
        bounty.setReward(reward);

        questRepository.insertQuest(bounty);
        return Response.ok("Created new bounty");
    }

    public Response recall(Player owner, PlayerProfile target) {
        List<BountyQuestEntity> oldBounty = questRepository.getBounty(owner, target)
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

    public BountyManager(CobaltKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void reload() {

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
}
