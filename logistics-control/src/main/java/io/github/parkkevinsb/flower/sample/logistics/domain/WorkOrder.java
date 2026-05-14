package io.github.parkkevinsb.flower.sample.logistics.domain;

import java.time.Instant;

public final class WorkOrder {

    private final String workOrderId;
    private final WorkOrderStatus status;
    private final Instant updatedAt;

    public WorkOrder(
            String workOrderId,
            WorkOrderStatus status,
            Instant updatedAt
    ) {
        this.workOrderId = workOrderId;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    public WorkOrder withStatus(WorkOrderStatus nextStatus) {
        return new WorkOrder(
                workOrderId,
                nextStatus,
                Instant.now());
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public WorkOrderStatus getStatus() {
        return status;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
