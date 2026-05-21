package io.github.parkkevinsb.flower.sample.durable.workflow.step;

import io.github.parkkevinsb.flower.sample.durable.domain.OrderRepository;

public final class ValidateOrderStep extends TimedOrderStep {

    public ValidateOrderStep(OrderRepository orders, String orderId) {
        super(orders, orderId);
    }

    @Override
    protected void apply() {
        orders.validate(orderId);
    }
}
