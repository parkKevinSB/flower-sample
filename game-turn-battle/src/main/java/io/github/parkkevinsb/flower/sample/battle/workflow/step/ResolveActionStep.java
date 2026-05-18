package io.github.parkkevinsb.flower.sample.battle.workflow.step;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.observability.logging.StepLogger;
import io.github.parkkevinsb.flower.sample.battle.domain.Battle;
import io.github.parkkevinsb.flower.sample.battle.domain.BattleStatus;
import io.github.parkkevinsb.flower.sample.battle.domain.BattleStore;
import io.github.parkkevinsb.flower.sample.battle.event.AnimationFinishedEvent;

/**
 * Applies the hero's sword damage and waits for the browser animation to end.
 */
public final class ResolveActionStep extends Step {

    private static final String SIGNAL_ANIMATION_DONE = "animation-finished";
    private static final int APPLY_DAMAGE = 0;
    private static final int ANIMATING = 10;
    private static final long ANIMATION_TIMEOUT_MS = 2_000L;

    private final BattleStore store;

    public ResolveActionStep(BattleStore store) {
        this.store = store;
    }

    @Override
    protected void onEnter(StepContext ctx) {
        ctx.subscribe(AnimationFinishedEvent.class, event -> onAnimationFinished(ctx, event));
    }

    private void onAnimationFinished(StepContext ctx, AnimationFinishedEvent event) {
        if (ctx.flowId().flowKey().equals(event.getBattleId())) {
            ctx.signal(SIGNAL_ANIMATION_DONE);
        }
    }

    @Override
    protected StepResult onTick(StepContext ctx) {
        String battleId = ctx.flowId().flowKey();

        if (ctx.stepNo() == APPLY_DAMAGE) {
            Battle battle = store.applyHeroAttack(battleId);
            store.setStatus(battleId, BattleStatus.ANIMATING_PLAYER_ATTACK);
            StepLogger.of(ResolveActionStep.class, ctx).info(
                    "hero attacked (monster hp " + battle.getMonsterHp() + "/" + battle.getMonsterMaxHp() + ")");
            ctx.startTimeout(ANIMATION_TIMEOUT_MS);
            ctx.setStepNo(ANIMATING);
            return StepResult.stay();
        }

        if (ctx.stepNo() == ANIMATING) {
            if (ctx.hasSignal(SIGNAL_ANIMATION_DONE)) {
                StepLogger.of(ResolveActionStep.class, ctx).info("sword animation finished");
                return StepResult.done();
            }
            if (ctx.timedOut()) {
                StepLogger.of(ResolveActionStep.class, ctx).info("animation timeout, completing step anyway");
                return StepResult.done();
            }
            return StepResult.stay();
        }

        return StepResult.fail(new IllegalStateException("unknown resolve stepNo: " + ctx.stepNo()));
    }
}
