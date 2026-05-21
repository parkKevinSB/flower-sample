package io.github.parkkevinsb.flower.sample.durable.workflow.step;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.sample.durable.domain.OrderRepository;

abstract class TimedOrderStep extends Step {

    private static final int START = 0;
    private static final int WORKING = 10;
    private static final long VISIBLE_WORK_MILLIS = 5_000L;

    protected final OrderRepository orders;
    protected final String orderId;

    TimedOrderStep(OrderRepository orders, String orderId) {
        this.orders = orders;
        this.orderId = orderId;
    }

    @Override
    protected void onEnter(StepContext ctx) {
        if (ctx.stepNo() == WORKING) {
            ctx.startTimeout(VISIBLE_WORK_MILLIS);
        }
    }

    @Override
    protected StepResult onTick(StepContext ctx) {
        if (ctx.stepNo() == START) {
            ctx.startTimeout(VISIBLE_WORK_MILLIS);
            ctx.setStepNo(WORKING);
            return StepResult.stay();
        }
        if (ctx.stepNo() == WORKING) {
            if (!ctx.timedOut()) {
                return StepResult.stay();
            }
            apply();
            return StepResult.done();
        }
        return StepResult.fail(new IllegalStateException("unknown timed order stepNo: " + ctx.stepNo()));
    }

    protected abstract void apply();
}
