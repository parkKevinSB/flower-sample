package io.github.parkkevinsb.flower.sample.cafe.event;

/**
 * Plain event object published when the order is ready to be brewed.
 */
public final class BrewRequestedEvent {

    private final String orderId;

    public BrewRequestedEvent(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
