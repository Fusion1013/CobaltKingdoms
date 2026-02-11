package se.fusion1013.cobaltKingdoms.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.player.character.CharacterProfileManager;
import se.fusion1013.cobaltKingdoms.player.character.ICharacterProfile;

public class CharacterCommand {

    private static final CharacterProfileManager MANAGER = CharacterProfileManager.getInstance();

    public static void register() {
        new CommandAPICommand("character")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "character"))
                .withSubcommand(createCommand())
                .withSubcommand(setCommand())
                .withSubcommand(infoCommand())
                .withSubcommand(setActiveCommand())
                .register();
    }

    private static CommandAPICommand setActiveCommand() {
        return new CommandAPICommand("active")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "character.active"))
                .withOptionalArguments(new StringArgument("id").replaceSuggestions(ArgumentSuggestions.strings(k -> CharacterProfileManager.getInstance().getCharacterIds((Player) k.sender()).toArray(new String[0]))))
                .executesPlayer(CharacterCommand::setActiveCharacter);
    }

    private static void setActiveCharacter(Player player, CommandArguments commandArguments) {
        String characterId = commandArguments.get("id") != null ? (String) commandArguments.get("id") : "";
        if (characterId.isEmpty()) {
            ICharacterProfile activeCharacter = CharacterProfileManager.getInstance().getActiveCharacter(player);
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.character.active", StringPlaceholders.builder().addPlaceholder("character", activeCharacter == null ? "empty" : activeCharacter.getCharacterId()).build());
        } else {
            CharacterProfileManager.getInstance().setActiveCharacter(player, characterId);
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.character.set_active", StringPlaceholders.builder().addPlaceholder("character", characterId).build());
        }
    }

    private static CommandAPICommand infoCommand() {
        return new CommandAPICommand("info")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "character.info"))
                .withOptionalArguments(new EntitySelectorArgument.OnePlayer("target"))
                .executesPlayer(CharacterCommand::displayCharacterInfo);
    }

    private static void displayCharacterInfo(Player player, CommandArguments commandArguments) {
        Player target = commandArguments.get("target") != null ? (Player) commandArguments.get("target") : player;
        MANAGER.sendCharacterInfo(player, target);
    }

    private static CommandAPICommand setCommand() {
        return new CommandAPICommand("set")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "character.set"))
                .withSubcommand(setNameCommand())
                .withSubcommand(setAgeCommand())
                .withSubcommand(setHeightCommand())
                .withSubcommand(setDescriptionCommand())
                .withSubcommand(setPronounsCommand());
    }

    private static CommandAPICommand setDescriptionCommand() {
        return new CommandAPICommand("description")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "character.set.description"))
                .withOptionalArguments(new GreedyStringArgument("description"))
                .executesPlayer((sender, commandArguments) -> {
                    String description = commandArguments.get("description") != null ? (String) commandArguments.get("description") : "";
                    MANAGER.setDescription(sender, description);

                    if (description.isEmpty()) {
                        LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.character.unset_description");
                    } else {
                        LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.character.set_description",
                                StringPlaceholders.builder()
                                        .addPlaceholder("description", description)
                                        .build()
                        );
                    }

                });
    }

    private static CommandAPICommand setHeightCommand() {
        return new CommandAPICommand("height")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "character.set.height"))
                .withOptionalArguments(new DoubleArgument("height"))
                .executesPlayer((sender, args) -> {
                    double height = args.get("height") != null ? (double) args.get("height") : 0;
                    MANAGER.setHeight(sender, height);

                    if (height == 0) {
                        LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.character.unset_height");
                    } else {
                        LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.character.set_height",
                                StringPlaceholders.builder()
                                        .addPlaceholder("height", height)
                                        .build()
                        );
                    }
                });
    }

    private static CommandAPICommand setAgeCommand() {
        return new CommandAPICommand("age")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "character.set.age"))
                .withOptionalArguments(new IntegerArgument("age"))
                .executesPlayer((sender, args) -> {
                    int age = args.get("age") != null ? (int) args.get("age") : 0;
                    MANAGER.setAge(sender, age);

                    if (age == 0) {
                        LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.character.unset_age");
                    } else {
                        LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.character.set_age",
                                StringPlaceholders.builder()
                                        .addPlaceholder("age", age)
                                        .build()
                        );
                    }
                });
    }

    private static CommandAPICommand setPronounsCommand() {
        return new CommandAPICommand("pronouns")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "character.set.pronouns"))
                .withOptionalArguments(new GreedyStringArgument("pronouns"))
                .executesPlayer((sender, args) -> {
                    String pronouns = args.get("pronouns") != null ? (String) args.get("pronouns") : "";
                    MANAGER.setPronouns(sender, pronouns);

                    if (pronouns.isEmpty()) {
                        LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.character.unset_pronouns");
                    } else {
                        LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.character.set_pronouns",
                                StringPlaceholders.builder()
                                        .addPlaceholder("pronouns", pronouns)
                                        .build()
                        );
                    }
                });
    }

    private static CommandAPICommand setNameCommand() {
        return new CommandAPICommand("name")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "character.set.name"))
                .withOptionalArguments(new GreedyStringArgument("name"))
                .executesPlayer((sender, args) -> {
                    String name = args.get("name") != null ? (String) args.get("name") : "";
                    MANAGER.setName(sender, name);

                    if (name.isEmpty()) {
                        LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.character.unset_name");
                    } else {
                        LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), sender, "kingdoms.commands.character.set_name",
                                StringPlaceholders.builder()
                                        .addPlaceholder("name", name)
                                        .build()
                        );
                    }
                });
    }

    private static CommandAPICommand createCommand() {
        return new CommandAPICommand("create")
                .withPermission(CommandUtil.getPermissionString(CobaltKingdoms.getInstance(), "character.create"))
                .withArguments(new StringArgument("character_id"))
                .executesPlayer(CharacterCommand::createNewCharacter);
    }

    private static void createNewCharacter(Player player, CommandArguments commandArguments) {
        String characterId = (String) commandArguments.get("character_id");
        boolean created = MANAGER.createCharacter(player, characterId);
        if (created) {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.character.create", StringPlaceholders.builder().addPlaceholder("character", characterId).build());
        } else {
            LocaleManager.getInstance().sendMessage(CobaltKingdoms.getInstance(), player, "kingdoms.commands.character.create.fail");
        }
    }

}
