package se.fusion1013.cobaltKingdoms.player;

public enum PlayerStatus {

    IN_CHARACTER("IC"), OUT_OF_CHARACTER("OOC"), AFK("AFK");

    public final String prefix;

    private PlayerStatus(String prefix) {
        this.prefix = prefix;
    }

}
