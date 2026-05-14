package io.github.parkkevinsb.flower.sample.logistics.domain;

import java.time.Instant;
import java.util.List;

public final class TruckDockSnapshot {

    private final TruckDockStatus status;
    private final int truckNo;
    private final int nextTruckNo;
    private final int loadedCount;
    private final int capacity;
    private final boolean acceptingLoading;
    private final boolean canDepart;
    private final Instant arrivalDueAt;
    private final List<String> loadedWorkOrderIds;
    private final List<String> shippedWorkOrderIds;

    public TruckDockSnapshot(
            TruckDockStatus status,
            int truckNo,
            int nextTruckNo,
            int loadedCount,
            int capacity,
            boolean acceptingLoading,
            boolean canDepart,
            Instant arrivalDueAt,
            List<String> loadedWorkOrderIds,
            List<String> shippedWorkOrderIds
    ) {
        this.status = status;
        this.truckNo = truckNo;
        this.nextTruckNo = nextTruckNo;
        this.loadedCount = loadedCount;
        this.capacity = capacity;
        this.acceptingLoading = acceptingLoading;
        this.canDepart = canDepart;
        this.arrivalDueAt = arrivalDueAt;
        this.loadedWorkOrderIds = List.copyOf(loadedWorkOrderIds);
        this.shippedWorkOrderIds = List.copyOf(shippedWorkOrderIds);
    }

    public TruckDockStatus getStatus() {
        return status;
    }

    public int getTruckNo() {
        return truckNo;
    }

    public int getNextTruckNo() {
        return nextTruckNo;
    }

    public int getLoadedCount() {
        return loadedCount;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isAcceptingLoading() {
        return acceptingLoading;
    }

    public boolean isCanDepart() {
        return canDepart;
    }

    public Instant getArrivalDueAt() {
        return arrivalDueAt;
    }

    public List<String> getLoadedWorkOrderIds() {
        return loadedWorkOrderIds;
    }

    public List<String> getShippedWorkOrderIds() {
        return shippedWorkOrderIds;
    }
}
