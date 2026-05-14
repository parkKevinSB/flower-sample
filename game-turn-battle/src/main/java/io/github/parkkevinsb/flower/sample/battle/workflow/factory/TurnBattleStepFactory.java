package io.github.parkkevinsb.flower.sample.battle.workflow.factory;

import io.github.parkkevinsb.flower.sample.battle.domain.BattleStore;
import io.github.parkkevinsb.flower.sample.battle.workflow.step.CheckBattleEndStep;
import io.github.parkkevinsb.flower.sample.battle.workflow.step.EnemyActionStep;
import io.github.parkkevinsb.flower.sample.battle.workflow.step.FinishTurnStep;
import io.github.parkkevinsb.flower.sample.battle.workflow.step.ResolveActionStep;
import io.github.parkkevinsb.flower.sample.battle.workflow.step.StartTurnStep;
import io.github.parkkevinsb.flower.sample.battle.workflow.step.WaitPlayerActionStep;
import org.springframework.stereotype.Component;

/**
 * Creates fresh Step instances with their dependencies.
 *
 * <p>Steps are per-Flow state holders (subscriptions, signals, stepNo, and
 * any waiting Step state) so a new instance is
 * required for every battle. The same Step instances are reused when the
 * Flow loops with {@code goTo("start-turn")}; only StepRuntime is reset as
 * each Step exits and re-enters.
 */
@Component
public final class TurnBattleStepFactory {

    private final BattleStore store;

    public TurnBattleStepFactory(BattleStore store) {
        this.store = store;
    }

    public StartTurnStep startTurn() {
        return new StartTurnStep(store);
    }

    public WaitPlayerActionStep waitPlayerAttack() {
        return new WaitPlayerActionStep(store);
    }

    public ResolveActionStep resolvePlayerAttack() {
        return new ResolveActionStep(store);
    }

    public EnemyActionStep monsterAction() {
        return new EnemyActionStep(store);
    }

    public CheckBattleEndStep checkBattleEnd() {
        return new CheckBattleEndStep(store);
    }

    public FinishTurnStep finishTurn() {
        return new FinishTurnStep(store);
    }
}
