package se.fusion1013.cobaltKingdoms.quest.model;

import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.town.model.Town;

public class HeadlessQuest extends AbstractQuest {

    public HeadlessQuest(QuestType questType) {
        super(questType);
    }

    @Override
    public boolean tryComplete(@NotNull Player player, @NotNull Location location, Long locationId) {
        throw new NotImplementedException();
    }

    @Override
    public void start(@NotNull Player player, @NotNull Location location) {
        throw new NotImplementedException();
    }

    @Override
    public void fail(@NotNull Player player, QuestFailReason reason) {
        throw new NotImplementedException();
    }

    @Override
    public ItemStack getButtonItem() {
        throw new NotImplementedException();
    }

    @Override
    public String getTitle() {
        throw new NotImplementedException();
    }

    @Override
    public Response canClaim(@NotNull Player player) {
        throw new NotImplementedException();
    }

    @Override
    public ItemStack getInstructionsItem() {
        throw new NotImplementedException();
    }

    @Override
    public int getDuration() {
        throw new NotImplementedException();
    }

    @Override
    public int getXpValue() {
        throw new NotImplementedException();
    }

    @Override
    public boolean isValid() {
        throw new NotImplementedException();
    }

    @Override
    public boolean validateQuest(@NotNull Player player) {
        throw new NotImplementedException();
    }

    @Override
    public boolean shouldShowInMenu(Town startTown, Player player) {
        throw new NotImplementedException();
    }
}
