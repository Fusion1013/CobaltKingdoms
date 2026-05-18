package se.fusion1013.cobaltKingdoms.database.quest.bounty;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.database.system.IDao;
import se.fusion1013.cobaltKingdoms.quest.bounty.BountyPlayerStatus;
import se.fusion1013.cobaltKingdoms.quest.bounty.BountyQuest;

import java.util.List;
import java.util.UUID;

public interface IBountyRepository extends IDao {

    @Override
    default String getId() {
        return "bounty";
    }

    BountyQuest getBountyByQuest(Long questId);

    void insertQuest(BountyQuest quest);

    List<BountyQuest> getBounties(Player owner, PlayerProfile target);

    List<BountyQuest> getBounties(UUID targetId);

    void insertPlayerBountyStatus(BountyPlayerStatus bountyPlayerStatus);

    BountyPlayerStatus getPlayerBountyStatus(Player player);

    BountyPlayerStatus getPlayerBountyStatus(UUID playerId, String playerName);

    List<BountyQuest> getQuests();
}
