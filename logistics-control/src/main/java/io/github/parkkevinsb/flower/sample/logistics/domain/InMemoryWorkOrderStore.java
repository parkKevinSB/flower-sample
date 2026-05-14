package io.github.parkkevinsb.flower.sample.logistics.domain;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class InMemoryWorkOrderStore implements WorkOrderStore {

    private final ConcurrentMap<String, WorkOrder> workOrders = new ConcurrentHashMap<>();

    @Override
    public WorkOrder accept(WorkOrder workOrder) {
        return workOrders.compute(workOrder.getWorkOrderId(), (id, existing) ->
                workOrder.withStatus(WorkOrderStatus.ACCEPTED));
    }

    @Override
    public WorkOrder transition(String workOrderId, WorkOrderStatus next) {
        return workOrders.compute(workOrderId, (id, existing) -> {
            if (existing == null) {
                throw new IllegalStateException("unknown workOrderId: " + id);
            }
            if (existing.getStatus() == next) {
                return existing;
            }
            return existing.withStatus(next);
        });
    }

    @Override
    public WorkOrder find(String workOrderId) {
        return workOrders.get(workOrderId);
    }

    @Override
    public Collection<WorkOrder> findAll() {
        return Collections.unmodifiableCollection(workOrders.values());
    }
}
