package io.github.parkkevinsb.flower.sample.battle.workflow.factory;

import io.github.parkkevinsb.flower.core.flow.Flow;
import org.springframework.stereotype.Component;

/**
 * Builds the turn-battle Flow.
 *
 * <p>The Flow runs one turn from top to bottom. {@code finish-turn} returns
 * {@code goTo("start-turn")}, which sends the Worker back to the first Step
 * for the next turn. The two end checks return {@code done()} when the hero
 * or monster reaches 0 HP.
 */
@Component
public final class TurnBattleFlowFactory {

    public static final String FLOW_TYPE = "turn-battle";

    public static final String START_TURN = "start-turn";
    public static final String WAIT_PLAYER_ACTION = "wait-player-action";
    public static final String RESOLVE_PLAYER_ATTACK = "resolve-player-attack";
    public static final String CHECK_AFTER_PLAYER = "check-after-player";
    public static final String MONSTER_ACTION = "monster-action";
    public static final String CHECK_AFTER_MONSTER = "check-after-monster";
    public static final String FINISH_TURN = "finish-turn";

    private final TurnBattleStepFactory steps;

    public TurnBattleFlowFactory(TurnBattleStepFactory steps) {
        this.steps = steps;
    }

    public Flow create(String battleId) {
        return Flow.builder(FLOW_TYPE, battleId)
                .step(START_TURN, steps.startTurn())
                .step(WAIT_PLAYER_ACTION, steps.waitPlayerAttack())
                .step(RESOLVE_PLAYER_ATTACK, steps.resolvePlayerAttack())
                .step(CHECK_AFTER_PLAYER, steps.checkBattleEnd())
                .step(MONSTER_ACTION, steps.monsterAction())
                .step(CHECK_AFTER_MONSTER, steps.checkBattleEnd())
                .step(FINISH_TURN, steps.finishTurn())
                .build();
    }
}
