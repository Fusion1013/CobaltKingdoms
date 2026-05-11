package se.fusion1013.cobaltKingdoms.database;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationPersister extends StringType {

    private static final LocationPersister singleTon =
            new LocationPersister();

    public static LocationPersister getSingleton() {
        return singleTon;
    }

    private LocationPersister() {
        super(SqlType.STRING, new Class<?>[]{Location.class});
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {

        if (javaObject == null) {
            return null;
        }

        Location loc = (Location) javaObject;

        return loc.getWorld().getName() + ";" +
                loc.getX() + ";" +
                loc.getY() + ";" +
                loc.getZ() + ";" +
                loc.getYaw() + ";" +
                loc.getPitch();
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType,
                               Object sqlArg,
                               int columnPos) {

        String[] parts = ((String) sqlArg).split(";");

        World world = Bukkit.getWorld(parts[0]);

        if (world == null) {
            return null;
        }

        return new Location(
                world,
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3]),
                Float.parseFloat(parts[4]),
                Float.parseFloat(parts[5])
        );
    }
}
