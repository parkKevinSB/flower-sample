package io.github.parkkevinsb.flower.sample.battle.workflow.step;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.observability.logging.StepLogger;
import io.github.parkkevinsb.flower.sample.battle.domain.Battle;
import io.github.parkkevinsb.flower.sample.battle.domain.BattleStore;

/**
 * Ends the Flow when either side reaches 0 HP.
 */
public final class CheckBattleEndStep extends Step {

    private final BattleStore store;

    public CheckBattleEndStep(BattleStore store) {
        this.store = store;
    }

    @Override
    protected StepResult onTick(StepContext ctx) {
        String battleId = ctx.flowId().flowKey();
        Battle battle = store.find(battleId);

        if (battle.getMonsterHp() <= 0) {
            store.finishVictory(battleId);
            StepLogger.of(CheckBattleEndStep.class, ctx).info("hero wins");
            return StepResult.finish();
        }
        if (battle.getHeroHp() <= 0) {
            store.finishDefeat(battleId);
            StepLogger.of(CheckBattleEndStep.class, ctx).info("monster wins");
            return StepResult.finish();
        }

        StepLogger.of(CheckBattleEndStep.class, ctx).info("battle continues");
        return StepResult.done();
    }
}
