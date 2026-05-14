package io.github.parkkevinsb.flower.sample.battle.domain;

import java.util.Collection;

public interface BattleStore {

    Battle start(String battleId, MonsterType requestedMonster);

    Battle nextTurn(String battleId);

    Battle setStatus(String battleId, BattleStatus status);

    Battle applyHeroAttack(String battleId);

    Battle applyMonsterAttack(String battleId);

    Battle attemptFlee(String battleId);

    Battle finishVictory(String battleId);

    Battle finishDefeat(String battleId);

    Battle find(String battleId);

    Collection<Battle> findAll();

    GameState snapshot();

    void reset();
}
