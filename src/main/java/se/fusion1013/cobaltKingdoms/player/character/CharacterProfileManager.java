package se.fusion1013.cobaltKingdoms.player.character;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.database.player.ICharacterProfileDao;

import java.util.*;

public class CharacterProfileManager extends Manager<CobaltKingdoms> implements Listener {

    private static Map<UUID, List<ICharacterProfile>> CHARACTER_PROFILES = new HashMap<>();
    private static Map<UUID, UUID> ACTIVE_PROFILES = new HashMap<>();
    private static final ICharacterProfileDao PROFILE_DAO = DataManager.getInstance().getDao(ICharacterProfileDao.class);

    public CharacterProfileManager(CobaltKingdoms plugin) {
        super(plugin);
    }

    public ICharacterProfile getActiveCharacter(Player player) {
        List<ICharacterProfile> profiles = CHARACTER_PROFILES.get(player.getUniqueId());
        UUID activeCharacter = ACTIVE_PROFILES.get(player.getUniqueId());
        if (activeCharacter == null || profiles.isEmpty()) return null;

        for (ICharacterProfile profile : profiles) {
            if (profile.getProfileId().equals(activeCharacter)) return profile;
        }
        return null;
    }

    public boolean createCharacter(Player player, String characterId) {
        if (getActiveCharacter(player) != null && !player.hasPermission("kingdoms.profiles.allow_multiple"))
            return false;

        List<ICharacterProfile> characterProfiles = CHARACTER_PROFILES.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>());
        CharacterProfile profile = new CharacterProfile(player.getUniqueId(), characterId);
        characterProfiles.add(profile);

        if (characterProfiles.size() <= 1) {
            setActiveCharacter(player, characterId);
        }

        PROFILE_DAO.insertCharacterProfile(profile);

        return true;
    }

    public void setActiveCharacter(Player player, String characterId) {
        List<ICharacterProfile> profiles = CHARACTER_PROFILES.get(player.getUniqueId());
        if (profiles.isEmpty()) return;
        for (ICharacterProfile profile : profiles) {
            if (!profile.getCharacterId().equalsIgnoreCase(characterId)) continue;
            ACTIVE_PROFILES.put(player.getUniqueId(), profile.getProfileId());
            PROFILE_DAO.insertActiveCharacter(player.getUniqueId(), profile.getProfileId());
        }
    }

    public void setName(Player sender, String name) {
        ICharacterProfile profile = getActiveCharacter(sender);
        profile.setCharacterName(name);
        persistCharacterProfile(profile);
    }

    public void setPronouns(Player sender, String pronouns) {
        ICharacterProfile profile = getActiveCharacter(sender);
        profile.setPronouns(pronouns);
        persistCharacterProfile(profile);
    }

    public void setAge(Player sender, int age) {
        ICharacterProfile profile = getActiveCharacter(sender);
        profile.setAge(age);
        persistCharacterProfile(profile);
    }

    public void setHeight(Player sender, double height) {
        ICharacterProfile profile = getActiveCharacter(sender);
        profile.setHeight(height);
        persistCharacterProfile(profile);
    }

    public void setDescription(Player sender, String description) {
        ICharacterProfile profile = getActiveCharacter(sender);
        profile.setDescription(description);
        persistCharacterProfile(profile);
    }

    private void persistCharacterProfile(ICharacterProfile profile) {
        ICharacterProfileDao characterProfileDao = DataManager.getInstance().getDao(ICharacterProfileDao.class);
        characterProfileDao.insertCharacterProfile(profile);
    }

    public void sendCharacterInfo(Player player, Player target) {
        ICharacterProfile profile = getActiveCharacter(target);
        if (profile == null) return;

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("player", target.getName())
                .addPlaceholder("name", formatInfo(profile.getCharacterName()))
                .addPlaceholder("pronouns", formatInfo(profile.getPronouns()))
                .addPlaceholder("age", formatInfo(profile.getAge()))
                .addPlaceholder("height", formatInfo(profile.getHeight()))
                .addPlaceholder("description", formatInfo(profile.getDescription()))
                .build();

        LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.character.info.header", placeholders);
        LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.character.info.name", placeholders);
        LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.character.info.pronouns", placeholders);
        LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.character.info.age", placeholders);
        LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.character.info.height", placeholders);
        LocaleManager.getInstance().sendMessage("", player, "kingdoms.commands.character.info.description", placeholders);
    }

    private static String formatInfo(String value) {
        if (value == null) return "???";
        return value.isEmpty() ? "???" : value;
    }

    private static String formatInfo(int value) {
        if (value <= 0) return "???";
        return "" + value;
    }

    private static String formatInfo(double value) {
        if (value <= 0) return "???";
        return "" + value;
    }

    private static String formatInfo(Object value) {
        if (value == null) return "???";
        return value.toString();
    }

    @EventHandler
    private void playerRightClickEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity rightClicked = event.getRightClicked();

        // If player is shift-right clicking with an empty hand
        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) return;
        if (!player.isSneaking()) return;
        if (!player.getInventory().getItemInMainHand().isEmpty()) return;

        if (rightClicked instanceof Player otherPlayer) {
            ICharacterProfile profile = getActiveCharacter(otherPlayer);
            if (profile == null) return;
            sendCharacterInfo(player, otherPlayer);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        }
    }

    public List<String> getCharacterIds(Player player) {
        List<ICharacterProfile> profiles = CHARACTER_PROFILES.get(player.getUniqueId());
        return profiles.stream().map(ICharacterProfile::getCharacterId).toList();
    }

    @Override
    public void reload() {
        CHARACTER_PROFILES = PROFILE_DAO.getCharacterProfiles();
        ACTIVE_PROFILES = PROFILE_DAO.getActiveCharacters();

        Bukkit.getPluginManager().registerEvents(this, CobaltKingdoms.getInstance());
    }

    @Override
    public void disable() {

    }

    private static CharacterProfileManager INSTANCE;

    public static CharacterProfileManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CharacterProfileManager(CobaltKingdoms.getInstance());
        }
        return INSTANCE;
    }
}
