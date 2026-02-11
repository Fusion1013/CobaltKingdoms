package se.fusion1013.cobaltKingdoms.player.character;

import org.json.simple.JSONObject;

import java.util.UUID;

public class CharacterProfile implements ICharacterProfile {

    private final UUID profileId;
    private final UUID playerId;
    private final String characterId;

    private String characterName;
    private String pronouns;
    private int age;
    private double height;
    private String otherDescription;

    public CharacterProfile(UUID playerId, String characterId) {
        this.profileId = UUID.randomUUID();
        this.playerId = playerId;
        this.characterId = characterId;
    }

    public CharacterProfile(UUID profileId, UUID playerId, String characterId, JSONObject data) {
        this.profileId = profileId;
        this.playerId = playerId;
        this.characterId = characterId;
        loadJsonData(data);
    }

    private void loadJsonData(JSONObject json) {
        if (json.containsKey("character_name")) characterName = (String) json.get("character_name");
        if (json.containsKey("pronouns")) pronouns = (String) json.get("pronouns");
        if (json.containsKey("age")) age = Math.toIntExact((long) json.get("age"));
        if (json.containsKey("height")) height = (double) json.get("height");
        if (json.containsKey("description")) otherDescription = (String) json.get("description");
    }

    public JSONObject getJsonData() {
        JSONObject json = new JSONObject();
        json.put("character_name", characterName);
        json.put("pronouns", pronouns);
        json.put("age", age);
        json.put("height", height);
        json.put("description", otherDescription);
        return json;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getCharacterId() {
        return characterId;
    }

    @Override
    public String getCharacterName() {
        return characterName;
    }

    @Override
    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    @Override
    public String getDescription() {
        return otherDescription;
    }

    @Override
    public void setDescription(String otherDescription) {
        this.otherDescription = otherDescription;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String getPronouns() {
        return pronouns;
    }

    @Override
    public void setPronouns(String pronouns) {
        this.pronouns = pronouns;
    }
}
