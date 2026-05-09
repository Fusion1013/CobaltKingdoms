/**
 * Source: https://github.com/kangarko/CowCannon/blob/master/src/main/java/org/mineacademy/cowcannon/gui/Menu.java
 */

package se.fusion1013.cobaltKingdoms.quest.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

import java.util.ArrayList;
import java.util.List;

import static se.fusion1013.cobaltKingdoms.quest.QuestMenuListener.IN_MENU_METADATA;

public class Menu {

    private final List<Button> buttons = new ArrayList<>();

    private int size = 9 * 3;
    private String title = "Custom Menu";

    private final Menu parent;
    private boolean extraButtonsRegistered = false;

    public Menu() {
        this(null);
    }

    public Menu(Menu parent) {
        this.parent = parent;
    }

    public final List<Button> getButtons() {
        return buttons;
    }

    protected final void addButton(Button button) {
        this.buttons.add(button);
    }

    protected final void setSize(int size) {
        this.size = size;
    }

    protected final void setTitle(String title) {
        this.title = title;
    }

    public final void displayTo(Player player) {
        final Inventory inventory = Bukkit.createInventory(player, this.size, Component.text(this.title));

        for (final Button button : this.buttons)
            inventory.setItem(button.getSlot(), button.getItem());

        if (this.parent != null && !this.extraButtonsRegistered) {
            this.extraButtonsRegistered = true;

            final Button returnBackButton = new Button(this.size - 1) {
                @Override
                public ItemStack getItem() {
                    final ItemStack item = new ItemStack(Material.OAK_DOOR);
                    final ItemMeta meta = item.getItemMeta();
                    meta.displayName(Component.text("Return Back", NamedTextColor.WHITE));
                    item.setItemMeta(meta);

                    return item;
                }

                @Override
                public void onClick(Player player) {
                    try {
                        final Menu newMenuInstance = parent.getClass().getConstructor().newInstance();

                        newMenuInstance.displayTo(player);

                    } catch (final ReflectiveOperationException ex) {
                        ex.printStackTrace();
                    }
                }
            };

            this.buttons.add(returnBackButton);
            inventory.setItem(returnBackButton.getSlot(), returnBackButton.getItem());
        }

        if (player.hasMetadata(IN_MENU_METADATA))
            player.closeInventory();

        player.setMetadata(IN_MENU_METADATA, new FixedMetadataValue(CobaltKingdoms.getInstance(), this));

        player.openInventory(inventory);
    }
}