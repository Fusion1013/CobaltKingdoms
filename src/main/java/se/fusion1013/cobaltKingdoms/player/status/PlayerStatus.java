package se.fusion1013.cobaltKingdoms.player.status;

public enum PlayerStatus {

    IN_CHARACTER("IC", "In Character", "&a"),
    OPEN_FOR_IN_CHARACTER("OIC", "Open for In Character", "&e"),
    OUT_OF_CHARACTER("OOC", "Out of Character", "&c"),
    AFK("AFK", "AFK", "&8");

    public final String prefix;
    public final String title;
    public final String colorPrefix;

    PlayerStatus(String prefix, String title, String colorPrefix) {
        this.prefix = prefix;
        this.title = title;
        this.colorPrefix = colorPrefix;
    }

}
