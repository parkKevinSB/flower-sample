package io.github.parkkevinsb.flower.sample.durable.workflow.step;

import io.github.parkkevinsb.flower.sample.durable.domain.OrderRepository;

public final class ShipOrderStep extends TimedOrderStep {

    public ShipOrderStep(OrderRepository orders, String orderId) {
        super(orders, orderId);
    }

    @Override
    protected void apply() {
        orders.ship(orderId);
    }
}
