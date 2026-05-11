package se.fusion1013.cobaltKingdoms.database;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltKingdoms.util.ItemSerializationUtils;

import java.util.List;

public class ItemStackPersister extends StringType {

    private static final ItemStackPersister singleton = new ItemStackPersister();

    public static ItemStackPersister getSingleton() {
        return singleton;
    }

    private ItemStackPersister() {
        super(SqlType.STRING, new Class<?>[]{ItemStack.class});
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        if (javaObject == null) return null;

        ItemStack itemStack = (ItemStack) javaObject;
        return ItemSerializationUtils.serializeItemStacks(List.of(itemStack));
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        List<ItemStack> itemStacks = ItemSerializationUtils.deserializeItemStacks((String) sqlArg);
        return itemStacks.getFirst();
    }
}
