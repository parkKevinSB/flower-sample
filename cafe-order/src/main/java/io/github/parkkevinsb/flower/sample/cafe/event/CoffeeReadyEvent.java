package io.github.parkkevinsb.flower.sample.cafe.event;

/**
 * Reply event published when the coffee is ready.
 */
public final class CoffeeReadyEvent {

    private final String orderId;

    public CoffeeReadyEvent(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
