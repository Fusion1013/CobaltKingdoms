package se.fusion1013.cobaltKingdoms.player;

public enum PlayerStatus {

    IN_CHARACTER("IC", "In Character"), OUT_OF_CHARACTER("OOC", "Out of Character"), AFK("AFK", "AFK");

    public final String prefix;
    public final String title;

    PlayerStatus(String prefix, String title) {
        this.prefix = prefix;
        this.title = title;
    }

}
