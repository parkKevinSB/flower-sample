package io.github.parkkevinsb.flower.sample.cafe.workflow.step;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.observability.logging.StepLogger;
import io.github.parkkevinsb.flower.sample.cafe.domain.CafeOrderStore;

/**
 * Records the order in the store, leaves it visible as ACCEPTED for a short
 * sample delay, then advances to the next Step.
 *
 * <p>Demonstrates the simplest Step shape: do a single store mutation, log,
 * and return ADVANCE.
 */
public final class AcceptOrderStep extends Step {

    private static final int ACCEPT_ORDER = 0;
    private static final int SHOW_ACCEPTED = 10;
    private static final long ACCEPTED_VISIBLE_MS = 1_200L;

    private final CafeOrderStore store;

    public AcceptOrderStep(CafeOrderStore store) {
        this.store = store;
    }

    @Override
    protected StepResult onTick(StepContext ctx) {
        String orderId = ctx.flowId().flowKey();

        if (ctx.stepNo() == ACCEPT_ORDER) {
            store.accept(orderId);
            StepLogger.of(AcceptOrderStep.class, ctx).info("order accepted");
            ctx.startTimeout(ACCEPTED_VISIBLE_MS);
            ctx.setStepNo(SHOW_ACCEPTED);
            return StepResult.stay();
        }

        if (ctx.stepNo() == SHOW_ACCEPTED) {
            return ctx.timedOut() ? StepResult.advance() : StepResult.stay();
        }

        return StepResult.fail(new IllegalStateException("unknown accept-order stepNo: " + ctx.stepNo()));
    }
}
