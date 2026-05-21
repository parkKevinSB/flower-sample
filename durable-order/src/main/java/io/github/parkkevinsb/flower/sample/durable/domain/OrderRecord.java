package io.github.parkkevinsb.flower.sample.durable.domain;

import java.time.Instant;

public final class OrderRecord {
    private final String orderId;
    private final OrderStatus status;
    private final boolean paymentReceived;
    private final boolean inventoryReserved;
    private final boolean shipped;
    private final boolean completed;
    private final Instant createdAt;
    private final Instant updatedAt;

    public OrderRecord(
            String orderId,
            OrderStatus status,
            boolean paymentReceived,
            boolean inventoryReserved,
            boolean shipped,
            boolean completed,
            Instant createdAt,
            Instant updatedAt) {
        this.orderId = orderId;
        this.status = status;
        this.paymentReceived = paymentReceived;
        this.inventoryReserved = inventoryReserved;
        this.shipped = shipped;
        this.completed = completed;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getOrderId() {
        return orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public boolean isPaymentReceived() {
        return paymentReceived;
    }

    public boolean isInventoryReserved() {
        return inventoryReserved;
    }

    public boolean isShipped() {
        return shipped;
    }

    public boolean isCompleted() {
        return completed;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
