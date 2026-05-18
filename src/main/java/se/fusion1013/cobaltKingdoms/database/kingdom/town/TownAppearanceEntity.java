package se.fusion1013.cobaltKingdoms.database.kingdom.town;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "town_appearance")
public class TownAppearanceEntity {

    @DatabaseField(generatedId = true, columnName = "id")
    private Long id;

    @DatabaseField(columnName = "skin")
    private String skin;

    @DatabaseField(columnName = "texture")
    private String texture;

    @DatabaseField(columnName = "title_greeting")
    private String titleGreeting;

    @DatabaseField(columnName = "chat_greeting")
    private String chatGreeting;

    public TownAppearanceEntity() {
    }

    public void setId(Long id) {
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
