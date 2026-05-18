package io.github.parkkevinsb.flower.sample.battle.workflow.step;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.observability.logging.StepLogger;
import io.github.parkkevinsb.flower.sample.battle.domain.Battle;
import io.github.parkkevinsb.flower.sample.battle.domain.BattleStatus;
import io.github.parkkevinsb.flower.sample.battle.domain.BattleStore;
import io.github.parkkevinsb.flower.sample.battle.event.PlayerAttackEvent;
import io.github.parkkevinsb.flower.sample.battle.event.PlayerFleeEvent;
import io.github.parkkevinsb.flower.sample.battle.workflow.factory.TurnBattleFlowFactory;

/**
 * Waits for the player's sword swing command.
 *
 * <p>The Bloom callback only sets a signal. The battle row is mutated later
 * from Worker-owned Step.onTick, which keeps Flower responsible for
 * orchestration and Bloom responsible only for event delivery.
 */
public final class WaitPlayerActionStep extends Step {

    private static final String SIGNAL_PLAYER_ATTACK = "player-attack";
    private static final String SIGNAL_PLAYER_FLEE = "player-flee";
    private static final int START_WAIT = 0;
    private static final int WAITING = 10;

    private final BattleStore store;

    public WaitPlayerActionStep(BattleStore store) {
        this.store = store;
    }

    @Override
    protected void onEnter(StepContext ctx) {
        ctx.subscribe(PlayerAttackEvent.class, event -> onPlayerAttack(ctx, event));
        ctx.subscribe(PlayerFleeEvent.class, event -> onPlayerFlee(ctx, event));
    }

    private void onPlayerAttack(StepContext ctx, PlayerAttackEvent event) {
        if (ctx.flowId().flowKey().equals(event.getBattleId())) {
            ctx.signal(SIGNAL_PLAYER_ATTACK);
        }
    }

    private void onPlayerFlee(StepContext ctx, PlayerFleeEvent event) {
        if (ctx.flowId().flowKey().equals(event.getBattleId())) {
            ctx.signal(SIGNAL_PLAYER_FLEE);
        }
    }

    @Override
    protected StepResult onTick(StepContext ctx) {
        String battleId = ctx.flowId().flowKey();

        if (ctx.stepNo() == START_WAIT) {
            store.setStatus(battleId, BattleStatus.WAITING_PLAYER_ACTION);
            StepLogger.of(WaitPlayerActionStep.class, ctx).info("waiting for player action");
            ctx.setStepNo(WAITING);
            return StepResult.stay();
        }

        if (ctx.stepNo() == WAITING) {
            if (ctx.hasSignal(SIGNAL_PLAYER_ATTACK)) {
                StepLogger.of(WaitPlayerActionStep.class, ctx).info("player attack signal received");
                return StepResult.done();
            }
            if (ctx.hasSignal(SIGNAL_PLAYER_FLEE)) {
                Battle battle = store.attemptFlee(battleId);
                if (battle.isFinished()) {
                    StepLogger.of(WaitPlayerActionStep.class, ctx).info("player escaped");
                    return StepResult.finish();
                }
                StepLogger.of(WaitPlayerActionStep.class, ctx).info("player failed to escape");
                return StepResult.goTo(TurnBattleFlowFactory.MONSTER_ACTION);
            }
            return StepResult.stay();
        }

        return StepResult.fail(new IllegalStateException("unknown wait-player stepNo: " + ctx.stepNo()));
    }
}
