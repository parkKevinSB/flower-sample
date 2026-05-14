package io.github.parkkevinsb.flower.sample.battle.api;

import io.github.parkkevinsb.flower.sample.battle.domain.Hero;
import io.github.parkkevinsb.flower.sample.battle.domain.LootItem;

import java.util.List;

public final class HeroResponse {

    private final int level;
    private final int xp;
    private final int nextLevelXp;
    private final int currentHp;
    private final int maxHp;
    private final int attack;
    private final int defense;
    private final List<LootItem> inventory;
    private final List<String> inventoryNames;
    private final List<ItemResponse> items;

    public HeroResponse(Hero hero) {
        this.level = hero.getLevel();
        this.xp = hero.getXp();
        this.nextLevelXp = hero.getNextLevelXp();
        this.currentHp = hero.getCurrentHp();
        this.maxHp = hero.getMaxHp();
        this.attack = hero.getAttack();
        this.defense = hero.getDefense();
        this.inventory = hero.getInventory();
        this.inventoryNames = hero.getInventory().stream()
                .map(LootItem::getDisplayName)
                .toList();
        this.items = hero.getInventory().stream()
                .map(ItemResponse::of)
                .toList();
    }

    public static HeroResponse of(Hero hero) {
        return new HeroResponse(hero);
    }

    public int getLevel() { return level; }
    public int getXp() { return xp; }
    public int getNextLevelXp() { return nextLevelXp; }
    public int getCurrentHp() { return currentHp; }
    public int getMaxHp() { return maxHp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public List<LootItem> getInventory() { return inventory; }
    public List<String> getInventoryNames() { return inventoryNames; }
    public List<ItemResponse> getItems() { return items; }
}
