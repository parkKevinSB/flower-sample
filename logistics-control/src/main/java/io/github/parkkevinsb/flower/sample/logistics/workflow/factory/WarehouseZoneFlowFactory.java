package io.github.parkkevinsb.flower.sample.logistics.workflow.factory;

import io.github.parkkevinsb.flower.core.flow.Flow;
import io.github.parkkevinsb.flower.sample.logistics.domain.WarehouseConveyor;
import io.github.parkkevinsb.flower.sample.logistics.domain.WarehouseZone;
import io.github.parkkevinsb.flower.sample.logistics.workflow.step.AwaitRackBoxStep;
import io.github.parkkevinsb.flower.sample.logistics.workflow.step.AwaitZoneBoxStep;
import io.github.parkkevinsb.flower.sample.logistics.workflow.step.GripRackBoxStep;
import io.github.parkkevinsb.flower.sample.logistics.workflow.step.InspectBoxStep;
import io.github.parkkevinsb.flower.sample.logistics.workflow.step.LoadTruckStep;
import io.github.parkkevinsb.flower.sample.logistics.workflow.step.PackBoxStep;
import io.github.parkkevinsb.flower.sample.logistics.workflow.step.PlaceRackBoxOnConveyorStep;
import io.github.parkkevinsb.flower.sample.logistics.workflow.step.ReleaseZoneBoxStep;
import io.github.parkkevinsb.flower.sample.logistics.workflow.step.SortBoxStep;
import io.github.parkkevinsb.flower.sample.logistics.workflow.step.TimedZoneWorkStep;
import io.github.parkkevinsb.flower.sample.logistics.workflow.step.ZoneCycleState;
import org.springframework.stereotype.Component;

@Component
public final class WarehouseZoneFlowFactory {

    public static final String FLOW_TYPE = "warehouse-zone";
    public static final String AWAIT_BOX = "await-box";
    public static final String RELEASE_BOX = "release-box";
    public static final String AWAIT_RACK_BOX = "await-rack-box";
    public static final String GRIP_RACK_BOX = "grip-rack-box";
    public static final String PLACE_ON_CONVEYOR = "place-on-conveyor";
    public static final String INSPECT_BOX = "inspect-box";
    public static final String PACK_BOX = "pack-box";
    public static final String SORT_BOX = "sort-box";
    public static final String LOAD_TRUCK = "load-truck";

    private final WarehouseConveyor conveyor;

    public WarehouseZoneFlowFactory(WarehouseConveyor conveyor) {
        this.conveyor = conveyor;
    }

    public Flow create(WarehouseZone zone) {
        ZoneCycleState cycle = new ZoneCycleState();
        if (zone == WarehouseZone.RACK_ROBOT) {
            return Flow.builder(FLOW_TYPE, zone.id())
                    .step(AWAIT_RACK_BOX, new AwaitRackBoxStep(conveyor, cycle))
                    .step(GRIP_RACK_BOX, new GripRackBoxStep(cycle, AWAIT_RACK_BOX))
                    .step(PLACE_ON_CONVEYOR, new PlaceRackBoxOnConveyorStep(conveyor, cycle, AWAIT_RACK_BOX))
                    .build();
        }

        return Flow.builder(FLOW_TYPE, zone.id())
                .step(AWAIT_BOX, new AwaitZoneBoxStep(conveyor, zone, cycle))
                .step(processStepId(zone), processStep(zone, cycle))
                .step(RELEASE_BOX, new ReleaseZoneBoxStep(conveyor, zone, cycle, AWAIT_BOX))
                .build();
    }

    private String processStepId(WarehouseZone zone) {
        switch (zone) {
            case INSPECTION:
                return INSPECT_BOX;
            case PACKING:
                return PACK_BOX;
            case SORTATION:
                return SORT_BOX;
            case LOADING:
                return LOAD_TRUCK;
            case RACK_ROBOT:
            default:
                throw new IllegalArgumentException("unsupported timed Zone: " + zone);
        }
    }

    private TimedZoneWorkStep processStep(WarehouseZone zone, ZoneCycleState cycle) {
        switch (zone) {
            case INSPECTION:
                return new InspectBoxStep(cycle, AWAIT_BOX);
            case PACKING:
                return new PackBoxStep(cycle, AWAIT_BOX);
            case SORTATION:
                return new SortBoxStep(cycle, AWAIT_BOX);
            case LOADING:
                return new LoadTruckStep(cycle, AWAIT_BOX);
            case RACK_ROBOT:
            default:
                throw new IllegalArgumentException("unsupported timed Zone: " + zone);
        }
    }
}
