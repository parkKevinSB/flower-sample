package io.github.parkkevinsb.flower.sample.battle.workflow.step;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.observability.logging.StepLogger;
import io.github.parkkevinsb.flower.sample.battle.domain.Battle;
import io.github.parkkevinsb.flower.sample.battle.domain.BattleStore;
import io.github.parkkevinsb.flower.sample.battle.workflow.factory.TurnBattleFlowFactory;

/**
 * Advances the turn counter and jumps the Flow back to the first Step.
 */
public final class FinishTurnStep extends Step {

    private final BattleStore store;

    public FinishTurnStep(BattleStore store) {
        this.store = store;
    }

    @Override
    protected StepResult onTick(StepContext ctx) {
        String battleId = ctx.flowId().flowKey();
        Battle next = store.nextTurn(battleId);
        StepLogger.of(FinishTurnStep.class, ctx).info("turn finished, looping to turn " + next.getTurn());
        return StepResult.goTo(TurnBattleFlowFactory.START_TURN);
    }
}
