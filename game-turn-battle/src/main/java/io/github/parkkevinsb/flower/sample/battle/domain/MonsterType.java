package io.github.parkkevinsb.flower.sample.battle.domain;

import java.util.Random;

public enum MonsterType {
    BOAR("Wild Boar", 45, 12, 1, 35, 20, 45, 85, 72, 23, 4, 1),
    ORC("Orc", 85, 20, 4, 65, 22, 50, 70, 62, 28, 8, 2),
    SKELETON_KNIGHT("Skeleton Knight", 95, 18, 5, 80, 18, 55, 62, 55, 31, 11, 3),
    WRAITH("Wraith", 120, 24, 7, 105, 15, 60, 50, 45, 35, 15, 5),
    WYVERN("Wyvern", 180, 40, 8, 125, 12, 65, 38, 34, 36, 22, 8),
    DRAGON("Dragon", 200, 70, 10, 170, 8, 72, 25, 24, 35, 30, 11),
    ABYSS_LORD("Abyss Lord", 600, 200, 22, 340, 4, 85, 12, 5, 25, 40, 30),
    VOID_EMPEROR("Void Emperor", 1400, 520, 55, 1200, 1, 95, 3, 0, 5, 30, 65);

    private static final MonsterType[] VALUES = values();
    private static final int TOTAL_ENCOUNTER_WEIGHT;

    static {
        int total = 0;
        for (MonsterType monster : VALUES) {
            total += monster.encounterWeight;
        }
        TOTAL_ENCOUNTER_WEIGHT = total;
    }

    private final String displayName;
    private final int maxHp;
    private final int attack;
    private final int defense;
    private final int xpReward;
    private final int encounterWeight;
    private final int itemDropChancePercent;
    private final int fleeChancePercent;
    private final int commonDropWeight;
    private final int magicDropWeight;
    private final int rareDropWeight;
    private final int uniqueDropWeight;

    MonsterType(
            String displayName,
            int maxHp,
            int attack,
            int defense,
            int xpReward,
            int encounterWeight,
            int itemDropChancePercent,
            int fleeChancePercent,
            int commonDropWeight,
            int magicDropWeight,
            int rareDropWeight,
            int uniqueDropWeight
    ) {
        this.displayName = displayName;
        this.maxHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.xpReward = xpReward;
        this.encounterWeight = encounterWeight;
        this.itemDropChancePercent = itemDropChancePercent;
        this.fleeChancePercent = fleeChancePercent;
        this.commonDropWeight = commonDropWeight;
        this.magicDropWeight = magicDropWeight;
        this.rareDropWeight = rareDropWeight;
        this.uniqueDropWeight = uniqueDropWeight;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getXpReward() {
        return xpReward;
    }

    public int getEncounterWeight() {
        return encounterWeight;
    }

    public int getEncounterChancePercent() {
        return encounterWeight * 100 / TOTAL_ENCOUNTER_WEIGHT;
    }

    public int getItemDropChancePercent() {
        return itemDropChancePercent;
    }

    public int getFleeChancePercent() {
        return fleeChancePercent;
    }

    public int getCommonDropWeight() {
        return commonDropWeight;
    }

    public int getMagicDropWeight() {
        return magicDropWeight;
    }

    public int getRareDropWeight() {
        return rareDropWeight;
    }

    public int getUniqueDropWeight() {
        return uniqueDropWeight;
    }

    public static MonsterType random(Random random) {
        int roll = random.nextInt(TOTAL_ENCOUNTER_WEIGHT);
        int cursor = 0;
        for (MonsterType monster : VALUES) {
            cursor += monster.encounterWeight;
            if (roll < cursor) {
                return monster;
            }
        }
        return BOAR;
    }
}
