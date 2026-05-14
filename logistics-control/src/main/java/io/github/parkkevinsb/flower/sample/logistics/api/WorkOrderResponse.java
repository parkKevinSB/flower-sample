package io.github.parkkevinsb.flower.sample.logistics.api;

import io.github.parkkevinsb.flower.sample.logistics.domain.WorkOrder;
import io.github.parkkevinsb.flower.sample.logistics.domain.WorkOrderStatus;

import java.time.Instant;

public final class WorkOrderResponse {

    private final String workOrderId;
    private final WorkOrderStatus status;
    private final Instant updatedAt;

    public WorkOrderResponse(
            String workOrderId,
            WorkOrderStatus status,
            Instant updatedAt
    ) {
        this.workOrderId = workOrderId;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    public static WorkOrderResponse of(WorkOrder workOrder) {
        return new WorkOrderResponse(
                workOrder.getWorkOrderId(),
                workOrder.getStatus(),
                workOrder.getUpdatedAt());
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
