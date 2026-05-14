package io.github.parkkevinsb.flower.sample.battle.domain;

import java.util.ArrayList;
import java.util.List;

public final class Hero {

    private final int level;
    private final int xp;
    private final int maxHp;
    private final int currentHp;
    private final int attack;
    private final int defense;
    private final List<LootItem> inventory;

    public Hero(int level, int xp, int maxHp, int attack, int defense, List<LootItem> inventory) {
        this(level, xp, maxHp, maxHp, attack, defense, inventory);
    }

    public Hero(int level, int xp, int maxHp, int currentHp, int attack, int defense, List<LootItem> inventory) {
        this.level = level;
        this.xp = xp;
        this.maxHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.inventory = inventory == null ? List.of() : List.copyOf(inventory);
        this.currentHp = Math.max(0, Math.min(currentHp, getMaxHp()));
    }

    public static Hero initial() {
        return new Hero(1, 0, 100, 22, 4, List.of());
    }

    public int damageAgainst(MonsterType monster) {
        return Math.max(1, getAttack() - monster.getDefense());
    }

    public int damageTakenFrom(MonsterType monster) {
        return Math.max(1, monster.getAttack() - getDefense());
    }

    public HeroReward gainReward(int gainedXp, LootItem item) {
        return gainReward(gainedXp, item, currentHp);
    }

    public HeroReward gainReward(int gainedXp, LootItem item, int survivingHp) {
        int nextLevel = level;
        int nextXp = xp + gainedXp;
        int nextMaxHp = maxHp;
        int nextCurrentHp = Math.max(0, survivingHp);
        int nextAttack = attack;
        int nextDefense = defense;
        List<LootItem> nextInventory = new ArrayList<>(inventory);
        List<String> logs = new ArrayList<>();
        boolean leveledUp = false;

        if (item == null) {
            logs.add("No item dropped this time.");
        } else {
            nextInventory.add(item);
            logs.add("Server reward: " + item.getDisplayName()
                    + " (" + item.getRarityName() + ", " + item.getStatSummary() + ").");
        }
        logs.add("Hero gains " + gainedXp + " XP.");

        while (nextXp >= xpNeededFor(nextLevel)) {
            nextXp -= xpNeededFor(nextLevel);
            nextLevel++;
            nextMaxHp += 18;
            nextAttack += 5;
            nextDefense += 2;
            leveledUp = true;
            logs.add("Level up! Hero is now level " + nextLevel + ".");
        }

        Hero next = new Hero(nextLevel, nextXp, nextMaxHp, nextCurrentHp, nextAttack, nextDefense, nextInventory);
        if (leveledUp) {
            next = next.withCurrentHp(next.getMaxHp());
            logs.add("HP restored to full.");
        }
        return new HeroReward(next, logs);
    }

    public Hero withCurrentHp(int nextCurrentHp) {
        return new Hero(level, xp, maxHp, nextCurrentHp, attack, defense, inventory);
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public int getNextLevelXp() {
        return xpNeededFor(level);
    }

    public int getMaxHp() {
        int total = maxHp;
        for (LootItem item : inventory) {
            total += item.getMaxHpBonus();
        }
        return total;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getAttack() {
        int total = attack;
        for (LootItem item : inventory) {
            total += item.getAttackBonus();
        }
        return total;
    }

    public int getDefense() {
        int total = defense;
        for (LootItem item : inventory) {
            total += item.getDefenseBonus();
        }
        return total;
    }

    public List<LootItem> getInventory() {
        return inventory;
    }

    private static int xpNeededFor(int level) {
        return 55 + (level - 1) * 35;
    }
}
