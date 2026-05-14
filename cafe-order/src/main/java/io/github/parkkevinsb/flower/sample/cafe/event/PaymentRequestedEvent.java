package io.github.parkkevinsb.flower.sample.cafe.event;

/**
 * Plain event object published when the order needs payment approval.
 *
 * <p>Bloom only cares about the runtime class of the object. There is no base
 * event type and no annotation requirement.
 */
public final class PaymentRequestedEvent {

    private final String orderId;

    public PaymentRequestedEvent(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
