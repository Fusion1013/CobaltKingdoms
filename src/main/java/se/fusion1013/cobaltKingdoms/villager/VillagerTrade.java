package se.fusion1013.cobaltKingdoms.villager;

import java.util.List;

public record VillagerTrade(List<TradeEntry> ingredients, TradeEntry result) {

    @Override
    public String toString() {
        return "VillagerTrade{" +
                "ingredients=" + ingredients +
                ", result=" + result +
                '}';
    }
}