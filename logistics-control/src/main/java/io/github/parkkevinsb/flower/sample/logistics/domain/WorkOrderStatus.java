package io.github.parkkevinsb.flower.sample.logistics.domain;

public enum WorkOrderStatus {
    CREATED,
    ACCEPTED,
    WAITING_RACK_ROBOT,
    ROBOT_GRIPPING,
    ROBOT_PLACING,
    ON_CONVEYOR,
    WAITING_INSPECTION,
    INSPECTING,
    INSPECTED,
    WAITING_PACKING,
    PACKING,
    PACKED,
    WAITING_SORTATION,
    SORTING,
    SORTED,
    WAITING_LOADING,
    LOADING_TRUCK,
    LOADED,
    COMPLETED,
    SHIPPED,
    FAILED
}
