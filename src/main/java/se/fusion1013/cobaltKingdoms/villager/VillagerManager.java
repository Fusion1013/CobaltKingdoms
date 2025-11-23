package se.fusion1013.cobaltKingdoms.villager;

import org.bukkit.entity.Villager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.util.FileUtil;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class VillagerManager extends Manager<CobaltKingdoms> {

    private static final Map<Villager.Profession, List<VillagerTrade>> PROFESSION_TRADES = new HashMap<>();

    public static List<VillagerTrade> getTrades(Villager.Profession profession) {
        return PROFESSION_TRADES.getOrDefault(profession, Collections.emptyList());
    }

    public void loadTradesForProfession(Villager.Profession profession) {

        File file = getProfessionTradeFile(profession);

        List<VillagerTrade> trades = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = reader.readLine()) != null) {

                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                // Example: 20:raw_copper,,16:gold_coin
                String[] parts = line.split(",");

                List<TradeEntry> ingredients = new ArrayList<>();
                TradeEntry result = null;

                for (int i = 0; i < parts.length; i++) {
                    String part = parts[i].trim();
                    if (part.isEmpty()) continue;

                    String[] split = part.split(":");
                    if (split.length != 2) continue;

                    int amount = Integer.parseInt(split[0]);
                    String item = split[1];

                    TradeEntry entry = new TradeEntry(amount, item);

                    // Last section = result
                    if (i == parts.length - 1) {
                        result = entry;
                    } else {
                        ingredients.add(entry);
                    }
                }

                if (result != null) {
                    trades.add(new VillagerTrade(ingredients, result));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        PROFESSION_TRADES.put(profession, trades);
    }

    private File getProfessionTradeFile(Villager.Profession profession) {
        return FileUtil.getOrCreateFileFromResource(CobaltKingdoms.getInstance(), "villager/" + profession.getKey().getKey() + "_trades.txt");
    }

    public VillagerManager(CobaltKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        loadTradesForProfession(Villager.Profession.ARMORER);
        loadTradesForProfession(Villager.Profession.BUTCHER);
        loadTradesForProfession(Villager.Profession.CARTOGRAPHER);
        loadTradesForProfession(Villager.Profession.FARMER);
        loadTradesForProfession(Villager.Profession.FISHERMAN);
        loadTradesForProfession(Villager.Profession.FLETCHER);
        loadTradesForProfession(Villager.Profession.LEATHERWORKER);
        loadTradesForProfession(Villager.Profession.LIBRARIAN);
        loadTradesForProfession(Villager.Profession.MASON);
        loadTradesForProfession(Villager.Profession.SHEPHERD);
        loadTradesForProfession(Villager.Profession.TOOLSMITH);
        loadTradesForProfession(Villager.Profession.WEAPONSMITH);
    }

    @Override
    public void disable() {

    }
}
