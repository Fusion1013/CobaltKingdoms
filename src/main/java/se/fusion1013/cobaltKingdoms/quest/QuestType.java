package se.fusion1013.cobaltKingdoms.quest;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Parrot;

public enum QuestType {

    COMBAT("⚔", NamedTextColor.RED, Parrot.Variant.RED),
    DELIVER("\uD83D\uDCE6", NamedTextColor.DARK_GREEN, Parrot.Variant.GREEN),
    ARTIFACT_HUNT("\uD83C\uDF3F", NamedTextColor.BLUE, Parrot.Variant.BLUE),
    BOUNTY("⚔", NamedTextColor.DARK_RED, Parrot.Variant.RED);

    public final String symbol;
    public final NamedTextColor textColor;
    public final Parrot.Variant parrotVariant;

    QuestType(String symbol, NamedTextColor textColor, Parrot.Variant parrotVariant) {
        this.symbol = symbol;
        this.textColor = textColor;
        this.parrotVariant = parrotVariant;
    }

}
