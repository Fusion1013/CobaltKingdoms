package se.fusion1013.cobaltKingdoms.database.quest.bounty;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.database.system.IDao;
import se.fusion1013.cobaltKingdoms.quest.bounty.BountyPlayerStatusEntity;
import se.fusion1013.cobaltKingdoms.quest.bounty.BountyQuestEntity;

import java.util.List;
import java.util.UUID;

public interface IBountyRepository extends IDao {

    @Override
    default String getId() {
        return "bounty";
    }

    BountyQuestEntity getBountyByQuest(Long questId);

    void insertQuest(BountyQuestEntity quest);

    List<BountyQuestEntity> getBounties(Player owner, PlayerProfile target);

    List<BountyQuestEntity> getBounties(UUID targetId);

    void insertPlayerBountyStatus(BountyPlayerStatusEntity bountyPlayerStatusEntity);

    BountyPlayerStatusEntity getPlayerBountyStatus(Player player);

    BountyPlayerStatusEntity getPlayerBountyStatus(UUID playerId, String playerName);

}
