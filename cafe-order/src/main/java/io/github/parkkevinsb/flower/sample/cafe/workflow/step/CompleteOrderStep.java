package io.github.parkkevinsb.flower.sample.cafe.workflow.step;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.observability.logging.StepLogger;
import io.github.parkkevinsb.flower.sample.cafe.domain.CafeOrderStore;

public final class CompleteOrderStep extends Step {

    private final CafeOrderStore store;

    public CompleteOrderStep(CafeOrderStore store) {
        this.store = store;
    }

    @Override
    protected StepResult onTick(StepContext ctx) {
        String orderId = ctx.flowId().flowKey();
        store.complete(orderId);
        StepLogger.of(CompleteOrderStep.class, ctx).info("order completed");
        return StepResult.done();
    }
}
