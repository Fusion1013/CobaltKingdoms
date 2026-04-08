package se.fusion1013.cobaltKingdoms.loot;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.variable.StringVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LootDistribution implements ILootDistribution {

    private final StringVariable internalName = new StringVariable("internal_name");
    private final List<ItemStack> items = new ArrayList<>();

    public LootDistribution(YamlConfiguration yaml) {
        internalName.load(yaml);
        List<Map<?, ?>> mapList = yaml.getMapList("items");
        for (Map<?, ?> map : mapList) {
            String itemName = (String) map.get("item");
            int amount = (int) map.get("amount");

            ItemStack itemStack = CustomItemManager.getItemStack(itemName);
            if (itemStack == null) return;
            itemStack.setAmount(amount);
            items.add(itemStack);
        }
    }

    public LootDistribution(JsonObject json) {

    }

    @Override
    public String getInternalName() {
        return internalName.getValue();
    }

    @Override
    public List<ItemStack> getItems() {
        return items;
    }
}
