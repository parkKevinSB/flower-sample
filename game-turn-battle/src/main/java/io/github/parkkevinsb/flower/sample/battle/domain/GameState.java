package io.github.parkkevinsb.flower.sample.battle.domain;

public final class GameState {

    private final Hero hero;
    private final Battle battle;

    public GameState(Hero hero, Battle battle) {
        this.hero = hero;
        this.battle = battle;
    }

    public Hero getHero() {
        return hero;
    }

    public Battle getBattle() {
        return battle;
    }
}
