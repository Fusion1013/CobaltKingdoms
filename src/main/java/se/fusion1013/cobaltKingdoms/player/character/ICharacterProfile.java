package se.fusion1013.cobaltKingdoms.player.character;

import org.json.simple.JSONObject;

import java.util.UUID;

public interface ICharacterProfile {

    UUID getProfileId();

    UUID getPlayerId();

    String getCharacterId();

    JSONObject getJsonData();

    // ##%%##%%## OPTIONAL PARAMS ##%%##%%## //

    void setCharacterName(String characterName);

    String getCharacterName();

    void setPronouns(String pronouns);

    String getPronouns();

    void setAge(int age);

    int getAge();

    void setHeight(double height);

    double getHeight();

    void setDescription(String description);

    String getDescription();
}
