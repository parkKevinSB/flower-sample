package io.github.parkkevinsb.flower.sample.logistics.domain;

public enum WarehouseZone {
    RACK_ROBOT(
            "rack-robot",
            "rack-robot-zone",
            "Rack robot",
            "pick a box from Goods rack and place it on the conveyor",
            WorkOrderStatus.WAITING_RACK_ROBOT,
            WorkOrderStatus.ROBOT_GRIPPING,
            WorkOrderStatus.ON_CONVEYOR,
            0L),
    INSPECTION(
            "inspection",
            "inspection-zone",
            "Inspection",
            "weight and vision check",
            WorkOrderStatus.WAITING_INSPECTION,
            WorkOrderStatus.INSPECTING,
            WorkOrderStatus.INSPECTED,
            3_500L),
    PACKING(
            "packing",
            "packing-zone",
            "Packing",
            "dunnage, seal, and shipping label",
            WorkOrderStatus.WAITING_PACKING,
            WorkOrderStatus.PACKING,
            WorkOrderStatus.PACKED,
            4_200L),
    SORTATION(
            "sortation",
            "sortation-zone",
            "Sortation",
            "route gate to outbound dock",
            WorkOrderStatus.WAITING_SORTATION,
            WorkOrderStatus.SORTING,
            WorkOrderStatus.SORTED,
            3_200L),
    LOADING(
            "loading",
            "loading-zone",
            "Loading",
            "lift box into the truck",
            WorkOrderStatus.WAITING_LOADING,
            WorkOrderStatus.LOADING_TRUCK,
            WorkOrderStatus.LOADED,
            3_800L);

    private final String id;
    private final String workerName;
    private final String displayName;
    private final String workDescription;
    private final WorkOrderStatus waitingStatus;
    private final WorkOrderStatus processingStatus;
    private final WorkOrderStatus completedStatus;
    private final long processingMillis;

    WarehouseZone(
            String id,
            String workerName,
            String displayName,
            String workDescription,
            WorkOrderStatus waitingStatus,
            WorkOrderStatus processingStatus,
            WorkOrderStatus completedStatus,
            long processingMillis
    ) {
        this.id = id;
        this.workerName = workerName;
        this.displayName = displayName;
        this.workDescription = workDescription;
        this.waitingStatus = waitingStatus;
        this.processingStatus = processingStatus;
        this.completedStatus = completedStatus;
        this.processingMillis = processingMillis;
    }

    public static WarehouseZone first() {
        return RACK_ROBOT;
    }

    public WarehouseZone next() {
        switch (this) {
            case RACK_ROBOT:
                return INSPECTION;
            case INSPECTION:
                return PACKING;
            case PACKING:
                return SORTATION;
            case SORTATION:
                return LOADING;
            case LOADING:
            default:
                return null;
        }
    }

    public String id() {
        return id;
    }

    public String workerName() {
        return workerName;
    }

    public String displayName() {
        return displayName;
    }

    public String workDescription() {
        return workDescription;
    }

    public WorkOrderStatus waitingStatus() {
        return waitingStatus;
    }

    public WorkOrderStatus processingStatus() {
        return processingStatus;
    }

    public WorkOrderStatus completedStatus() {
        return completedStatus;
    }

    public long processingMillis() {
        return processingMillis;
    }
}
