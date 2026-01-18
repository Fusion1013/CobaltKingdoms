package se.fusion1013.cobaltKingdoms.player;

public enum PlayerStatus {

    IN_CHARACTER("IC", "In Character", "&a"), OUT_OF_CHARACTER("OOC", "Out of Character", "&e"), AFK("AFK", "AFK", "&c");

    public final String prefix;
    public final String title;
    public final String colorPrefix;

    PlayerStatus(String prefix, String title, String colorPrefix) {
        this.prefix = prefix;
        this.title = title;
        this.colorPrefix = colorPrefix;
    }

}
