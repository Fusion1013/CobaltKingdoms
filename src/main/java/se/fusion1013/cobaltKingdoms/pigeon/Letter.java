package se.fusion1013.cobaltKingdoms.pigeon;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public record Letter(UUID letterId, String sender, String receiver, ItemStack data) implements ILetter {
}
