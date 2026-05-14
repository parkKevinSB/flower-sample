package io.github.parkkevinsb.flower.sample.cafe.workflow.step;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.observability.logging.StepLogger;
import io.github.parkkevinsb.flower.sample.cafe.domain.CafeOrderStore;

/**
 * Demonstrates a Step that only uses stepNo.
 *
 * <p>No Bloom event, signal, or timeout is involved. Returning
 * {@link StepResult#stay()} after changing stepNo lets the Worker come back on
 * the next tick and continue from the next small internal stage.
 */
public final class PrepareCupStep extends Step {

    private static final int PICK_CUP = 0;
    private static final int LABEL_CUP = 10;
    private static final int SHOW_CUP_PREPARED = 20;
    private static final long PREPARE_CUP_MS = 1_600L;
    private static final long CUP_PREPARED_VISIBLE_MS = 1_400L;

    private final CafeOrderStore store;

    public PrepareCupStep(CafeOrderStore store) {
        this.store = store;
    }

    @Override
    protected StepResult onTick(StepContext ctx) {
        String orderId = ctx.flowId().flowKey();

        if (ctx.stepNo() == PICK_CUP) {
            StepLogger.of(PrepareCupStep.class, ctx).info("pick cup");
            ctx.startTimeout(PREPARE_CUP_MS);
            ctx.setStepNo(LABEL_CUP);
            return StepResult.stay();
        }

        if (ctx.stepNo() == LABEL_CUP) {
            if (!ctx.timedOut()) {
                return StepResult.stay();
            }
            store.cupPrepared(orderId);
            StepLogger.of(PrepareCupStep.class, ctx).info("cup prepared");
            ctx.startTimeout(CUP_PREPARED_VISIBLE_MS);
            ctx.setStepNo(SHOW_CUP_PREPARED);
            return StepResult.stay();
        }

        if (ctx.stepNo() == SHOW_CUP_PREPARED) {
            return ctx.timedOut() ? StepResult.advance() : StepResult.stay();
        }

        return StepResult.fail(new IllegalStateException("unknown prepare-cup stepNo: " + ctx.stepNo()));
    }
}
