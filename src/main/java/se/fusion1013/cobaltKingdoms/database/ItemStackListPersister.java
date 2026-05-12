package se.fusion1013.cobaltKingdoms.database;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltKingdoms.util.ItemSerializationUtils;
import se.fusion1013.cobaltKingdoms.util.ItemStackList;

import java.sql.SQLException;
import java.util.List;

public class ItemStackListPersister extends StringType {

    private static final ItemStackListPersister singleton = new ItemStackListPersister();

    public static ItemStackListPersister getSingleton() {
        return singleton;
    }

    private ItemStackListPersister() {
        super(SqlType.STRING, new Class<?>[]{ItemStackList.class});
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
        if (javaObject == null) return List.of();

        ItemStackList list = (ItemStackList) javaObject;
        return ItemSerializationUtils.serializeItemStacks(list.list());
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        List<ItemStack> list = ItemSerializationUtils.deserializeItemStacks((String) sqlArg);
        return new ItemStackList(list);
    }
}
