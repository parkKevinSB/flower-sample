package io.github.parkkevinsb.flower.sample.battle.workflow.step;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.observability.logging.StepLogger;
import io.github.parkkevinsb.flower.sample.battle.domain.Battle;
import io.github.parkkevinsb.flower.sample.battle.domain.BattleStatus;
import io.github.parkkevinsb.flower.sample.battle.domain.BattleStore;

/**
 * Runs the monster's automatic counterattack after a short server-side windup.
 */
public final class EnemyActionStep extends Step {

    private static final int START_WINDUP = 0;
    private static final int WINDUP = 10;
    private static final long MONSTER_WINDUP_MS = 450L;

    private final BattleStore store;

    public EnemyActionStep(BattleStore store) {
        this.store = store;
    }

    @Override
    protected StepResult onTick(StepContext ctx) {
        String battleId = ctx.flowId().flowKey();

        if (ctx.stepNo() == START_WINDUP) {
            store.setStatus(battleId, BattleStatus.MONSTER_TURN);
            ctx.startTimeout(MONSTER_WINDUP_MS);
            ctx.setStepNo(WINDUP);
            StepLogger.of(EnemyActionStep.class, ctx).info("monster windup");
            return StepResult.stay();
        }

        if (ctx.stepNo() == WINDUP) {
            if (!ctx.timedOut()) {
                return StepResult.stay();
            }
            Battle battle = store.applyMonsterAttack(battleId);
            StepLogger.of(EnemyActionStep.class, ctx).info(
                    "monster attacked (hero hp " + battle.getHeroHp() + "/" + battle.getHeroMaxHp() + ")");
            return StepResult.advance();
        }

        return StepResult.fail(new IllegalStateException("unknown monster stepNo: " + ctx.stepNo()));
    }
}
