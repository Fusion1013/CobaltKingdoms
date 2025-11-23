package se.fusion1013.cobaltKingdoms.villager;

import java.util.List;

public class VillagerTrade {
    public final List<TradeEntry> ingredients;
    public final TradeEntry result;

    public VillagerTrade(List<TradeEntry> ingredients, TradeEntry result) {
        this.ingredients = ingredients;
        this.result = result;
    }
}