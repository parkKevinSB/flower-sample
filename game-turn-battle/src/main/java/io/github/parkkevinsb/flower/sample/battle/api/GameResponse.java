package io.github.parkkevinsb.flower.sample.battle.api;

import io.github.parkkevinsb.flower.sample.battle.domain.GameState;

public final class GameResponse {

    private final HeroResponse hero;
    private final BattleResponse battle;

    public GameResponse(HeroResponse hero, BattleResponse battle) {
        this.hero = hero;
        this.battle = battle;
    }

    public static GameResponse of(GameState state) {
        return new GameResponse(
                HeroResponse.of(state.getHero()),
                state.getBattle() == null ? null : BattleResponse.of(state.getBattle()));
    }

    public HeroResponse getHero() { return hero; }
    public BattleResponse getBattle() { return battle; }
}
