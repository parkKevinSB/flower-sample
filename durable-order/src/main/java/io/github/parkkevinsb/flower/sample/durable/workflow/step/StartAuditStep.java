package io.github.parkkevinsb.flower.sample.durable.workflow.step;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.sample.durable.domain.OrderRepository;

public final class StartAuditStep extends Step {

    private final OrderRepository orders;
    private final String message;

    public StartAuditStep(OrderRepository orders, String message) {
        this.orders = orders;
        this.message = message;
    }

    @Override
    protected StepResult onTick(StepContext ctx) {
        orders.addAudit("TRANSIENT_STARTED", message);
        return StepResult.done();
    }
}
