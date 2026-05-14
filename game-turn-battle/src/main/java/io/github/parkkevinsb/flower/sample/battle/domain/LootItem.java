package io.github.parkkevinsb.flower.sample.battle.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum LootItem {
    CRACKED_RING("Cracked Ring", ItemRarity.COMMON, 1, 0, 0),
    TRAVELER_BUCKLER("Traveler Buckler", ItemRarity.COMMON, 0, 1, 0),
    BOAR_HIDE_VEST("Boar Hide Vest", ItemRarity.COMMON, 0, 1, 10),
    IRON_CHARM("Iron Charm", ItemRarity.COMMON, 1, 1, 4),

    SAPPHIRE_EDGE("Sapphire Edge", ItemRarity.MAGIC, 5, 0, 0),
    AZURE_GUARD("Azure Guard", ItemRarity.MAGIC, 0, 5, 12),
    STORM_CHARM("Storm Charm", ItemRarity.MAGIC, 5, 3, 0),
    VITALITY_PENDANT("Vitality Pendant", ItemRarity.MAGIC, 0, 2, 28),

    SUNFORGED_AXE("Sunforged Axe", ItemRarity.RARE, 10, 0, 0),
    GOLDEN_AEGIS("Golden Aegis", ItemRarity.RARE, 0, 10, 30),
    WAR_CROWN("War Crown", ItemRarity.RARE, 10, 5, 20),
    WYVERN_FANG("Wyvern Fang", ItemRarity.RARE, 20, 0, 20),

    HELLFIRE_EDGE("Hellfire Edge", ItemRarity.UNIQUE, 50, 0, 0),
    DRAGONHEART_PLATE("Dragonheart Plate", ItemRarity.UNIQUE, 6, 50, 70),
    CROWN_OF_THE_ABYSS("Crown of the Abyss", ItemRarity.UNIQUE, 10, 8, 400),
    WORLDBREAKER_CHARM("Worldbreaker Charm", ItemRarity.UNIQUE, 20, 20, 200);

    private static final LootItem[] VALUES = values();

    private final String displayName;
    private final ItemRarity rarity;
    private final int attackBonus;
    private final int defenseBonus;
    private final int maxHpBonus;

    LootItem(String displayName, ItemRarity rarity, int attackBonus, int defenseBonus, int maxHpBonus) {
        this.displayName = displayName;
        this.rarity = rarity;
        this.attackBonus = attackBonus;
        this.defenseBonus = defenseBonus;
        this.maxHpBonus = maxHpBonus;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ItemRarity getRarity() {
        return rarity;
    }

    public String getRarityName() {
        return rarity.getDisplayName();
    }

    public String getRarityClass() {
        return rarity.getCssClass();
    }

    public String getColor() {
        return rarity.getColor();
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }

    public int getMaxHpBonus() {
        return maxHpBonus;
    }

    public String getStatSummary() {
        List<String> stats = new ArrayList<>();
        if (attackBonus > 0) {
            stats.add("+" + attackBonus + " ATK");
        }
        if (defenseBonus > 0) {
            stats.add("+" + defenseBonus + " DEF");
        }
        if (maxHpBonus > 0) {
            stats.add("+" + maxHpBonus + " HP");
        }
        return String.join(", ", stats);
    }

    public int powerScore() {
        return attackBonus * 3 + defenseBonus * 3 + maxHpBonus;
    }

    public static LootItem random(Random random, MonsterType monster) {
        ItemRarity rarity = randomRarity(random, monster);
        List<LootItem> candidates = new ArrayList<>();
        for (LootItem item : VALUES) {
            if (item.rarity == rarity) {
                candidates.add(item);
            }
        }
        return candidates.get(random.nextInt(candidates.size()));
    }

    public static LootItem randomDrop(Random random, MonsterType monster) {
        if (random.nextInt(100) >= monster.getItemDropChancePercent()) {
            return null;
        }
        return random(random, monster);
    }

    private static ItemRarity randomRarity(Random random, MonsterType monster) {
        int roll = random.nextInt(100);
        int common = monster.getCommonDropWeight();
        int magic = monster.getMagicDropWeight();
        int rare = monster.getRareDropWeight();

        if (roll < common) {
            return ItemRarity.COMMON;
        }
        if (roll < common + magic) {
            return ItemRarity.MAGIC;
        }
        if (roll < common + magic + rare) {
            return ItemRarity.RARE;
        }
        return ItemRarity.UNIQUE;
    }
}
