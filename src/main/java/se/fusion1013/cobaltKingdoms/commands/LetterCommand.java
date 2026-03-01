package se.fusion1013.cobaltKingdoms.commands;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.pigeon.LetterManager;
import se.fusion1013.cobaltKingdoms.player.character.CharacterProfileManager;
import se.fusion1013.cobaltKingdoms.player.character.ICharacterProfile;

public class LetterCommand {

    private static final Argument<?> noSelectorSuggestions = new PlayerProfileArgument("target")
            .replaceSafeSuggestions(SafeSuggestions.suggest(info ->
                    Bukkit.getOnlinePlayers().stream().map(Player::getPlayerProfile).toArray(PlayerProfile[]::new)
            ));

    public static void register() {
        new CommandAPICommand("letter")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "letter"))
                .withSubcommand(createSendCommand())
                .withSubcommand(createReadCommand())
                .register();
    }

    private static CommandAPICommand createReadCommand() {
        return new CommandAPICommand("read")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "letter.read"))
                .executesPlayer(LetterCommand::readAllLetters);
    }

    private static void readAllLetters(Player player, CommandArguments commandArguments) {
        LetterManager.getInstance().trySendAllSavedLetters(player.getUniqueId());
    }

    private static CommandAPICommand createSendCommand() {
        return new CommandAPICommand("send")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "letter.send"))
                .withArguments(new StringArgument("target").replaceSuggestions(ArgumentSuggestions.strings(info -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new))))
                .executesPlayer(LetterCommand::sendLetter);
    }

    private static void sendLetter(Player player, CommandArguments commandArguments) {
        String receiverPlayer = (String) commandArguments.get("target");
        ItemStack mainHandItem = player.getInventory().getItemInMainHand().clone();

        if (player.getName().equalsIgnoreCase(receiverPlayer)) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.letter.send.not_self");
            if (!player.isOp()) return;
        }

        if (mainHandItem.getType() != Material.WRITTEN_BOOK) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.letter.send.not_written_book");
            return;
        }

        BookMeta bookMeta = (BookMeta) mainHandItem.getItemMeta();
        if (!bookMeta.getItemModel().getKey().contains("letter")) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.letter.send.not_letter");
            return;
        }

        ICharacterProfile profile = CharacterProfileManager.getInstance().getActiveCharacter(player);
        String characterName = profile == null ? player.getName() : profile.getCharacterName().isEmpty() ? player.getName() : profile.getCharacterName();
        bookMeta.setAuthor(characterName);
        mainHandItem.setItemMeta(bookMeta);

        player.getInventory().getItemInMainHand().setAmount(mainHandItem.getAmount() - 1);

        LetterManager.getInstance().sendLetter(player, receiverPlayer, mainHandItem, false);
    }

}
