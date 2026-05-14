package io.github.parkkevinsb.flower.sample.logistics.api;

import io.github.parkkevinsb.flower.sample.logistics.domain.TruckDockSnapshot;

import java.time.Instant;
import java.util.List;

public final class TruckDockResponse {

    private final String status;
    private final int truckNo;
    private final int nextTruckNo;
    private final int loadedCount;
    private final int capacity;
    private final boolean acceptingLoading;
    private final boolean canDepart;
    private final Instant arrivalDueAt;
    private final List<String> loadedWorkOrderIds;
    private final List<String> shippedWorkOrderIds;

    private TruckDockResponse(
            String status,
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
        this.loadedWorkOrderIds = loadedWorkOrderIds;
        this.shippedWorkOrderIds = shippedWorkOrderIds;
    }

    public static TruckDockResponse of(TruckDockSnapshot snapshot) {
        return new TruckDockResponse(
                snapshot.getStatus().name(),
                snapshot.getTruckNo(),
                snapshot.getNextTruckNo(),
                snapshot.getLoadedCount(),
                snapshot.getCapacity(),
                snapshot.isAcceptingLoading(),
                snapshot.isCanDepart(),
                snapshot.getArrivalDueAt(),
                snapshot.getLoadedWorkOrderIds(),
                snapshot.getShippedWorkOrderIds());
    }

    public String getStatus() {
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
