package io.github.parkkevinsb.flower.sample.durable.workflow.step;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.sample.durable.domain.OrderRepository;

public final class WaitPaymentStep extends Step {

    private static final int PAYMENT_CONFIRMED = 10_000;
    private static final long PAYMENT_CONFIRMED_VISIBLE_MILLIS = 5_000L;

    private final OrderRepository orders;
    private final String orderId;

    public WaitPaymentStep(OrderRepository orders, String orderId) {
        this.orders = orders;
        this.orderId = orderId;
    }

    @Override
    protected void onEnter(StepContext ctx) {
        orders.waitForPayment(orderId);
        if (ctx.stepNo() >= PAYMENT_CONFIRMED) {
            ctx.startTimeout(PAYMENT_CONFIRMED_VISIBLE_MILLIS);
        }
    }

    @Override
    protected StepResult onTick(StepContext ctx) {
        if (ctx.stepNo() >= PAYMENT_CONFIRMED) {
            return ctx.timedOut() ? StepResult.done() : StepResult.stay();
        }
        if (orders.isPaid(orderId)) {
            ctx.startTimeout(PAYMENT_CONFIRMED_VISIBLE_MILLIS);
            ctx.setStepNo(PAYMENT_CONFIRMED);
            return StepResult.stay();
        }
        ctx.setStepNo(ctx.stepNo() + 1);
        return StepResult.stay();
    }
}
