package se.fusion1013.cobaltKingdoms.kingdom.model;

public enum KingdomPermission {

    INVITE("kingdom.invite"),
    KICK("kingdom.kick"),
    MODIFY("kingdom.modify"),
    DELETE("kingdom.delete");

    private final String permissionString;

    KingdomPermission(String permissionString) {
        this.permissionString = permissionString;
    }

    public String key() {
        return permissionString;
    }

}
