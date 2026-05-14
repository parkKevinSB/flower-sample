package io.github.parkkevinsb.flower.sample.logistics.workflow.step;

import io.github.parkkevinsb.flower.sample.logistics.domain.WarehouseZone;

public final class LoadTruckStep extends TimedZoneWorkStep {

    public LoadTruckStep(ZoneCycleState cycle, String awaitStepId) {
        super(WarehouseZone.LOADING, cycle, awaitStepId, "lifts the box into the truck");
    }
}
