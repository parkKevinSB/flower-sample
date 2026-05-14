package io.github.parkkevinsb.flower.sample.logistics.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.EnumMap;
import java.util.Map;
import java.util.Queue;

/**
 * Shared conveyor state used by long-running Zone Flows.
 *
 * <p>Submitting an order creates a box and puts it in the first Zone queue.
 * Each Zone Flow repeatedly admits one waiting box, processes it, and moves it
 * to the next Zone queue.
 */
@Component
public final class WarehouseConveyor {

    public static final int GOODS_RACK_CAPACITY = 8;
    public static final int TRUCK_CAPACITY = 6;
    private static final Duration DEFAULT_TRUCK_ARRIVAL_DELAY = Duration.ofSeconds(5);

    private final WorkOrderStore store;
    private final Duration truckArrivalDelay;
    private final Map<WarehouseZone, Queue<String>> queues = new EnumMap<>(WarehouseZone.class);
    private final List<String> currentTruckWorkOrderIds = new ArrayList<>();
    private final List<String> lastShippedWorkOrderIds = new ArrayList<>();
    private int truckNo = 1;
    private boolean truckDocked = true;
    private Instant truckArrivalDueAt;

    @Autowired
    public WarehouseConveyor(WorkOrderStore store) {
        this(store, DEFAULT_TRUCK_ARRIVAL_DELAY);
    }

    public WarehouseConveyor(WorkOrderStore store, Duration truckArrivalDelay) {
        this.store = store;
        this.truckArrivalDelay = truckArrivalDelay;
        for (WarehouseZone zone : WarehouseZone.values()) {
            queues.put(zone, new ArrayDeque<>());
        }
    }

    public synchronized WorkOrder submitOrder(String workOrderId) {
        WorkOrder existing = store.find(workOrderId);
        if (existing != null) {
            return existing;
        }
        if (queues.get(WarehouseZone.first()).size() >= GOODS_RACK_CAPACITY) {
            throw new IllegalStateException("Goods rack is full. Maximum capacity is " + GOODS_RACK_CAPACITY);
        }
        WorkOrder created = new WorkOrder(workOrderId, WorkOrderStatus.CREATED, Instant.now());
        store.accept(created);
        enqueue(WarehouseZone.first(), workOrderId);
        return store.find(workOrderId);
    }

    public synchronized String admit(WarehouseZone zone, WorkOrderStatus admittedStatus) {
        refreshTruckArrival();
        if (zone == WarehouseZone.LOADING && !canAcceptLoading()) {
            return null;
        }

        String workOrderId = queues.get(zone).poll();
        if (workOrderId == null) {
            return null;
        }
        store.transition(workOrderId, admittedStatus);
        return workOrderId;
    }

    public synchronized void transition(String workOrderId, WorkOrderStatus status) {
        store.transition(workOrderId, status);
    }

    public synchronized void release(WarehouseZone zone, String workOrderId) {
        store.transition(workOrderId, zone.completedStatus());
        WarehouseZone next = zone.next();
        if (next == null) {
            store.transition(workOrderId, WorkOrderStatus.COMPLETED);
            currentTruckWorkOrderIds.add(workOrderId);
            return;
        }
        enqueue(next, workOrderId);
    }

    public synchronized TruckDockSnapshot truckDock() {
        refreshTruckArrival();
        return snapshot();
    }

    public synchronized TruckDockSnapshot departTruck() {
        refreshTruckArrival();
        if (!truckDocked) {
            throw new IllegalStateException("Truck dock is waiting for the next truck");
        }
        if (currentTruckWorkOrderIds.size() < TRUCK_CAPACITY) {
            throw new IllegalStateException("Truck is not full yet");
        }

        lastShippedWorkOrderIds.clear();
        lastShippedWorkOrderIds.addAll(currentTruckWorkOrderIds);
        for (String workOrderId : currentTruckWorkOrderIds) {
            store.transition(workOrderId, WorkOrderStatus.SHIPPED);
        }
        currentTruckWorkOrderIds.clear();
        truckDocked = false;
        truckArrivalDueAt = Instant.now().plus(truckArrivalDelay);
        return snapshot();
    }

    private void enqueue(WarehouseZone zone, String workOrderId) {
        Queue<String> queue = queues.get(zone);
        if (!queue.contains(workOrderId)) {
            queue.add(workOrderId);
        }
        store.transition(workOrderId, zone.waitingStatus());
    }

    private boolean canAcceptLoading() {
        return truckDocked && currentTruckWorkOrderIds.size() < TRUCK_CAPACITY;
    }

    private void refreshTruckArrival() {
        if (truckDocked || truckArrivalDueAt == null || Instant.now().isBefore(truckArrivalDueAt)) {
            return;
        }

        truckNo += 1;
        truckDocked = true;
        truckArrivalDueAt = null;
        lastShippedWorkOrderIds.clear();
    }

    private TruckDockSnapshot snapshot() {
        return new TruckDockSnapshot(
                truckDocked ? TruckDockStatus.DOCKED : TruckDockStatus.AWAITING_TRUCK,
                truckNo,
                truckNo + 1,
                currentTruckWorkOrderIds.size(),
                TRUCK_CAPACITY,
                canAcceptLoading(),
                truckDocked && currentTruckWorkOrderIds.size() >= TRUCK_CAPACITY,
                truckArrivalDueAt,
                currentTruckWorkOrderIds,
                lastShippedWorkOrderIds);
    }
}
