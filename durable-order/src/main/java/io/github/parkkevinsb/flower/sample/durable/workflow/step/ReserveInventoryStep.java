package io.github.parkkevinsb.flower.sample.durable.workflow.step;

import io.github.parkkevinsb.flower.sample.durable.domain.OrderRepository;

public final class ReserveInventoryStep extends TimedOrderStep {

    public ReserveInventoryStep(OrderRepository orders, String orderId) {
        super(orders, orderId);
    }

    @Override
    protected void apply() {
        orders.reserveInventory(orderId);
    }
}
