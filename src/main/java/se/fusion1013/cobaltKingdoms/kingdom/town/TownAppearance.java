package se.fusion1013.cobaltKingdoms.kingdom.town;

public class TownAppearance {

    private Long id;
    private String skin;
    private String texture;
    private String titleGreeting;
    private String chatGreeting;

    public TownAppearance() {
    }

    public TownAppearance(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public String getTitleGreeting() {
        return titleGreeting;
    }

    public void setTitleGreeting(String titleGreeting) {
        this.titleGreeting = titleGreeting;
    }

    public String getChatGreeting() {
        return chatGreeting;
    }

    public void setChatGreeting(String chatGreeting) {
        this.chatGreeting = chatGreeting;
    }
}
