package io.github.parkkevinsb.flower.sample.battle.api;

import io.github.parkkevinsb.flower.sample.battle.domain.LootItem;

public final class ItemResponse {

    private final String id;
    private final String name;
    private final String rarity;
    private final String rarityClass;
    private final String color;
    private final int attackBonus;
    private final int defenseBonus;
    private final int maxHpBonus;
    private final String statSummary;

    public ItemResponse(LootItem item) {
        this.id = item.name();
        this.name = item.getDisplayName();
        this.rarity = item.getRarityName();
        this.rarityClass = item.getRarityClass();
        this.color = item.getColor();
        this.attackBonus = item.getAttackBonus();
        this.defenseBonus = item.getDefenseBonus();
        this.maxHpBonus = item.getMaxHpBonus();
        this.statSummary = item.getStatSummary();
    }

    public static ItemResponse of(LootItem item) {
        return new ItemResponse(item);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getRarity() { return rarity; }
    public String getRarityClass() { return rarityClass; }
    public String getColor() { return color; }
    public int getAttackBonus() { return attackBonus; }
    public int getDefenseBonus() { return defenseBonus; }
    public int getMaxHpBonus() { return maxHpBonus; }
    public String getStatSummary() { return statSummary; }
}
