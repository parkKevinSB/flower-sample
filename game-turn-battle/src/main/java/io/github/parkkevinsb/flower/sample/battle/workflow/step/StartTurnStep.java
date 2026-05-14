package io.github.parkkevinsb.flower.sample.battle.workflow.step;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.observability.logging.StepLogger;
import io.github.parkkevinsb.flower.sample.battle.domain.Battle;
import io.github.parkkevinsb.flower.sample.battle.domain.BattleStore;

public final class StartTurnStep extends Step {

    private final BattleStore store;

    public StartTurnStep(BattleStore store) {
        this.store = store;
    }

    @Override
    protected StepResult onTick(StepContext ctx) {
        Battle battle = store.find(ctx.flowId().flowKey());
        StepLogger.of(StartTurnStep.class, ctx).info("turn " + battle.getTurn() + " started"
                + " (hero " + battle.getHeroHp() + "/" + battle.getHeroMaxHp()
                + ", monster " + battle.getMonsterHp() + "/" + battle.getMonsterMaxHp() + ")");
        return StepResult.advance();
    }
}
