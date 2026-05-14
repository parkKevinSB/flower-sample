package io.github.parkkevinsb.flower.sample.cafe.event;

/**
 * Reply event published by the sample payment gateway.
 */
public final class PaymentApprovedEvent {

    private final String orderId;

    public PaymentApprovedEvent(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
