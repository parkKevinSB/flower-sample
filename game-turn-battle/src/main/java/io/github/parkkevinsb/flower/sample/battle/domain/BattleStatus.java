package io.github.parkkevinsb.flower.sample.battle.domain;

public enum BattleStatus {
    TURN_STARTED,
    WAITING_PLAYER_ACTION,
    ANIMATING_PLAYER_ATTACK,
    FLEE_FAILED,
    MONSTER_TURN,
    ESCAPED,
    VICTORY,
    DEFEAT
}
